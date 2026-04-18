package earth.terrarium.heracles.common.network.packets.groups;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.handlers.quests.GroupSettings;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SyncGroupSettingsPacket(String group, String iconId, boolean iconEnabled) implements Packet<SyncGroupSettingsPacket> {

    public static final ClientboundPacketType<SyncGroupSettingsPacket> TYPE = new Type();

    @Override
    public PacketType<SyncGroupSettingsPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<SyncGroupSettingsPacket> {

        @Override
        public ResourceLocation id() {
            return Heracles.id("sync_group_settings");
        }

        @Override
        public void encode(SyncGroupSettingsPacket message, RegistryFriendlyByteBuf buffer) {
            buffer.writeUtf(message.group());
            buffer.writeUtf(message.iconId());
            buffer.writeBoolean(message.iconEnabled());
        }

        @Override
        public SyncGroupSettingsPacket decode(RegistryFriendlyByteBuf buffer) {
            return new SyncGroupSettingsPacket(
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readBoolean()
            );
        }

        @Override
        public Runnable handle(SyncGroupSettingsPacket message) {
            return () -> ClientQuests.updateGroupSettings(
                message.group(),
                GroupSettings.deserializeIcon(message.iconId()),
                message.iconEnabled()
            );
        }
    }
}
