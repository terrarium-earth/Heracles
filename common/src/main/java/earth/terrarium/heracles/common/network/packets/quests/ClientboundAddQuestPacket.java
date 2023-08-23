package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.networking.base.CodecPacketHandler;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.resources.ResourceLocation;

public record ClientboundAddQuestPacket(
    String id, Quest quest
) implements Packet<ClientboundAddQuestPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "add_client_quest");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ClientboundAddQuestPacket> getHandler() {
        return HANDLER;
    }


    @SuppressWarnings("UnstableApiUsage")
    public static class Handler extends CodecPacketHandler<ClientboundAddQuestPacket> {

        public Handler() {
            super(ObjectByteCodec.create(
                ByteCodec.STRING.fieldOf(ClientboundAddQuestPacket::id),
                ModUtils.toByteCodec(Quest.CODEC).fieldOf(ClientboundAddQuestPacket::quest),
                ClientboundAddQuestPacket::new
            ));
        }

        @Override
        public PacketContext handle(ClientboundAddQuestPacket message) {
            return (player, level) -> ClientQuests.addQuest(message.id, message.quest);
        }
    }
}
