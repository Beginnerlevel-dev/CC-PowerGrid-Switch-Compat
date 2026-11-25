package com.bld.ccpgsc;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import org.patryk3211.powergrid.collections.ModdedBlockEntities;
import org.patryk3211.powergrid.electricity.electricswitch.SwitchBlock;

import com.bld.ccpgsc.peripherals.*;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;


@Mod(CCPGSC.ID)
public class CCPGSC {
    public static final String ID = "ccpgsc";
    
    public CCPGSC(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerCapabilities);
    }
    
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            PeripheralCapability.get(),
            ModdedBlockEntities.SWITCH.get(),
            (blockEntity, side) -> new SwitchPeripheral(
                blockEntity, 
                (SwitchBlock) blockEntity.getBlockState().getBlock()
            )
        );
        
        event.registerBlockEntity(
        	PeripheralCapability.get(),
        	ModdedBlockEntities.HV_SWITCH.get(),
        	(blockEntity, side) -> new HvSwitchPeripheral(
        		blockEntity
        	)
        );
    }
}
