package com.bld.powergridsc.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;
import org.patryk3211.powergrid.electricity.electricswitch.HvSwitchBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.bld.powergridsc.access.IHvSwitchAccess;

public class HvSwitchPeripheral implements IPeripheral {
    private final HvSwitchBlockEntity hvSwitch;

    public HvSwitchPeripheral(HvSwitchBlockEntity hvSwitch) {
        this.hvSwitch = hvSwitch;
    }

    @Override
    public String getType() {
        return "hv_switch";
    }

    @LuaFunction
    public final boolean getState() {
        return hvSwitch.isClosed();
    }

    @LuaFunction
	public final void setState(boolean closed) {
    	((IHvSwitchAccess) (Object) hvSwitch).powergridsc$setInstantState(closed);
	}

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof HvSwitchPeripheral o && hvSwitch == o.hvSwitch;
    }
}
