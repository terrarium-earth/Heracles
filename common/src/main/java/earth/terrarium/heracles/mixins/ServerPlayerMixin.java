package earth.terrarium.heracles.mixins;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.api.tasks.defaults.RecipeTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Shadow
    @Final
    public MinecraftServer server;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "awardRecipes", at = @At("HEAD"))
    private void heracles$awardRecipes(Collection<Recipe<?>> recipes, CallbackInfoReturnable<Integer> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        QuestsProgress progress = QuestProgressHandler.getProgress(server, player.getUUID());

        for (Recipe<?> recipe : recipes) {
            progress.testAndProgressTaskType(player, recipe, RecipeTask.TYPE);
        }
    }

    @Inject(
        method = "onItemPickup",
        at = @At("HEAD")
    )
    public void heracles$onItemPickup(ItemEntity itemEntity, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        QuestsProgress progress = QuestProgressHandler.getProgress(server, player.getUUID());
        progress.testAndProgressTaskType(player, Pair.of(itemEntity.getItem(), player.getInventory()), GatherItemTask.TYPE);
    }
}
