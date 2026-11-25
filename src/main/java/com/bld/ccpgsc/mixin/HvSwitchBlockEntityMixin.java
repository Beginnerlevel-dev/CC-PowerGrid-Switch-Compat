package com.bld.ccpgsc.mixin;

import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.sounds.SoundSource;
import org.patryk3211.powergrid.collections.ModdedSoundEvents;
import org.patryk3211.powergrid.electricity.electricswitch.HvSwitchBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import com.bld.ccpgsc.access.IHvSwitchAccess;

@Mixin(HvSwitchBlockEntity.class)
public abstract class HvSwitchBlockEntityMixin implements IHvSwitchAccess {

    @Shadow
    protected LerpedFloat rod;

    @Shadow
    public abstract boolean isClosed();

    @Override
	@Unique
	public void ccpgsc$setInstantState(boolean closed) {
    	HvSwitchBlockEntity self = (HvSwitchBlockEntity) (Object) this;
    	var level = self.getLevel();
    	if (level == null || level.isClientSide) return;
	
    	float target = closed ? 1f : 0f;
    	boolean wasClosed = isClosed();
	
    	// Snap rod
    	rod.setValue(target);
    	rod.chase(target, 0f, LerpedFloat.Chaser.LINEAR);
    	System.out.println("Rod set to: " + rod.getValue() + " isClosed: " + isClosed());
	
    	// Force circuit / wire update (same logic as initialize())
    	try {
        	var ebField = HvSwitchBlockEntity.class.getSuperclass().getDeclaredField("electricBehaviour");
        	ebField.setAccessible(true);
        	var electricBehaviour = ebField.get(self);
	
        	var wireField = HvSwitchBlockEntity.class.getDeclaredField("wire");
        	wireField.setAccessible(true);
        	var wire = wireField.get(self);
	
        	if (wire == null && isClosed()) {
            	// Rebuild circuit so the wire is created
            	var rebuild = electricBehaviour.getClass().getMethod("rebuildCircuit", boolean.class);
            	rebuild.invoke(electricBehaviour, false);
            	wire = wireField.get(self); // re-get after rebuild
        	}
	
        	if (wire != null) {
    			var setState = wire.getClass().getMethod("setState", boolean.class);
    			var setResistance = wire.getClass().getMethod("setResistance", double.class);
			
    			setState.invoke(wire, false);
    			setResistance.invoke(wire, (double) self.getResistance());
    			setState.invoke(wire, isClosed());
			}
    	} catch (Exception e) {
        	e.printStackTrace();
    	}
	
    	// Sound
    	if (closed != wasClosed) {
        	var sound = closed
                	? ModdedSoundEvents.HV_SWITCH_CONNECT.getMainEvent()
                	: ModdedSoundEvents.HV_SWITCH_DISCONNECT.getMainEvent();
        	level.playSound(null, self.getBlockPos(), sound, SoundSource.BLOCKS, 1f, 1f);
    	}
	
		self.setChanged();
		
		var pos = self.getBlockPos();
		var state = self.getBlockState();
		level.sendBlockUpdated(pos, state, state, 3);
		
		if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
    		var packet = self.getUpdatePacket();
    		if (packet != null) {
        		serverLevel.getChunkSource().chunkMap
                		.getPlayers(new net.minecraft.world.level.ChunkPos(pos), false)
                		.forEach(player -> player.connection.send(packet));
                rod.tickChaser();
    		}
		}
}
}
