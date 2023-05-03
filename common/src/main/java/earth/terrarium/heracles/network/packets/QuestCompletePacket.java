package earth.terrarium.heracles.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.Quest;
import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.resource.QuestManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;

public record QuestCompletePacket(Quest quest, List<Item> items) implements Packet<QuestCompletePacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "quest_complete");
    public static final PacketHandler<QuestCompletePacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<QuestCompletePacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<QuestCompletePacket> {
        @Override
        public void encode(QuestCompletePacket message, FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(QuestManager.INSTANCE.getQuests().inverse().get(message.quest()));
            buffer.writeCollection(message.items(), (buf, item) -> buf.writeVarInt(Item.getId(item)));
        }

        @Override
        public QuestCompletePacket decode(FriendlyByteBuf buffer) {
            return new QuestCompletePacket(
                QuestManager.INSTANCE.getQuests().get(buffer.readResourceLocation()),
                buffer.readList(buf -> Item.byId(buf.readVarInt()))
            );
        }

        @Override
        public PacketContext handle(QuestCompletePacket message) {
            return (player, level) -> HeraclesClient.displayItemsRewardedToast(message.quest(), message.items());
        }
    }
}
