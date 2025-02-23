package earth.terrarium.heracles.common.network.packets.screens;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.ModScreens;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record OpenQuestScreenPacket(boolean editing, QuestContent content) implements Packet<OpenQuestScreenPacket> {

    public static final ClientboundPacketType<OpenQuestScreenPacket> TYPE = new Type();

    @Override
    public PacketType<OpenQuestScreenPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<OpenQuestScreenPacket> {
        @Override
        public ResourceLocation id() {
            return ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "open_quest_screen");
        }

        @Override
        public void encode(OpenQuestScreenPacket message, RegistryFriendlyByteBuf buffer) {
            buffer.writeBoolean(message.editing);
            message.content.to(buffer);
        }

        @Override
        public OpenQuestScreenPacket decode(RegistryFriendlyByteBuf buffer) {
            return new OpenQuestScreenPacket(
                buffer.readBoolean(),
                QuestContent.from(buffer)
            );
        }

        @Override
        public Runnable handle(OpenQuestScreenPacket message) {
            return () -> {
                if (message.editing) {
                    ModScreens.openEditQuestScreen(message.content);
                } else {
                    ModScreens.openQuestScreen(message.content);
                }
            };
        }
    }
}
