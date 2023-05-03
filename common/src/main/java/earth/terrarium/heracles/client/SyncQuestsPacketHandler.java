package earth.terrarium.heracles.client;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Quest;
import earth.terrarium.heracles.network.SyncQuestsPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class SyncQuestsPacketHandler implements PacketHandler<SyncQuestsPacket> {
    private static final Codec<Map<ResourceLocation, Quest>> QUEST_MAP_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Quest.networkCodec());

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
