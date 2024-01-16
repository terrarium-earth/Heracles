package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.function.Consumer;

public record ServerboundRemoveQuestPacket(String id) implements Packet<ServerboundRemoveQuestPacket> {

    public static final ServerboundPacketType<ServerboundRemoveQuestPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundRemoveQuestPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ServerboundRemoveQuestPacket>, CodecPacketType<ServerboundRemoveQuestPacket> {

        private static final ByteCodec<ServerboundRemoveQuestPacket> CODEC = ByteCodec.STRING.map(ServerboundRemoveQuestPacket::new, ServerboundRemoveQuestPacket::id);

        @Override
        public Class<ServerboundRemoveQuestPacket> type() {
            return ServerboundRemoveQuestPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "remove_server_quest");
        }

        @Override
        public ByteCodec<ServerboundRemoveQuestPacket> codec() {
            return CODEC;
        }

        @Override
        public Consumer<Player> handle(ServerboundRemoveQuestPacket message) {
            return (player) -> {
                if (player.hasPermissions(2)) {
                    QuestHandler.remove(message.id);
                    NetworkHandler.CHANNEL.sendToAllPlayers(
                        new ClientboundRemoveQuestPacket(message.id()),
                        Objects.requireNonNull(player.getServer())
                    );
                    QuestProgressHandler.read(player.getServer()).updatePossibleQuests();
                }
            };
        }
    }
}
