package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.HeraclesClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;

public record QuestRewardClaimedPacket(String id, List<Item> items) implements Packet<QuestRewardClaimedPacket> {
    public static final ClientboundPacketType<QuestRewardClaimedPacket> TYPE = new Type();

    @Override
    public PacketType<QuestRewardClaimedPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<QuestRewardClaimedPacket> {
        @Override
        public Class<QuestRewardClaimedPacket> type() {
            return QuestRewardClaimedPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "quest_reward_claimed");
        }

        @Override
        public void encode(QuestRewardClaimedPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id());
            buffer.writeCollection(message.items(), (buf, item) -> buf.writeVarInt(Item.getId(item)));
        }

        @Override
        public QuestRewardClaimedPacket decode(FriendlyByteBuf buffer) {
            return new QuestRewardClaimedPacket(
                buffer.readUtf(),
                buffer.readList(buf -> Item.byId(buf.readVarInt()))
            );
        }

        @Override
        public Runnable handle(QuestRewardClaimedPacket message) {
            return () -> HeraclesClient.displayItemsRewardedToast(message.id, message.items());
        }
    }
}
