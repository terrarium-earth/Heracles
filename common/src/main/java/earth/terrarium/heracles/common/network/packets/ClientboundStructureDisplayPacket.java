package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientStructureDisplays;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record ClientboundStructureDisplayPacket(
    Set<ResourceLocation> structures,
    Set<ResourceLocation> structureTags
) implements Packet<ClientboundStructureDisplayPacket> {

    public static final ClientboundPacketType<ClientboundStructureDisplayPacket> TYPE = new Type();

    public ClientboundStructureDisplayPacket(MinecraftServer server) {
        this(
            server.registryAccess()
                .registry(Registries.STRUCTURE)
                .map(registry -> registry.keySet())
                .orElse(Set.of()),
            server.registryAccess()
                .registry(Registries.STRUCTURE)
                .map(registry -> registry.getTagNames()
                    .map(TagKey::location)
                    .collect(Collectors.toSet()))
                .orElse(Set.of())
        );
    }

    @Override
    public PacketType<ClientboundStructureDisplayPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<ClientboundStructureDisplayPacket> {
        @Override
        public Class<ClientboundStructureDisplayPacket> type() {
            return ClientboundStructureDisplayPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "structure_display");
        }

        @Override
        public void encode(ClientboundStructureDisplayPacket message, FriendlyByteBuf buffer) {
            buffer.writeCollection(message.structures, FriendlyByteBuf::writeResourceLocation);
            buffer.writeCollection(message.structureTags, FriendlyByteBuf::writeResourceLocation);
        }

        @Override
        public ClientboundStructureDisplayPacket decode(FriendlyByteBuf buffer) {
            return new ClientboundStructureDisplayPacket(
                Set.copyOf(buffer.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation)),
                Set.copyOf(buffer.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation))
            );
        }

        @Override
        public Runnable handle(ClientboundStructureDisplayPacket message) {
            return () -> ClientStructureDisplays.update(message.structures(), message.structureTags());
        }
    }
}