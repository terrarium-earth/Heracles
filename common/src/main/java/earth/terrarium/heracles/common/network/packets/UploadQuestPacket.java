package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.utils.PacketHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record UploadQuestPacket(String id, Quest quest) implements Packet<UploadQuestPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "upload_quest");
    public static final PacketHandler<UploadQuestPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<UploadQuestPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<UploadQuestPacket> {

        @Override
        public void encode(UploadQuestPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id());
            PacketHelper.writeWithYabn(Heracles.getRegistryAccess(), buffer, Quest.CODEC, message.quest(), true);
        }

        @Override
        public UploadQuestPacket decode(FriendlyByteBuf buffer) {
            return new UploadQuestPacket(
                buffer.readUtf(),
                PacketHelper.readWithYabn(Heracles.getRegistryAccess(), buffer, Quest.CODEC, true).get().orThrow()
            );
        }

        @Override
        public PacketContext handle(UploadQuestPacket message) {
            return (player, level) -> {
                if (player.hasPermissions(2)) {
                    if (!QuestHandler.upload(message.id(), message.quest())) {
                        //TODO: Send error message to client
                    }
                }
            };
        }
    }
}
