package earth.terrarium.heracles.common.network.packets.quests;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.codecs.yabn.YabnOps;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import com.teamresourceful.yabn.YabnParser;
import com.teamresourceful.yabn.elements.YabnElement;
import com.teamresourceful.yabn.reader.ByteReader;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.groups.Group;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record SyncQuestsPacket(Map<String, Quest> quests, Map<String, Group> groups) implements Packet<SyncQuestsPacket> {

    public static final ClientboundPacketType<SyncQuestsPacket> TYPE = new Type();

    @Override
    public PacketType<SyncQuestsPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<SyncQuestsPacket> {
        private static final Codec<Map<String, Quest>> QUEST_MAP_CODEC = Codec.unboundedMap(Codec.STRING, Quest.CODEC);
        private static final Codec<Map<String, Group>> GROUP_MAP_CODEC = Codec.unboundedMap(Codec.STRING, Group.CODEC);

        @Override
        public Class<SyncQuestsPacket> type() {
            return SyncQuestsPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "sync_quests");
        }

        @Override
        public void encode(SyncQuestsPacket message, FriendlyByteBuf buffer) {
            PacketHelper.writeWithRegistryYabn(Heracles.getRegistryAccess(), buffer, QUEST_MAP_CODEC, message.quests(), true);
            PacketHelper.writeWithRegistryYabn(Heracles.getRegistryAccess(), buffer, GROUP_MAP_CODEC, message.groups(), true);
        }

        @Override
        public SyncQuestsPacket decode(FriendlyByteBuf buffer) {
            YabnElement quests = YabnParser.parse(new ByteBufByteReader(buffer));
            YabnElement groups = YabnParser.parse(new ByteBufByteReader(buffer));
            try {
                return new SyncQuestsPacket(
                    QUEST_MAP_CODEC.parse(RegistryOps.create(YabnOps.COMPRESSED, Heracles.getRegistryAccess()), quests).get().orThrow(),
                    GROUP_MAP_CODEC.parse(RegistryOps.create(YabnOps.COMPRESSED, Heracles.getRegistryAccess()), groups).get().orThrow()
                );
            }catch (Exception e) {
                Heracles.LOGGER.error("Failed to decode sync quests packet");
                Heracles.LOGGER.error("Quests: " + quests);
                Heracles.LOGGER.error("Groups: " + groups);
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
