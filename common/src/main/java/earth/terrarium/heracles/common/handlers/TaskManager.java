package earth.terrarium.heracles.common.handlers;

import earth.terrarium.heracles.api.tasks.defaults.BiomeTask;
import earth.terrarium.heracles.api.tasks.defaults.LocationTask;
import earth.terrarium.heracles.api.tasks.defaults.StructureTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.HashMap;
import java.util.Map;

public class TaskManager {

    public static void onPlayerTick(ServerPlayer player) {
        if ((player.tickCount + player.getId()) % 20 != 0) return;

        QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
        checkLocation(progress, player);
        checkStructures(progress, player);
    }

    private static void checkLocation(QuestsProgress progress, ServerPlayer player) {
        progress.testAndProgressTaskType(player, player.level().getBiome(player.getOnPos()), BiomeTask.TYPE);
        progress.testAndProgressTaskType(player, player, LocationTask.TYPE);
    }

    private static void checkStructures(QuestsProgress progress, ServerPlayer player) {
        if (!QuestHandler.isTaskUsed(StructureTask.TYPE)) return;
        StructureTask.Cache cache = QuestHandler.getTaskCache(StructureTask.TYPE);
        if (cache == null) return;
        var structures = player.serverLevel().structureManager().getAllStructuresAt(player.getOnPos());
        if (structures.isEmpty()) return;
        checkStructuresAccurately(cache, progress, player, structures);
        checkStructuresInaccurately(cache, progress, player, structures);
    }

    private static void checkStructuresAccurately(StructureTask.Cache cache, QuestsProgress progress, ServerPlayer player, Map<Structure, LongSet> structures) {
        if (!cache.accurate().isEmpty()) return;
        structures = new HashMap<>(structures);
        var registry = player.server.registryAccess().registry(Registries.STRUCTURE).orElse(null);
        if (registry != null) {
            structures.entrySet().removeIf(entry -> {
                ResourceKey<Structure> key = registry.getResourceKey(entry.getKey()).orElse(null);
                if (key == null) return true;
                return cache.accurate().stream().noneMatch(holder -> holder.is(key));
            });
        }

        ServerLevel level = player.serverLevel();

        structures.entrySet().removeIf(entry -> {
            for (Long pos : entry.getValue()) {
                int x = ChunkPos.getX(pos);
                int z = ChunkPos.getZ(pos);
                StructureStart start = level.getChunk(x, z, ChunkStatus.STRUCTURE_STARTS).getStartForStructure(entry.getKey());
                if (start == null) continue;
                if (start.getBoundingBox().isInside(player.blockPosition())) {
                    for (StructurePiece piece : start.getPieces()) {
                        if (piece.getBoundingBox().isInside(player.blockPosition())) {
                            return false;
                        }
                    }
                }
            }
            return true;
        });

        progress.testAndProgressTaskType(player, BooleanObjectPair.of(true, structures.keySet()), StructureTask.TYPE);
    }

    private static void checkStructuresInaccurately(StructureTask.Cache cache, QuestsProgress progress, ServerPlayer player, Map<Structure, LongSet> structures) {
        if (!cache.inaccurate().isEmpty()) return;
        progress.testAndProgressTaskType(player, BooleanObjectPair.of(false, structures.keySet()), StructureTask.TYPE);
    }
}
