package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Consumer;

public record ServerboundUpdateGroupOrderPacket(
    List<String> order
) implements Packet<ServerboundUpdateGroupOrderPacket> {

    public static final ServerboundPacketType<ServerboundUpdateGroupOrderPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundUpdateGroupOrderPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ServerboundUpdateGroupOrderPacket> {

        @Override
        public Class<ServerboundUpdateGroupOrderPacket> type() {
            return ServerboundUpdateGroupOrderPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "update_group_order");
        }

        @Override
        public void encode(ServerboundUpdateGroupOrderPacket message, FriendlyByteBuf buffer) {
            buffer.writeCollection(message.order, FriendlyByteBuf::writeUtf);
        }

        @Override
        public ServerboundUpdateGroupOrderPacket decode(FriendlyByteBuf buffer) {
            return new ServerboundUpdateGroupOrderPacket(buffer.readList(FriendlyByteBuf::readUtf));
        }

        @Override
        public Consumer<Player> handle(ServerboundUpdateGroupOrderPacket message) {
            return (player) -> {
                if (player.hasPermissions(2)) {
                    QuestHandler.groupsOrder().clear();
                    QuestHandler.groupsOrder().addAll(message.order);
                    QuestHandler.saveGroups();
                    NetworkHandler.CHANNEL.sendToAllPlayers(new SyncGroupOrderPacket(QuestHandler.groupsOrder()), player.getServer());
                }
            };
        }
    }
}
