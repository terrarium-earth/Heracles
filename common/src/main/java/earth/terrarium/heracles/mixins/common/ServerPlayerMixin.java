package earth.terrarium.heracles.mixins.common;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.api.tasks.defaults.RecipeTask;
import earth.terrarium.heracles.api.tasks.defaults.StatTask;
import earth.terrarium.heracles.api.tasks.defaults.XpTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Shadow
    @Final
    public MinecraftServer server;

    @Shadow
    @Final
    private ServerStatsCounter stats;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "triggerRecipeCrafted", at = @At("HEAD"))
    private void heracles$triggerRecipeCrafted(Recipe<?> recipe, List<ItemStack> items, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        QuestsProgress progress = QuestProgressHandler.getProgress(server, player.getUUID());
        progress.testAndProgressTaskType(player, recipe, RecipeTask.TYPE);
    }

    @Inject(
        method = "onItemPickup",
        at = @At("HEAD")
    )
    public void heracles$onItemPickup(ItemEntity itemEntity, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        QuestsProgress progress = QuestProgressHandler.getProgress(server, player.getUUID());
        progress.testAndProgressTaskType(player, Pair.of(Optional.of(itemEntity.getItem()), player.getInventory()), GatherItemTask.TYPE);
    }

    @Inject(
        method = "doTick",
        at = @At(
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
            value = "INVOKE",
            ordinal = 2
        )
    )
    public void heracles$doTick(CallbackInfo ci) {
        if (!QuestHandler.isTaskUsed(XpTask.TYPE)) return;
        ServerPlayer player = (ServerPlayer) (Object) this;
        QuestsProgress progress = QuestProgressHandler.getProgress(server, player.getUUID());
        progress.testAndProgressTaskType(player, Pair.of(player, XpTask.Cause.GAINED_XP), XpTask.TYPE);
    }

    @Inject(
        method = "awardStat",
        at = @At("TAIL")
    )
    public void heracles$awardStat(Stat<?> stat, int increment, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (stat.getValue() instanceof ResourceLocation id && StatTask.hasStat(id)) {
            QuestsProgress progress = QuestProgressHandler.getProgress(server, player.getUUID());
            progress.testAndProgressTaskType(player, new Pair<>(id, this.stats.getValue(stat)), StatTask.TYPE);
        }
    }

}
