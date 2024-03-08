package earth.terrarium.heracles.common.network.packets.screens;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.ModScreens;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record OpenQuestScreenPacket(QuestContent content) implements Packet<OpenQuestScreenPacket> {

    public static final ClientboundPacketType<OpenQuestScreenPacket> TYPE = new Type();

    @Override
    public PacketType<OpenQuestScreenPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<OpenQuestScreenPacket> {
        @Override
        public Class<OpenQuestScreenPacket> type() {
            return OpenQuestScreenPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "open_quest_screen");
        }

        @Override
        public void encode(OpenQuestScreenPacket message, FriendlyByteBuf buffer) {
            message.content.to(buffer);
        }

        @Override
        public OpenQuestScreenPacket decode(FriendlyByteBuf buffer) {
            return new OpenQuestScreenPacket(
                QuestContent.from(buffer)
            );
        }

        @Override
        public Runnable handle(OpenQuestScreenPacket message) {
            return () -> ModScreens.openQuest(message.content());
        }
    }
}
