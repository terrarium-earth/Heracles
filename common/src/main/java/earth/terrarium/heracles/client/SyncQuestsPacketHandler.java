package earth.terrarium.heracles.client;

import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Quest;
import earth.terrarium.heracles.network.SyncQuestsPacket;
import net.minecraft.network.FriendlyByteBuf;

public class SyncQuestsPacketHandler implements PacketHandler<SyncQuestsPacket> {
    @Override
    public void encode(SyncQuestsPacket message, FriendlyByteBuf buffer) {
        buffer.writeMap(message.quests(), FriendlyByteBuf::writeResourceLocation, (buf, quest) -> PacketHelper.writeWithYabn(buf, Quest.networkCodec(), quest, true));
    }

    @Override
    public SyncQuestsPacket decode(FriendlyByteBuf buffer) {
        return new SyncQuestsPacket(buffer.readMap(FriendlyByteBuf::readResourceLocation, buf -> PacketHelper.readWithYabn(buf, Quest.networkCodec(), true).get().orThrow()));
    }

    @Override
    public PacketContext handle(SyncQuestsPacket message) {
        return (player, level) -> ClientQuests.sync(message.quests());
    }
}
