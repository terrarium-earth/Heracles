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
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record ServerboundAddQuestPacket(
    String id, Quest quest
) implements Packet<ServerboundAddQuestPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "add_server_quest");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ServerboundAddQuestPacket> getHandler() {
        return HANDLER;
    }


    @SuppressWarnings("UnstableApiUsage")
    public static class Handler extends CodecPacketHandler<ServerboundAddQuestPacket> {

        public Handler() {
            super(ObjectByteCodec.create(
                ByteCodec.STRING.fieldOf(ServerboundAddQuestPacket::id),
                ModUtils.toByteCodec(Quest.CODEC).fieldOf(ServerboundAddQuestPacket::quest),
                ServerboundAddQuestPacket::new
            ));
        }

        @Override
        public PacketContext handle(ServerboundAddQuestPacket message) {
            return (player, level) -> {
                if (player.hasPermissions(2)) {
                    QuestHandler.upload(message.id(), message.quest());
                    NetworkHandler.CHANNEL.sendToAllPlayers(
                        new ClientboundAddQuestPacket(message.id(), message.quest()),
                        Objects.requireNonNull(player.getServer())
                    );
                }
            };
        }
    }
}
