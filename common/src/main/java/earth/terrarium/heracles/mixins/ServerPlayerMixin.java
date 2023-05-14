package earth.terrarium.heracles.mixins;

import com.mojang.authlib.GameProfile;
import earth.terrarium.heracles.api.tasks.defaults.RecipeTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @Shadow @Final public MinecraftServer server;

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "awardRecipes", at = @At("HEAD"))
    private void heracles$awardRecipes(Collection<Recipe<?>> recipes, CallbackInfoReturnable<Integer> cir) {
        QuestsProgress progress = QuestProgressHandler.getProgress(server, getUUID());

        for (Recipe<?> recipe : recipes) {
            progress.testAndProgressTaskType((ServerPlayer) (Object) this, recipe, RecipeTask.TYPE);
        }
    }
}
