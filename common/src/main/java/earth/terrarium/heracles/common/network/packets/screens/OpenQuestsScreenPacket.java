package earth.terrarium.heracles.common.network.packets.screens;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.ModScreens;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record OpenQuestsScreenPacket(QuestsContent content) implements Packet<OpenQuestsScreenPacket> {

    public static final ClientboundPacketType<OpenQuestsScreenPacket> TYPE = new Type();

    @Override
    public PacketType<OpenQuestsScreenPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<OpenQuestsScreenPacket> {
        @Override
        public Class<OpenQuestsScreenPacket> type() {
            return OpenQuestsScreenPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "open_quests_screen");
        }

        @Override
        public void encode(OpenQuestsScreenPacket message, FriendlyByteBuf buffer) {
            message.content.to(buffer);
        }

        @Override
        public OpenQuestsScreenPacket decode(FriendlyByteBuf buffer) {
            return new OpenQuestsScreenPacket(QuestsContent.from(buffer));
        }

        @Override
        public Runnable handle(OpenQuestsScreenPacket message) {
            return () -> {
                ClientQuests.syncGroup(message.content());
                ModScreens.openQuests(message.content());
            };
        }
    }
}
