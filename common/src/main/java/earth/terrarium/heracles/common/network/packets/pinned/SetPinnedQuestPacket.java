package earth.terrarium.heracles.common.network.packets.pinned;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.pinned.PinnedQuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public record SetPinnedQuestPacket(String quest, boolean value) implements Packet<SetPinnedQuestPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "set_pinned_quest");
    public static final PacketHandler<SetPinnedQuestPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SetPinnedQuestPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<SetPinnedQuestPacket> {

        @Override
        public void encode(SetPinnedQuestPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.quest);
            buffer.writeBoolean(message.value);
        }

        @Override
        public SetPinnedQuestPacket decode(FriendlyByteBuf buffer) {
            return new SetPinnedQuestPacket(buffer.readUtf(), buffer.readBoolean());
        }

        @Override
        public PacketContext handle(SetPinnedQuestPacket message) {
            return (player, level) -> {
                Set<String> quests = PinnedQuestHandler.getPinned((ServerPlayer) player);
                if (message.value) {
                    quests.add(message.quest);
                } else {
                    quests.remove(message.quest);
                }
                PinnedQuestHandler.sync((ServerPlayer) player);
            };
        }
    }
}
