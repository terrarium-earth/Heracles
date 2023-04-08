package earth.terrarium.heracles;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.client.QuestCompletePacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;

public record QuestCompletePacket(Quest quest, List<Item> items) implements Packet<QuestCompletePacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "quest_complete");
    public static final PacketHandler<QuestCompletePacket> HANDLER = new QuestCompletePacketHandler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<QuestCompletePacket> getHandler() {
        return HANDLER;
    }
}
