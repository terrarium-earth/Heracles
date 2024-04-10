package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientlootTableDisplays;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootDataType;

import java.util.HashSet;
import java.util.Set;

public record ClientboundLootTablesDisplayPacket(
    Set<ResourceLocation> tables
) implements Packet<ClientboundLootTablesDisplayPacket> {

    public static final ClientboundPacketType<ClientboundLootTablesDisplayPacket> TYPE = new Type();

    public ClientboundLootTablesDisplayPacket(MinecraftServer server) {
        this(new HashSet<>(server.getLootData().getKeys(LootDataType.TABLE)));
    }

    @Override
    public PacketType<ClientboundLootTablesDisplayPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<ClientboundLootTablesDisplayPacket> {
        @Override
        public Class<ClientboundLootTablesDisplayPacket> type() {
            return ClientboundLootTablesDisplayPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "loottable_display");
        }

        @Override
        public void encode(ClientboundLootTablesDisplayPacket message, FriendlyByteBuf buffer) {
            buffer.writeCollection(
                message.tables,
                FriendlyByteBuf::writeResourceLocation
            );
        }

        @Override
        public ClientboundLootTablesDisplayPacket decode(FriendlyByteBuf buffer) {
            Set<ResourceLocation> ids = buffer.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation);
            return new ClientboundLootTablesDisplayPacket(ids);
        }

        @Override
        public Runnable handle(ClientboundLootTablesDisplayPacket message) {
            return () -> ClientlootTableDisplays.set(message.tables);
        }
    }
}
