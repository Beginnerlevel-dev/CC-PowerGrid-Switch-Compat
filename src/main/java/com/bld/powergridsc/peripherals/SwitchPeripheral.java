package com.bld.powergridsc.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.patryk3211.powergrid.collections.ModdedBlocks;
import org.patryk3211.powergrid.electricity.electricswitch.SwitchBlock;
import org.patryk3211.powergrid.electricity.electricswitch.SwitchBlockEntity;
import org.jetbrains.annotations.Nullable;

public class SwitchPeripheral implements IPeripheral {
	private final SwitchBlockEntity electric_switch;
	private final SwitchBlock switch_block;
	
	public SwitchPeripheral(SwitchBlockEntity electric_switch,SwitchBlock switch_block) {
		this.electric_switch = electric_switch;
		this.switch_block = switch_block;
	}
	
	private String determineVariant() {
		var level = electric_switch.getLevel();
		if (level == null) return "switch";
		
		var pos = electric_switch.getBlockPos();
		var block = level.getBlockState(pos).getBlock();
		
		if (block == ModdedBlocks.LV_SWITCH.get()) {
			return "lv_switch";
		} else if (block == ModdedBlocks.MV_SWITCH.get()) {
			return "mv_switch";
		} else if (block == ModdedBlocks.LV_BUTTON.get()) {
			return "lv_button";
		}
		
		return "switch";
	}
	
	@Override
	public String getType() {
		return determineVariant();
	}
	
	@LuaFunction
	public boolean getState() {
		var level = electric_switch.getLevel();
		var pos = electric_switch.getBlockPos();
		var state = level.getBlockState(pos);
		return !state.getValue(SwitchBlock.OPEN);
	}
	
	@LuaFunction
	public void setState(boolean state) {
		electric_switch.setState(state);
		
		var level = electric_switch.getLevel();
        if (level != null && !level.isClientSide) {
            var pos = electric_switch.getBlockPos();
            var current = level.getBlockState(pos);
            boolean desiredOpen = !state;
            if (current.getValue(SwitchBlock.OPEN) != desiredOpen) {
                level.setBlockAndUpdate(pos, current.setValue(SwitchBlock.OPEN, desiredOpen));
                switch_block.useSound(level,pos,desiredOpen);
            }
        }
	}
	
	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof SwitchPeripheral o && electric_switch == o.electric_switch && switch_block == o.switch_block;
	}
}
