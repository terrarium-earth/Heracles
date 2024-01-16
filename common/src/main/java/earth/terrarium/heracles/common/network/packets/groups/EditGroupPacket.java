package earth.terrarium.heracles.common.network.packets.groups;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.groups.Group;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.function.Consumer;

public record EditGroupPacket(
    String group,
    Optional<QuestIcon<?>> icon,
    Optional<String> title,
    Optional<String> description
) implements Packet<EditGroupPacket> {

    public static final ServerboundPacketType<EditGroupPacket> TYPE = new Type();

    public static EditGroupPacket ofTitle(String group, String title) {
        return new EditGroupPacket(group, Optional.empty(), Optional.of(title), Optional.empty());
    }

    public static EditGroupPacket ofIcon(String group, QuestIcon<?> icon) {
        return new EditGroupPacket(group, Optional.of(icon), Optional.empty(), Optional.empty());
    }

    @Override
    public PacketType<EditGroupPacket> type() {
        return TYPE;
    }

    public static class Type implements ServerboundPacketType<EditGroupPacket> {

        public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "edit_group");

        @Override
        public Class<EditGroupPacket> type() {
            return EditGroupPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public void encode(EditGroupPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.group);
            buffer.writeOptional(message.icon(), (buf, icon) -> PacketHelper.writeWithRegistryYabn(Heracles.getRegistryAccess(), buffer, QuestIcons.CODEC, icon, true));
            buffer.writeOptional(message.title(), FriendlyByteBuf::writeUtf);
            buffer.writeOptional(message.description(), FriendlyByteBuf::writeUtf);
        }

        @Override
        public EditGroupPacket decode(FriendlyByteBuf buffer) {
            String id = buffer.readUtf();
            Optional<QuestIcon<?>> icon = buffer.readOptional(buf -> PacketHelper.readWithRegistryYabn(Heracles.getRegistryAccess(), buf, QuestIcons.CODEC, true)
                .getOrThrow(false, System.err::println));
            Optional<String> title = buffer.readOptional(FriendlyByteBuf::readUtf);
            Optional<String> description = buffer.readOptional(FriendlyByteBuf::readUtf);
            return new EditGroupPacket(id, icon, title, description);
        }

        @Override
        public Consumer<Player> handle(EditGroupPacket message) {
            return (player) -> {
                if (player.hasPermissions(2) && QuestHandler.groups().containsKey(message.group())) {
                    Group group = QuestHandler.groups().get(message.group());
                    QuestHandler.groups().put(message.group(), new Group(
                        message.icon().or(group::icon),
                        message.title().orElse(group.title()),
                        message.description().orElse(group.description())
                    ));
                    QuestHandler.saveGroups();
                    //TODO send packet to all players to update their quest gui
                }
            };
        }
    }
}
