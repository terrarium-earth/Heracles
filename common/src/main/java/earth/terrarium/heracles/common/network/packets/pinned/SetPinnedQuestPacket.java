package earth.terrarium.heracles.common.network.packets.pinned;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.pinned.PinnedQuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.function.Consumer;

public record SetPinnedQuestPacket(String quest, boolean value) implements Packet<SetPinnedQuestPacket> {

    public static final ServerboundPacketType<SetPinnedQuestPacket> TYPE = new Type();

    @Override
    public PacketType<SetPinnedQuestPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<SetPinnedQuestPacket> {

        @Override
        public Class<SetPinnedQuestPacket> type() {
            return SetPinnedQuestPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "set_pinned_quest");
        }

        @Override
        public void encode(SetPinnedQuestPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.quest);
            buffer.writeBoolean(message.value);
        }

        @Override
        public SetPinnedQuestPacket decode(FriendlyByteBuf buffer) {
            return new SetPinnedQuestPacket(buffer.readUtf(), buffer.readBoolean());
        }

        @Override
        public Consumer<Player> handle(SetPinnedQuestPacket message) {
            return (player) -> {
                Set<String> quests = PinnedQuestHandler.getPinned((ServerPlayer) player);
                if (message.value) {
                    quests.add(message.quest);
                } else {
                    quests.remove(message.quest);
                }
                PinnedQuestHandler.sync((ServerPlayer) player);
            };
        }
    }
}
