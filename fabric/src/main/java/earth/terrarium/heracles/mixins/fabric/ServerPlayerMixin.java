package earth.terrarium.heracles.mixins.fabric;

import com.mojang.authlib.GameProfile;
import earth.terrarium.heracles.api.tasks.defaults.BiomeTask;
import earth.terrarium.heracles.api.tasks.defaults.ItemUseTask;
import earth.terrarium.heracles.api.tasks.defaults.StructureTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @Shadow
    @Final
    public MinecraftServer server;

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "completeUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;completeUsingItem()V"))
    private void heracles$completeUsingItem(CallbackInfo ci) {
        QuestProgressHandler.getProgress(server, getUUID())
            .testAndProgressTaskType((ServerPlayer) (Object) this, useItem, ItemUseTask.TYPE);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void heracles$tick(CallbackInfo ci) {
        if (tickCount % 20 == 0) {
            ServerPlayer player = (ServerPlayer) (Object) this;

            QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
            Map<Structure, LongSet> structures = player.getLevel().structureManager().getAllStructuresAt(player.getOnPos());

            progress.testAndProgressTaskType(player, player.level.getBiome(player.getOnPos()), BiomeTask.TYPE);

            if (!structures.isEmpty()) {
                progress.testAndProgressTaskType(player, structures.keySet(), StructureTask.TYPE);
            }
        }
    }
}
