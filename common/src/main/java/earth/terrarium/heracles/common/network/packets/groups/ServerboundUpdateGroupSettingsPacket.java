package earth.terrarium.heracles.common.network.packets.groups;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.quests.GroupSettings;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record ServerboundUpdateGroupSettingsPacket(String group, String newName, String iconId, boolean iconEnabled, String background, int backgroundOpacity) implements Packet<ServerboundUpdateGroupSettingsPacket> {

    public static final ServerboundPacketType<ServerboundUpdateGroupSettingsPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundUpdateGroupSettingsPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ServerboundUpdateGroupSettingsPacket> {

        @Override
        public ResourceLocation id() {
            return Heracles.id("update_group_settings");
        }

        @Override
        public void encode(ServerboundUpdateGroupSettingsPacket message, RegistryFriendlyByteBuf buffer) {
            buffer.writeUtf(message.group());
            buffer.writeUtf(message.newName());
            buffer.writeUtf(message.iconId());
            buffer.writeBoolean(message.iconEnabled());
            buffer.writeUtf(message.background());
            buffer.writeInt(message.backgroundOpacity());
        }

        @Override
        public ServerboundUpdateGroupSettingsPacket decode(RegistryFriendlyByteBuf buffer) {
            return new ServerboundUpdateGroupSettingsPacket(
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readBoolean(),
                buffer.readUtf(),
                buffer.readInt()
            );
        }

        @Override
        public Consumer<Player> handle(ServerboundUpdateGroupSettingsPacket message) {
            return (player) -> {
                if (player.hasPermissions(2)) {
                    QuestHandler.renameGroup(message.group(), message.newName());
                    GroupSettings settings = QuestHandler.getGroupSettings(message.newName());
                    settings.setIcon(GroupSettings.deserializeIcon(message.iconId()));
                    settings.setIconEnabled(message.iconEnabled());
                    settings.setBackground(message.background());
                    settings.setBackgroundOpacity(message.backgroundOpacity());
                    QuestHandler.saveGroupSettings();
                    if (player.getServer() != null) {
                        NetworkHandler.CHANNEL.sendToPlayers(
                            new SyncGroupSettingsPacket(message.group(), message.newName(), message.iconId(), message.iconEnabled(), message.background(), message.backgroundOpacity()),
                            player.getServer().getPlayerList().getPlayers()
                        );
                    }
                }
            };
        }
    }
}
