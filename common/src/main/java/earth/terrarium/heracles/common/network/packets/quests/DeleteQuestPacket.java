package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record DeleteQuestPacket(String id) implements Packet<DeleteQuestPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "delete_quest");
    public static final PacketHandler<DeleteQuestPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<DeleteQuestPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<DeleteQuestPacket> {

        @Override
        public void encode(DeleteQuestPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id());
        }

        @Override
        public DeleteQuestPacket decode(FriendlyByteBuf buffer) {
            return new DeleteQuestPacket(buffer.readUtf());
        }

        @Override
        public PacketContext handle(DeleteQuestPacket message) {
            return (player, level) -> {
                if (player.hasPermissions(2)) {
                    QuestHandler.delete(message.id());
                }
            };
        }
    }
}
