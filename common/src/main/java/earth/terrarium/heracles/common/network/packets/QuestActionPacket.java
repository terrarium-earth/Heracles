package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.handlers.syncing.QuestSyncer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record QuestActionPacket(Action action) implements Packet<QuestActionPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "quest_action");
    public static final PacketHandler<QuestActionPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<QuestActionPacket> getHandler() {
        return HANDLER;
    }

    public enum Action {
        SAVE
    }

    public static class Handler implements PacketHandler<QuestActionPacket> {

        @Override
        public void encode(QuestActionPacket message, FriendlyByteBuf buffer) {
            buffer.writeEnum(message.action());
        }

        @Override
        public QuestActionPacket decode(FriendlyByteBuf buffer) {
            return new QuestActionPacket(buffer.readEnum(Action.class));
        }

        @Override
        public PacketContext handle(QuestActionPacket message) {
            return (player, level) -> {
                if (player.hasPermissions(2)) {
                    switch (message.action()) {
                        case SAVE -> {
                            if (player.hasPermissions(2)) {
                                QuestHandler.save();
                                if (player.getServer() != null) {
                                    QuestSyncer.syncToAll(player.getServer().getPlayerList().getPlayers());
                                }
                            }
                        }
                    }
                }
            };
        }
    }
}
