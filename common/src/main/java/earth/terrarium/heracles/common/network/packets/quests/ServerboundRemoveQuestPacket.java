package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.resourcefullib.common.networking.base.CodecPacketHandler;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record ServerboundRemoveQuestPacket(String id) implements Packet<ServerboundRemoveQuestPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "remove_server_quest");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ServerboundRemoveQuestPacket> getHandler() {
        return HANDLER;
    }


    @SuppressWarnings("UnstableApiUsage")
    public static class Handler extends CodecPacketHandler<ServerboundRemoveQuestPacket> {

        public Handler() {
            super(ByteCodec.STRING.map(ServerboundRemoveQuestPacket::new, ServerboundRemoveQuestPacket::id));
        }

        @Override
        public PacketContext handle(ServerboundRemoveQuestPacket message) {
            return (player, level) -> {
                if (player.hasPermissions(2)) {
                    QuestHandler.remove(message.id);
                    NetworkHandler.CHANNEL.sendToAllPlayers(
                        new ClientboundRemoveQuestPacket(message.id()),
                        Objects.requireNonNull(player.getServer())
                    );
                }
            };
        }
    }
}
