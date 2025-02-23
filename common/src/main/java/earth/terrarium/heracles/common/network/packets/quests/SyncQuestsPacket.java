package earth.terrarium.heracles.common.network.packets.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefullib.common.codecs.yabn.YabnOps;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.yabn.YabnParser;
import com.teamresourceful.yabn.elements.YabnElement;
import com.teamresourceful.yabn.reader.ByteReader;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SyncQuestsPacket(Map<String, Quest> quests, List<String> groups) implements Packet<SyncQuestsPacket> {

    public static final ClientboundPacketType<SyncQuestsPacket> TYPE = new Type();

    @Override
    public PacketType<SyncQuestsPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<SyncQuestsPacket> {
        private static final Codec<Map<String, Quest>> QUEST_MAP_CODEC = Codec.unboundedMap(Codec.STRING, Quest.CODEC);

        @Override
        public ResourceLocation id() {
            return ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "sync_quests");
        }

        @Override
        public void encode(SyncQuestsPacket message, RegistryFriendlyByteBuf buffer) {
            DataResult<YabnElement> result = QUEST_MAP_CODEC.encodeStart(YabnOps.COMPRESSED, message.quests());
            Optional<YabnElement> optional = result.result();
            optional.ifPresent(yabnElement -> buffer.writeBytes(yabnElement.toFullData()));
            buffer.writeCollection(message.groups(), FriendlyByteBuf::writeUtf);
        }

        @Override
        public SyncQuestsPacket decode(RegistryFriendlyByteBuf buffer) {
            YabnElement element = YabnParser.parse(new ByteBufByteReader(buffer));
            try {
                return new SyncQuestsPacket(
                    QUEST_MAP_CODEC.parse(RegistryOps.create(YabnOps.COMPRESSED, Heracles.getRegistryAccess()), element).getOrThrow(),
                    buffer.readList(FriendlyByteBuf::readUtf)
                );
            } catch (Exception e) {
                Heracles.LOGGER.error("Failed to decode sync quests packet: {}", element, e);
                throw e;
            }
        }


        @Override
        public Runnable handle(SyncQuestsPacket message) {
            return () -> ClientQuests.sync(message.quests(), message.groups());
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
