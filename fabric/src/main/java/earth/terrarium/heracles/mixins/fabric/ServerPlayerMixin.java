package earth.terrarium.heracles.mixins.fabric;

import com.mojang.authlib.GameProfile;
import earth.terrarium.heracles.api.tasks.defaults.BiomeTask;
import earth.terrarium.heracles.api.tasks.defaults.ItemUseTask;
import earth.terrarium.heracles.api.tasks.defaults.LocationTask;
import earth.terrarium.heracles.api.tasks.defaults.StructureTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

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

    @SuppressWarnings("UnreachableCode")
    @Inject(method = "tick", at = @At("TAIL"))
    private void heracles$tick(CallbackInfo ci) {
        if (tickCount % 20 != 0) return;

        var player = (ServerPlayer) (Object) this;

        var progress = QuestProgressHandler.getProgress(player.server, player.getUUID());

        progress.testAndProgressTaskType(player, player.level().getBiome(player.getOnPos()), BiomeTask.TYPE);
        progress.testAndProgressTaskType(player, player, LocationTask.TYPE);

        /*
         * https://github.com/sisby-folk/surveyor/blob/a81c2b05827a7298b81a26de4e00117965017ccd/src/main/java/folk/sisby/surveyor/Surveyor.java#L42-L70
         */
        var structureManager = player.serverLevel().structureManager();
        var blockPos = player.blockPosition();
        var structures = structureManager.getAllStructuresAt(blockPos);
        var result = new HashSet<Structure>();
        if (!structures.isEmpty()) {
            for (var entry : structures.entrySet()) {
                var structureStart = structureManager.getStructureAt(blockPos, entry.getKey());
                if (structureStart != StructureStart.INVALID_START &&
                    structureStart.getBoundingBox().isInside(blockPos) &&
                    structureStart.getPieces().stream().anyMatch(it -> it.getBoundingBox().isInside(blockPos))) {
                    result.add(entry.getKey());
                }
            }
            progress.testAndProgressTaskType(player, result, StructureTask.TYPE);
        }

    }
}
