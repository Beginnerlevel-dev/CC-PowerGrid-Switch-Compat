package com.bld.ccpgsc.mixin;

import org.patryk3211.powergrid.electricity.electricswitch.HvSwitchVisual;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HvSwitchVisual.class)
public abstract class HvSwitchVisualMixin {
    @Shadow
    protected boolean settled;

    @Inject(method = "beginFrame", at = @At("HEAD"), cancellable = true)
    private void forceUpdate(CallbackInfo ci) {
        // Always allow a refresh if the rod is not settled
        // (our instant set makes the rod unsettled on the client after the packet)
        settled = false;
    }
}
