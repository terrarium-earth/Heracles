package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientAdvancementDisplays;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.Map;
import java.util.stream.Collectors;

public record ClientboundAdvancementDisplayPacket(
    Map<ResourceLocation, DisplayInfo> infos
) implements Packet<ClientboundAdvancementDisplayPacket> {

    public static final ClientboundPacketType<ClientboundAdvancementDisplayPacket> TYPE = new Type();

    public ClientboundAdvancementDisplayPacket(MinecraftServer server) {
        this(
            server.getAdvancements()
                .getAllAdvancements()
                .stream()
                .filter(advancement -> advancement.getDisplay() != null)
                .collect(Collectors.toMap(Advancement::getId, Advancement::getDisplay))
        );
    }

    @Override
    public PacketType<ClientboundAdvancementDisplayPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<ClientboundAdvancementDisplayPacket> {
        @Override
        public Class<ClientboundAdvancementDisplayPacket> type() {
            return ClientboundAdvancementDisplayPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "advancement_display");
        }

        @Override
        public void encode(ClientboundAdvancementDisplayPacket message, FriendlyByteBuf buffer) {
            buffer.writeMap(
                message.infos,
                FriendlyByteBuf::writeResourceLocation,
                (buf, info) -> info.serializeToNetwork(buf)
            );
        }

        @Override
        public ClientboundAdvancementDisplayPacket decode(FriendlyByteBuf buffer) {
            return new ClientboundAdvancementDisplayPacket(
                buffer.readMap(
                    FriendlyByteBuf::readResourceLocation,
                    DisplayInfo::fromNetwork
                )
            );
        }

        @Override
        public Runnable handle(ClientboundAdvancementDisplayPacket message) {
            return () -> ClientAdvancementDisplays.add(message.infos());
        }
    }
}
