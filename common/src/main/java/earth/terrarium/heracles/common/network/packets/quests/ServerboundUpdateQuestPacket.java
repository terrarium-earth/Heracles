package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.function.Consumer;

public record ServerboundUpdateQuestPacket(
    String id, NetworkQuestData data, boolean sendToSelf
) implements Packet<ServerboundUpdateQuestPacket> {

    public static final ServerboundPacketType<ServerboundUpdateQuestPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundUpdateQuestPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ServerboundUpdateQuestPacket> implements ServerboundPacketType<ServerboundUpdateQuestPacket>{

        private static final ByteCodec<ServerboundUpdateQuestPacket> CODEC = ObjectByteCodec.create(
            ByteCodec.STRING.fieldOf(ServerboundUpdateQuestPacket::id),
            NetworkQuestData.CODEC.fieldOf(ServerboundUpdateQuestPacket::data),
            ByteCodec.BOOLEAN.fieldOf(ServerboundUpdateQuestPacket::sendToSelf),
            ServerboundUpdateQuestPacket::new
        );

        public Type() {
            super(Heracles.id("update_server_quest"), CODEC);
        }

        @Override
        public Consumer<Player> handle(ServerboundUpdateQuestPacket message) {
            return (player) -> {
                if (player.hasPermissions(2)) {
                    Quest quest = QuestHandler.get(message.id);
                    if (quest == null) return;
                    message.data.update(quest);
                    QuestHandler.markDirty(message.id);

                    ClientboundUpdateQuestPacket packet = new ClientboundUpdateQuestPacket(message.id, message.data);

                    Objects.requireNonNull(player.getServer())
                        .getPlayerList()
                        .getPlayers()
                        .stream()
                        .filter(p -> p != player || message.sendToSelf())
                        .forEach(p -> NetworkHandler.CHANNEL.sendToPlayer(packet, p));

                    QuestProgressHandler.read(player.getServer()).updatePossibleQuests();
                }
            };
        }
    }
}
