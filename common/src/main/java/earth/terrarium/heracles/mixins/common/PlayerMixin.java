package earth.terrarium.heracles.mixins.common;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
        method = "addItem",
        at = @At("HEAD")
    )
    public void heracles$onItemPickup(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        if (player instanceof ServerPlayer serverPlayer) {
            QuestsProgress progress = QuestProgressHandler.getProgress(serverPlayer.getServer(), serverPlayer.getUUID());
            progress.testAndProgressTaskType(serverPlayer, Pair.of(Optional.of(stack), serverPlayer.getInventory()), GatherItemTask.TYPE);
        }
    }
}
