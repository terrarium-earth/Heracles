package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;

public record QuestRewardClaimedPacket(Quest quest, List<Item> items) implements Packet<QuestRewardClaimedPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "quest_reward_claimed");
    public static final PacketHandler<QuestRewardClaimedPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<QuestRewardClaimedPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<QuestRewardClaimedPacket> {
        @Override
        public void encode(QuestRewardClaimedPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(QuestHandler.getKey(message.quest()));
            buffer.writeCollection(message.items(), (buf, item) -> buf.writeVarInt(Item.getId(item)));
        }

        @Override
        public QuestRewardClaimedPacket decode(FriendlyByteBuf buffer) {
            return new QuestRewardClaimedPacket(
                QuestHandler.get(buffer.readUtf()),
                buffer.readList(buf -> Item.byId(buf.readVarInt()))
            );
        }

        @Override
        public PacketContext handle(QuestRewardClaimedPacket message) {
            return (player, level) -> HeraclesClient.displayItemsRewardedToast(message.quest(), message.items());
        }
    }
}
