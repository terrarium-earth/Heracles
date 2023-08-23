package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.HeraclesClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record QuestUnlockedPacket(String id) implements Packet<QuestUnlockedPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "quest_unlocked");
    public static final PacketHandler<QuestUnlockedPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<QuestUnlockedPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<QuestUnlockedPacket> {
        @Override
        public void encode(QuestUnlockedPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id);
        }

        @Override
        public QuestUnlockedPacket decode(FriendlyByteBuf buffer) {
            return new QuestUnlockedPacket(buffer.readUtf());
        }

        @Override
        public PacketContext handle(QuestUnlockedPacket message) {
            return (player, level) -> HeraclesClient.displayQuestUnlockedToast(message.id());
        }
    }
}
