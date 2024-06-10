package earth.terrarium.heracles.mixins.client;

import earth.terrarium.heracles.common.regisitries.ModBlocks;
import earth.terrarium.heracles.common.regisitries.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "getMarkerParticleTarget", at = @At("TAIL"), cancellable = true)
    private void getMarkerParticleTarget(CallbackInfoReturnable<Block> cir) {
        if (this.minecraft.player == null) return;
        if (this.minecraft.gameMode == null) return;
        if (this.minecraft.gameMode.getPlayerMode() != GameType.CREATIVE) return;
        if (ModItems.BARRIER == null) return;
        ItemStack itemStack = this.minecraft.player.getMainHandItem();
        if (itemStack.is(ModItems.BARRIER.get())) {
            cir.setReturnValue(ModBlocks.BARRIER_BLOCK.get());
        }
    }
}
