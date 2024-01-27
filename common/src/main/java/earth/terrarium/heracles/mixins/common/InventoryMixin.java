package earth.terrarium.heracles.mixins.common;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.common.items.SlotChangeAwareInventory;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Inventory.class)
public abstract class InventoryMixin implements SlotChangeAwareInventory {
    @Unique int heracles$changedSlot = -1;
    @Unique ItemStack heracles$addedStack = null;

    @Shadow @Final public Player player;

    @Shadow public abstract ItemStack getItem(int slot);

    @Unique private void heracles$testProgress(ItemStack stack) {
        if (this.player instanceof ServerPlayer serverPlayer) {
            QuestsProgress progress = QuestProgressHandler.getProgress(serverPlayer.getServer(), serverPlayer.getUUID());
            progress.testAndProgressTaskType(serverPlayer, Pair.of(Optional.of(stack), serverPlayer.getInventory()), GatherItemTask.TYPE);
        }
    }

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"))
    private void heracles$addItemStack(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        heracles$addedStack = stack.copy();
    }

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
    private void heracles$addItemTest(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (heracles$addedStack != null) heracles$testProgress(heracles$addedStack);
        heracles$addedStack = null;
    }

    @Inject(method = "setChanged", at = @At("RETURN"))
    private void heracles$setChanged(CallbackInfo ci) {
        if (heracles$changedSlot != -1) heracles$testProgress(this.getItem(heracles$changedSlot));
    }

    @Override
    public void heracles$setChangedSlot(int slot) {
        heracles$changedSlot = slot;
    }
}
