package earth.terrarium.heracles.common.network.packets.groups;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.quests.GroupSettings;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public record ServerboundUpdateGroupSettingsPacket(String group, String iconId, boolean iconEnabled) implements Packet<ServerboundUpdateGroupSettingsPacket> {

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
            buffer.writeUtf(message.iconId());
            buffer.writeBoolean(message.iconEnabled());
        }

        @Override
        public ServerboundUpdateGroupSettingsPacket decode(RegistryFriendlyByteBuf buffer) {
            return new ServerboundUpdateGroupSettingsPacket(
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readBoolean()
            );
        }

        @Override
        public Consumer<Player> handle(ServerboundUpdateGroupSettingsPacket message) {
            return (player) -> {
                if (player.hasPermissions(2)) {
                    GroupSettings settings = QuestHandler.getGroupSettings(message.group());
                    settings.setIcon(GroupSettings.deserializeIcon(message.iconId()));
                    settings.setIconEnabled(message.iconEnabled());
                    QuestHandler.saveGroupSettings();
                    if (player.getServer() != null) {
                        NetworkHandler.CHANNEL.sendToPlayers(
                            new SyncGroupSettingsPacket(message.group(), message.iconId(), message.iconEnabled()),
                            player.getServer().getPlayerList().getPlayers()
                        );
                    }
                }
            };
        }
    }
}
