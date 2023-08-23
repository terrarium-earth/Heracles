package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.networking.base.CodecPacketHandler;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record ServerboundUpdateQuestPacket(
    String id, NetworkQuestData data
) implements Packet<ServerboundUpdateQuestPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "update_server_quest");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ServerboundUpdateQuestPacket> getHandler() {
        return HANDLER;
    }


    @SuppressWarnings("UnstableApiUsage")
    public static class Handler extends CodecPacketHandler<ServerboundUpdateQuestPacket> {

        public Handler() {
            super(ObjectByteCodec.create(
                ByteCodec.STRING.fieldOf(ServerboundUpdateQuestPacket::id),
                NetworkQuestData.CODEC.fieldOf(ServerboundUpdateQuestPacket::data),
                ServerboundUpdateQuestPacket::new
            ));
        }

        @Override
        public PacketContext handle(ServerboundUpdateQuestPacket message) {
            return (player, level) -> {
                if (player.hasPermissions(2)) {
                    Quest quest = QuestHandler.get(message.id);
                    if (quest == null) return;
                    message.data.update(quest);
                    QuestHandler.markDirty(message.id);
                    NetworkHandler.CHANNEL.sendToAllPlayers(
                        new ClientboundUpdateQuestPacket(message.id, message.data),
                        Objects.requireNonNull(player.getServer())
                    );
                }
            };
        }
    }
}
