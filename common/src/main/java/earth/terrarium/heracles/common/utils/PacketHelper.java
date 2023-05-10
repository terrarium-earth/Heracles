package earth.terrarium.heracles.common.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefullib.common.codecs.yabn.YabnOps;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.yabn.YabnParser;
import com.teamresourceful.yabn.elements.YabnElement;
import com.teamresourceful.yabn.reader.ByteReader;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryOps;

import java.util.Optional;

public final class PacketHelper {

    private PacketHelper() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static <T> DataResult<YabnElement> writeWithYabn(RegistryAccess access, FriendlyByteBuf buf, Codec<T> codec, T object, boolean compressed) {
        RegistryOps<YabnElement> ops = RegistryOps.create(compressed ? YabnOps.COMPRESSED : YabnOps.INSTANCE, access);
        DataResult<YabnElement> result = codec.encodeStart(ops, object);
        Optional<YabnElement> optional = result.result();
        if (optional.isPresent()) {
            buf.writeBytes(optional.get().toFullData());
            return result;
        }
        return result;
    }

    public static <T> DataResult<T> readWithYabn(RegistryAccess access, FriendlyByteBuf buf, Codec<T> codec, boolean compressed) {
        try {
            RegistryOps<YabnElement> ops = RegistryOps.create(compressed ? YabnOps.COMPRESSED : YabnOps.INSTANCE, access);
            return codec.parse(ops, YabnParser.parse(new ByteBufByteReader(buf)));
        } catch (Exception e) {
            return DataResult.error(e::getMessage);
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