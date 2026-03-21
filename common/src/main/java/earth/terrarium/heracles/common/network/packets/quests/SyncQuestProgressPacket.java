package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.quest.AbstractQuestScreen;
import earth.terrarium.heracles.client.ui.quest.TasksQuestScreen;
import earth.terrarium.heracles.client.ui.quests.AbstractQuestsScreen;
import earth.terrarium.heracles.client.ui.quests.QuestsScreen;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.rmi.registry.Registry;
import java.util.LinkedHashMap;
import java.util.Map;

public record SyncQuestProgressPacket(Map<String, QuestProgress> quests) implements Packet<SyncQuestProgressPacket> {

    public static final ClientboundPacketType<SyncQuestProgressPacket> TYPE = new Type();

    @Override
    public PacketType<SyncQuestProgressPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<SyncQuestProgressPacket> {

        @Override
        public Class<SyncQuestProgressPacket> type() {
            return SyncQuestProgressPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return Heracles.id("sync_quest_progress");
        }

        @Override
        public void encode(SyncQuestProgressPacket message, RegistryFriendlyByteBuf buffer) {
            buffer.writeVarInt(message.quests.size());
            for (var entry : message.quests.entrySet()) {
                buffer.writeUtf(entry.getKey());
                buffer.writeNbt(entry.getValue().save());
            }
        }

        @Override
        public SyncQuestProgressPacket decode(RegistryFriendlyByteBuf buffer) {
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
        public Runnable handle(SyncQuestProgressPacket message) {
            return () -> {
                ClientQuests.mergeProgress(message.quests);
                if(Minecraft.getInstance().screen instanceof AbstractQuestScreen screen) {
                    screen.updateProgress();
                }
            };
        }
    }
}
