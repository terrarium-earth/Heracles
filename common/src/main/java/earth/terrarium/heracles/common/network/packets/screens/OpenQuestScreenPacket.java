package earth.terrarium.heracles.common.network.packets.screens;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.ModScreens;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record OpenQuestScreenPacket(boolean editing, QuestContent content) implements Packet<OpenQuestScreenPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "open_quest_screen");
    public static final PacketHandler<OpenQuestScreenPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenQuestScreenPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<OpenQuestScreenPacket> {
        @Override
        public void encode(OpenQuestScreenPacket message, FriendlyByteBuf buffer) {
            buffer.writeBoolean(message.editing);
            message.content.to(buffer);
        }

        @Override
        public OpenQuestScreenPacket decode(FriendlyByteBuf buffer) {
            return new OpenQuestScreenPacket(
                buffer.readBoolean(),
                QuestContent.from(buffer)
            );
        }

        @Override
        public PacketContext handle(OpenQuestScreenPacket message) {
            return (player, level) -> {
                if (QuestHandler.failedToLoad) {
                    player.sendSystemMessage(Component.translatable("gui.heracles.error.quests.load_fail"));
                    player.sendSystemMessage(Component.translatable("gui.heracles.error.quests.load_fail.suggestion"));
                    return;
                }
                if (message.editing) {
                    ModScreens.openEditQuestScreen(message.content);
                } else {
                    ModScreens.openQuestScreen(message.content);
                }
            };
        }
    }
}
