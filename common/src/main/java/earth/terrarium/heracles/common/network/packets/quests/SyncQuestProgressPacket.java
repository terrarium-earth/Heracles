package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.quest.BaseQuestScreen;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public record SyncQuestProgressPacket(Map<String, QuestProgress> quests) implements Packet<SyncQuestProgressPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "sync_quest_progress");
    public static final PacketHandler<SyncQuestProgressPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SyncQuestProgressPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<SyncQuestProgressPacket> {

        @Override
        public void encode(SyncQuestProgressPacket message, FriendlyByteBuf buffer) {
            buffer.writeVarInt(message.quests.size());
            for (var entry : message.quests.entrySet()) {
                buffer.writeUtf(entry.getKey());
                buffer.writeNbt(entry.getValue().save());
            }
        }

        @Override
        public SyncQuestProgressPacket decode(FriendlyByteBuf buffer) {
            Map<String, QuestProgress> quests = new LinkedHashMap<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                String quest = buffer.readUtf();
                ClientQuests.get(quest).ifPresent(entry -> {
                    QuestProgress progress = new QuestProgress(entry.value(), buffer.readNbt());
                    quests.put(quest, progress);
                });
            }
            return new SyncQuestProgressPacket(quests);
        }

        @Override
        public PacketContext handle(SyncQuestProgressPacket message) {
            return (player, level) -> {
                ClientQuests.mergeProgress(message.quests);
                if (Minecraft.getInstance().screen instanceof BaseQuestScreen screen) {
                    screen.updateProgress(message.quests.getOrDefault(screen.getQuestId(), null));
                }
            };
        }
    }
}
