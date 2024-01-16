package earth.terrarium.heracles.common.network.packets.groups;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record DeleteGroupPacket(String group) implements Packet<DeleteGroupPacket> {
    public static final ServerboundPacketType<DeleteGroupPacket> TYPE = new Type();

    @Override
    public PacketType<DeleteGroupPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<DeleteGroupPacket> {

        @Override
        public Class<DeleteGroupPacket> type() {
            return DeleteGroupPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "delete_group");
        }

        @Override
        public void encode(DeleteGroupPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.group);
        }

        @Override
        public DeleteGroupPacket decode(FriendlyByteBuf buffer) {
            return new DeleteGroupPacket(buffer.readUtf());
        }

        @Override
        public Consumer<Player> handle(DeleteGroupPacket message) {
            return (player) -> {
                if (player.hasPermissions(2) && QuestHandler.groups().containsKey(message.group)) {
                    QuestHandler.groups().remove(message.group);
                    QuestHandler.saveGroups();
                }
            };
        }
    }
}
