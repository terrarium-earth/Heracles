package earth.terrarium.heracles.common.network.packets;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.Quest;
import earth.terrarium.heracles.client.ClientQuests;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record SyncQuestsPacket(Map<String, Quest> quests) implements Packet<SyncQuestsPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "sync_quests");
    public static final PacketHandler<SyncQuestsPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SyncQuestsPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<SyncQuestsPacket> {
        private static final Codec<Map<String, Quest>> QUEST_MAP_CODEC = Codec.unboundedMap(Codec.STRING, Quest.CODEC);

        @Override
        public void encode(SyncQuestsPacket message, FriendlyByteBuf buffer) {
            PacketHelper.writeWithYabn(buffer, QUEST_MAP_CODEC, message.quests(), true);
        }

        @Override
        public SyncQuestsPacket decode(FriendlyByteBuf buffer) {
            return new SyncQuestsPacket(PacketHelper.readWithYabn(buffer, QUEST_MAP_CODEC, true).get().orThrow());
        }

        @Override
        public PacketContext handle(SyncQuestsPacket message) {
            return (player, level) -> ClientQuests.sync(message.quests());
        }
    }
}
