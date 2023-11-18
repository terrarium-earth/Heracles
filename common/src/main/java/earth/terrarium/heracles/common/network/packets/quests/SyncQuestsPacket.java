package earth.terrarium.heracles.common.network.packets.quests;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.codecs.yabn.YabnOps;
import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import com.teamresourceful.yabn.YabnParser;
import com.teamresourceful.yabn.elements.YabnElement;
import com.teamresourceful.yabn.reader.ByteReader;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record SyncQuestsPacket(Map<String, Quest> quests, List<String> groups) implements Packet<SyncQuestsPacket> {
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
            PacketHelper.writeWithRegistryYabn(Heracles.getRegistryAccess(), buffer, QUEST_MAP_CODEC, message.quests(), true);
            buffer.writeCollection(message.groups(), FriendlyByteBuf::writeUtf);
        }

        @Override
        public SyncQuestsPacket decode(FriendlyByteBuf buffer) {
            YabnElement element = YabnParser.parse(new ByteBufByteReader(buffer));
            try {
                return new SyncQuestsPacket(
                    QUEST_MAP_CODEC.parse(RegistryOps.create(YabnOps.COMPRESSED, Heracles.getRegistryAccess()), element).get().orThrow(),
                    buffer.readList(FriendlyByteBuf::readUtf)
                );
            } catch (Exception e) {
                Heracles.LOGGER.error("Failed to decode sync quests packet: {}", element, e);
                throw e;
            }
        }


        @Override
        public PacketContext handle(SyncQuestsPacket message) {
            return (player, level) -> ClientQuests.sync(message.quests(), message.groups());
        }
    }

    private record ByteBufByteReader(ByteBuf buf) implements ByteReader {

        @Override
        public byte peek() {
            return buf.getByte(buf.readerIndex());
        }

        @Override
        public void advance() {
            buf.skipBytes(1);
        }

        @Override
        public byte readByte() {
            return buf.readByte();
        }
    }
}
