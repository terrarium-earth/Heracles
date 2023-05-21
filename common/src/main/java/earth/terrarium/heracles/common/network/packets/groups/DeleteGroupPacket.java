package earth.terrarium.heracles.common.network.packets.groups;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record DeleteGroupPacket(String group) implements Packet<DeleteGroupPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "delete_group");
    public static final PacketHandler<DeleteGroupPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<DeleteGroupPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<DeleteGroupPacket> {

        @Override
        public void encode(DeleteGroupPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.group);
        }

        @Override
        public DeleteGroupPacket decode(FriendlyByteBuf buffer) {
            return new DeleteGroupPacket(buffer.readUtf());
        }

        @Override
        public PacketContext handle(DeleteGroupPacket message) {
            return (player, level) -> {
                if (player.hasPermissions(2) && QuestHandler.groups().contains(message.group)) {
                    QuestHandler.groups().remove(message.group);
                    QuestHandler.saveGroups();
                }
            };
        }
    }
}
