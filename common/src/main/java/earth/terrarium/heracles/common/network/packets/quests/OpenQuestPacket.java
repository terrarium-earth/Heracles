package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record OpenQuestPacket(String group, String quest) implements Packet<OpenQuestPacket> {

    public static final ServerboundPacketType<OpenQuestPacket> TYPE = new Type();

    @Override
    public PacketType<OpenQuestPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<OpenQuestPacket> {

        @Override
        public Class<OpenQuestPacket> type() {
            return OpenQuestPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "open_quest");
        }

        @Override
        public void encode(OpenQuestPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.group);
            buffer.writeUtf(message.quest);
        }

        @Override
        public OpenQuestPacket decode(FriendlyByteBuf buffer) {
            return new OpenQuestPacket(buffer.readUtf(), buffer.readUtf());
        }

        @Override
        public Consumer<Player> handle(OpenQuestPacket message) {
            return (player) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    ModUtils.openQuest(serverPlayer, message.group(), message.quest());
                }
            };
        }
    }
}
