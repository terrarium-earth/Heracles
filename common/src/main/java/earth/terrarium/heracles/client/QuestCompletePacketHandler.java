package earth.terrarium.heracles.client;

import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.QuestCompletePacket;
import earth.terrarium.heracles.resource.QuestManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;

public class QuestCompletePacketHandler implements PacketHandler<QuestCompletePacket> {
    @Override
    public void encode(QuestCompletePacket message, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(QuestManager.getInstance().getQuests().inverse().get(message.quest()));
        buffer.writeCollection(message.items(), (buf, item) -> buf.writeVarInt(Item.getId(item)));
    }

    @Override
    public QuestCompletePacket decode(FriendlyByteBuf buffer) {
        return new QuestCompletePacket(
                QuestManager.getInstance().getQuests().get(buffer.readResourceLocation()),
                buffer.readList(buf -> Item.byId(buf.readVarInt()))
        );
    }

    @Override
    public PacketContext handle(QuestCompletePacket message) {
        return (player, level) -> HeraclesClient.displayItemsRewardedToast(message.quest(), message.items());
    }
}
