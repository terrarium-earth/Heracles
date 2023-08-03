package earth.terrarium.heracles.common.network.packets.screens;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.ModScreens;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record OpenQuestsScreenPacket(boolean editing, QuestsContent content) implements Packet<OpenQuestsScreenPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "open_quests_screen");
    public static final PacketHandler<OpenQuestsScreenPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenQuestsScreenPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<OpenQuestsScreenPacket> {
        @Override
        public void encode(OpenQuestsScreenPacket message, FriendlyByteBuf buffer) {
            buffer.writeBoolean(message.editing);
            message.content.to(buffer);
        }

        @Override
        public OpenQuestsScreenPacket decode(FriendlyByteBuf buffer) {
            return new OpenQuestsScreenPacket(
                buffer.readBoolean(),
                QuestsContent.from(buffer)
            );
        }

        @Override
        public PacketContext handle(OpenQuestsScreenPacket message) {
            return (player, level) -> {
                if (QuestHandler.failedToLoad) {
                    player.sendSystemMessage(Component.literal("Failed to load quests, check the logs for more information."));
                    player.sendSystemMessage(Component.literal("Manually fix and reload the quests by running /reload"));
                    return;
                }
                if (message.editing) {
                    ModScreens.openEditQuestsScreen(message.content);
                } else {
                    ModScreens.openQuestsScreen(message.content);
                }
            };
        }
    }
}
