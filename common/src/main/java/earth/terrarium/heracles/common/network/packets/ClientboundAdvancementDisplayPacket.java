package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientAdvancementDisplays;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
                .filter(advancement -> advancement.value().display().isPresent())
                .collect(Collectors.toMap(AdvancementHolder::id, advancement -> advancement.value().display().get()))
        );
    }

    @Override
    public PacketType<ClientboundAdvancementDisplayPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<ClientboundAdvancementDisplayPacket> {
        @Override
        public ResourceLocation id() {
            return ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "advancement_display");
        }

        @Override
        public void encode(ClientboundAdvancementDisplayPacket message, RegistryFriendlyByteBuf buffer) {
            buffer.writeMap(
                message.infos,
                FriendlyByteBuf::writeResourceLocation,
                (buf, info) -> DisplayInfo.STREAM_CODEC.encode(new RegistryFriendlyByteBuf(buf, buffer.registryAccess()), info)
            );
        }

        @Override
        public ClientboundAdvancementDisplayPacket decode(RegistryFriendlyByteBuf buffer) {
            return new ClientboundAdvancementDisplayPacket(
                buffer.readMap(
                    FriendlyByteBuf::readResourceLocation,
                    buf -> DisplayInfo.STREAM_CODEC.decode(new RegistryFriendlyByteBuf(buf, buffer.registryAccess()))
                )
            );
        }

        @Override
        public Runnable handle(ClientboundAdvancementDisplayPacket message) {
            return () -> ClientAdvancementDisplays.add(message.infos());
        }
    }
}
