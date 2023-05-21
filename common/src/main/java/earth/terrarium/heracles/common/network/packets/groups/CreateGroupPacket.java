package earth.terrarium.heracles.common.network.packets.groups;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record CreateGroupPacket(String group) implements Packet<CreateGroupPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "create_group");
    public static final PacketHandler<CreateGroupPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<CreateGroupPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<CreateGroupPacket> {

        @Override
        public void encode(CreateGroupPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.group);
        }

        @Override
        public CreateGroupPacket decode(FriendlyByteBuf buffer) {
            return new CreateGroupPacket(buffer.readUtf());
        }

        @Override
        public PacketContext handle(CreateGroupPacket message) {
            return (player, level) -> {
                if (player.hasPermissions(2) && !QuestHandler.groups().contains(message.group)) {
                    QuestHandler.groups().add(message.group);
                    QuestHandler.saveGroups();
                }
            };
        }
    }
}
