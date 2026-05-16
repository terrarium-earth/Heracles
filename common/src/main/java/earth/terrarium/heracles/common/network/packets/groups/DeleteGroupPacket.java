package earth.terrarium.heracles.common.network.packets.groups;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.handlers.syncing.QuestSyncer;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record DeleteGroupPacket(String group) implements Packet<DeleteGroupPacket> {
    public static final ServerboundPacketType<DeleteGroupPacket> TYPE = new Type();

    @Override
    public PacketType<DeleteGroupPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<DeleteGroupPacket> {

        @Override
        public ResourceLocation id() {
            return Heracles.id("delete_group");
        }

        @Override
        public void encode(DeleteGroupPacket message, RegistryFriendlyByteBuf buffer) {
            buffer.writeUtf(message.group);
        }

        @Override
        public DeleteGroupPacket decode(RegistryFriendlyByteBuf buffer) {
            return new DeleteGroupPacket(buffer.readUtf());
        }

        @Override
        public Consumer<Player> handle(DeleteGroupPacket message) {
            return (player) -> {
                if (player.hasPermissions(2) && QuestHandler.groups().contains(message.group)) {
                    QuestHandler.deleteGroup(message.group);
                    QuestSyncer.syncToAll(player.getServer(), player.getServer().getPlayerList().getPlayers());
                    if (player instanceof ServerPlayer serverPlayer) {
                        String firstGroup = QuestHandler.groups().isEmpty() ? "" : QuestHandler.groups().get(0);
                        if (!firstGroup.isEmpty()) {
                            ModUtils.openGroup(serverPlayer, firstGroup);
                        }
                    }
                }
            };
        }
    }
}
