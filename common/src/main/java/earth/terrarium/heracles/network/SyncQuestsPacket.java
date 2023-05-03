package earth.terrarium.heracles.network;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.Quest;
import earth.terrarium.heracles.client.SyncQuestsPacketHandler;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record SyncQuestsPacket(Map<ResourceLocation, Quest> quests) implements Packet<SyncQuestsPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "sync_quests");
    public static final PacketHandler<SyncQuestsPacket> HANDLER = new SyncQuestsPacketHandler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SyncQuestsPacket> getHandler() {
        return HANDLER;
    }
}
