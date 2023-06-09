package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.HeraclesClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record QuestCompletedPacket(String id) implements Packet<QuestCompletedPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "quest_complete");
    public static final PacketHandler<QuestCompletedPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<QuestCompletedPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<QuestCompletedPacket> {
        @Override
        public void encode(QuestCompletedPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id);
        }

        @Override
        public QuestCompletedPacket decode(FriendlyByteBuf buffer) {
            return new QuestCompletedPacket(buffer.readUtf());
        }

        @Override
        public PacketContext handle(QuestCompletedPacket message) {
            return (player, level) -> HeraclesClient.displayQuestCompleteToast(message.id());
        }
    }
}
