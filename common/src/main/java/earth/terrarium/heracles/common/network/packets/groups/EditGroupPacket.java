package earth.terrarium.heracles.common.network.packets.groups;

import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.groups.Group;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record EditGroupPacket(
    String group,
    Optional<QuestIcon<?>> icon,
    Optional<Component> title,
    Optional<Component> description
) implements Packet<EditGroupPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "edit_group");
    public static final PacketHandler<EditGroupPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<EditGroupPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<EditGroupPacket> {

        @Override
        public void encode(EditGroupPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.group);
            buffer.writeOptional(message.icon(), (buf, icon) -> PacketHelper.writeWithRegistryYabn(Heracles.getRegistryAccess(), buffer, QuestIcons.CODEC, icon, true));
            buffer.writeOptional(message.title(), FriendlyByteBuf::writeComponent);
            buffer.writeOptional(message.description(), FriendlyByteBuf::writeComponent);
        }

        @Override
        public EditGroupPacket decode(FriendlyByteBuf buffer) {
            String id = buffer.readUtf();
            Optional<QuestIcon<?>> icon = buffer.readOptional(buf -> PacketHelper.readWithRegistryYabn(Heracles.getRegistryAccess(), buf, QuestIcons.CODEC, true)
                .getOrThrow(false, System.err::println));
            Optional<Component> title = buffer.readOptional(FriendlyByteBuf::readComponent);
            Optional<Component> description = buffer.readOptional(FriendlyByteBuf::readComponent);
            return new EditGroupPacket(id, icon, title, description);
        }

        @Override
        public PacketContext handle(EditGroupPacket message) {
            return (player, level) -> {
                if (player.hasPermissions(2) && QuestHandler.groups().containsKey(message.group())) {
                    Group group = QuestHandler.groups().get(message.group());
                    QuestHandler.groups().put(message.group(), new Group(
                        message.icon().or(group::icon),
                        message.title().orElse(group.title()),
                        message.description().orElse(group.description())
                    ));
                    QuestHandler.saveGroups();
                }
            };
        }
    }
}
