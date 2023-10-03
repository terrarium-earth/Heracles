package earth.terrarium.heracles.mixins.common;

import earth.terrarium.heracles.common.duck.SlotChangeAwareInventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public class SlotMixin {
    @Shadow @Final private int slot;
    @Shadow @Final public Container container;

    @Inject(method = "setChanged", at = @At("HEAD"))
    public void provideSlotContext(CallbackInfo ci) {
        if (this.container instanceof SlotChangeAwareInventory awareInventory) awareInventory.heracles$setChangedSlot(this.slot);
    }

    @Inject(method = "setChanged", at = @At("TAIL"))
    public void clearSlotContext(CallbackInfo ci) {
        if (this.container instanceof SlotChangeAwareInventory awareInventory) awareInventory.heracles$setChangedSlot(-1);
    }
}
