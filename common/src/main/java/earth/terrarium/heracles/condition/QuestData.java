package earth.terrarium.heracles.condition;

import earth.terrarium.heracles.Heracles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestData extends SavedData {
    private final Map<UUID, QuestProgress> progress = new HashMap<>();

    public QuestData() {
    }

    public QuestData(CompoundTag compoundTag) {
        for (String key : compoundTag.getAllKeys()) {
            progress.put(UUID.fromString(key), new QuestProgress(compoundTag.getList(key, Tag.TAG_LIST)));
        }
    }

    public QuestProgress getProgress(Player player) {
        return progress.computeIfAbsent(player.getUUID(), k -> new QuestProgress());
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        for (Map.Entry<UUID, QuestProgress> entry : progress.entrySet()) {
            compoundTag.put(entry.getKey().toString(), entry.getValue().save());
        }

        return compoundTag;
    }

    public static QuestData getData(MinecraftServer server) {
        return server
                .overworld()
                .getDataStorage()
                .computeIfAbsent(QuestData::new, QuestData::new, Heracles.MOD_ID + ".quests");
    }
}
