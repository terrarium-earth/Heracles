package earth.terrarium.heracles.common.network.packets.groups;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.groups.Group;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record CreateGroupPacket(String group) implements Packet<CreateGroupPacket> {
    public static final ServerboundPacketType<CreateGroupPacket> TYPE = new Type();

    @Override
    public PacketType<CreateGroupPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<CreateGroupPacket> {

        @Override
        public Class<CreateGroupPacket> type() {
            return CreateGroupPacket.class;
        }

        @Override
        public void encode(CreateGroupPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.group);
        }

        @Override
        public CreateGroupPacket decode(FriendlyByteBuf buffer) {
            return new CreateGroupPacket(buffer.readUtf());
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "create_group");
        }

        @Override
        public Consumer<Player> handle(CreateGroupPacket message) {
            return (player) -> {
                if (player.hasPermissions(2) && !QuestHandler.groups().containsKey(message.group)) {
                    QuestHandler.groups().put(message.group, new Group(message.group));
                    QuestHandler.saveGroups();
                }
            };
        }
    }
}
