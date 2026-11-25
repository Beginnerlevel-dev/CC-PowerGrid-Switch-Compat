package com.bld.powergridsc;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import org.patryk3211.powergrid.collections.ModdedBlockEntities;
import org.patryk3211.powergrid.electricity.electricswitch.SwitchBlock;

import com.bld.powergridsc.peripherals.*;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;


@Mod(PowerGridSC.ID)
public class PowerGridSC {
    public static final String ID = "powergridsc";
    
    public PowerGridSC(IEventBus modEventBus, ModContainer modContainer) {
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
