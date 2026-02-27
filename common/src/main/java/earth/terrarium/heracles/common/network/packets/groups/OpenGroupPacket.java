package earth.terrarium.heracles.common.network.packets.groups;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record OpenGroupPacket(String group) implements Packet<OpenGroupPacket> {
    public static final ServerboundPacketType<OpenGroupPacket> TYPE = new Type();

    @Override
    public PacketType<OpenGroupPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<OpenGroupPacket> {

        @Override
        public Class<OpenGroupPacket> type() {
            return OpenGroupPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "open_group");
        }

        @Override
        public void encode(OpenGroupPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.group);
        }

        @Override
        public OpenGroupPacket decode(FriendlyByteBuf buffer) {
            return new OpenGroupPacket(buffer.readUtf());
        }

        @Override
        public Consumer<Player> handle(OpenGroupPacket message) {
            return (player) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    String group = message.group;
                    if (group.isEmpty()) {
                        group = QuestHandler.groups().get(0);
                    }
                    ModUtils.openGroup(serverPlayer, group);
                }
            };
        }
    }
}
