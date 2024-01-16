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
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.function.Consumer;

public record ServerboundAddQuestPacket(
    String id, Quest quest
) implements Packet<ServerboundAddQuestPacket> {

    public static final ServerboundPacketType<ServerboundAddQuestPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundAddQuestPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ServerboundAddQuestPacket>, CodecPacketType<ServerboundAddQuestPacket> {

        private static final ByteCodec<ServerboundAddQuestPacket> CODEC = ObjectByteCodec.create(
            ByteCodec.STRING.fieldOf(ServerboundAddQuestPacket::id),
            ModUtils.toByteCodec(Quest.CODEC).fieldOf(ServerboundAddQuestPacket::quest),
            ServerboundAddQuestPacket::new
        );

        @Override
        public Class<ServerboundAddQuestPacket> type() {
            return ServerboundAddQuestPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "add_server_quest");
        }

        @Override
        public ByteCodec<ServerboundAddQuestPacket> codec() {
            return CODEC;
        }

        @Override
        public Consumer<Player> handle(ServerboundAddQuestPacket message) {
            return (player) -> {
                if (player.hasPermissions(2)) {
                    QuestHandler.upload(message.id(), message.quest());
                    NetworkHandler.CHANNEL.sendToAllPlayers(
                        new ClientboundAddQuestPacket(message.id(), message.quest()),
                        Objects.requireNonNull(player.getServer())
                    );
                    QuestProgressHandler.read(player.getServer()).updatePossibleQuests();
                }
            };
        }
    }
}
