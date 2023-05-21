package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record OpenQuestPacket(String quest, boolean edit) implements Packet<OpenQuestPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "open_quest");
    public static final PacketHandler<OpenQuestPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenQuestPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<OpenQuestPacket> {

        @Override
        public void encode(OpenQuestPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.quest);
            buffer.writeBoolean(message.edit);
        }

        @Override
        public OpenQuestPacket decode(FriendlyByteBuf buffer) {
            return new OpenQuestPacket(buffer.readUtf(), buffer.readBoolean());
        }

        @Override
        public PacketContext handle(OpenQuestPacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    if (message.edit() && player.hasPermissions(2)) {
                        ModUtils.openEditQuest(serverPlayer, message.quest());
                    } else {
                        ModUtils.openQuest(serverPlayer, message.quest());
                    }
                }
            };
        }
    }
}
