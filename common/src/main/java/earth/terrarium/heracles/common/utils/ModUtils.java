package earth.terrarium.heracles.common.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.utils.ByteBufUtils;
import com.teamresourceful.resourcefullib.common.codecs.yabn.YabnOps;
import com.teamresourceful.yabn.YabnParser;
import com.teamresourceful.yabn.elements.YabnElement;
import com.teamresourceful.yabn.reader.ArrayByteReader;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.screens.OpenQuestScreenPacket;
import earth.terrarium.heracles.common.network.packets.screens.OpenQuestsScreenPacket;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ModUtils {

    private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    public static final Codec<Vector2i> VECTOR2I = Codec.INT
        .listOf()
        .comapFlatMap(
            list -> Util.fixedSize(list, 2).map(listx -> new Vector2i(listx.get(0), listx.get(1))),
            vector3f -> List.of(vector3f.x(), vector3f.y())
        );

    public static final ByteCodec<Vector2i> VECTOR2I_BYTE_CODEC = ByteCodec.passthrough((bytebuf, vector) -> {
        bytebuf.writeInt(vector.x());
        bytebuf.writeInt(vector.y());
    }, bytebuf -> new Vector2i(bytebuf.readInt(), bytebuf.readInt()));

    public static <T> Predicate<T> predicateTrue() {
        return value -> true;
    }

    public static <T> Predicate<T> predicateFalse() {
        return value -> false;
    }

    public static <T> List<T> getValue(ResourceKey<? extends Registry<T>> key, TagKey<T> tag) {
        return Heracles.getRegistryAccess().registry(key)
            .map(registry ->
                registry.getTag(tag).map(v ->
                    v.stream().filter(Holder::isBound).map(Holder::value).toList()
                ).orElse(List.of())
            )
            .orElse(List.of());
    }

    @SuppressWarnings("unchecked")
    public static <T, U> U cast(T value) {
        return (U) value;
    }

    public static void openQuest(ServerPlayer player, String group, String id) {
        NetworkHandler.CHANNEL.sendToPlayer(new OpenQuestScreenPacket(
            new QuestContent(
                id,
                group,
                QuestProgressHandler.getProgress(player.server, player.getUUID()).getProgress(id),
                getQuests(player)
            )
        ), player);
    }

    public static void openGroup(ServerPlayer player, String group) {
        if (!QuestHandler.groups().contains(group)) {
            player.sendSystemMessage(Component.translatable("gui.heracles.error.group.not_found", group));
            return;
        }
        NetworkHandler.CHANNEL.sendToPlayer(new OpenQuestsScreenPacket(
            new QuestsContent(group, getQuests(player), player.hasPermissions(2))
        ), player);
    }

    private static Map<String, QuestStatus> getQuests(ServerPlayer player) {
        Map<String, QuestStatus> quests = new HashMap<>();
        QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
        for (String quest : progress.completableQuests().getQuests(progress)) {
            quests.put(quest, QuestStatus.IN_PROGRESS);
        }
        QuestHandler.quests().forEach((id, quest) -> {
            if (!quests.containsKey(id)) {
                quests.put(id, progress.isComplete(id) ? (progress.isClaimed(id, quest) ? QuestStatus.COMPLETED_CLAIMED : QuestStatus.COMPLETED) : QuestStatus.LOCKED);
            }
        });
        return quests;
    }

    public enum QuestStatus implements StringRepresentable {
        LOCKED,
        IN_PROGRESS,
        COMPLETED,
        COMPLETED_CLAIMED;

        public static final int SIZE = values().length;

        public boolean isComplete() {
            return this == COMPLETED || this == COMPLETED_CLAIMED;
        }

        @Override
        public @NotNull String getSerializedName() {
            return "quest.heracles.%s".formatted(name().toLowerCase(Locale.ROOT));
        }
    }

    public static String findAvailableFolderName(String folderName) {
        folderName = folderName.replaceAll("[./\"]", "_");
        for (String string : INVALID_FILE_NAMES) {
            if (folderName.equalsIgnoreCase(string)) {
                folderName = "_" + folderName + "_";
            }
        }

        return folderName;
    }

    public static <B> ByteCodec<B> toByteCodec(Codec<B> codec) {
        return toByteCodec(codec, "Failed to find element", "Failed to parse element");
    }

    public static <B> ByteCodec<B> toByteCodec(Codec<B> codec, String notFound, String failedToParse) {
        return ByteCodec.passthrough((buf, item) -> {
            DataResult<YabnElement> result = codec.encodeStart(RegistryOps.create(YabnOps.COMPRESSED, Heracles.getRegistryAccess()), item);
            Optional<YabnElement> optional = result.result();
            optional.ifPresentOrElse(element -> {
                byte[] bytes = element.toFullData();
                buf.writeBoolean(true);
                ByteBufUtils.writeVarInt(buf, bytes.length);
                buf.writeBytes(bytes);
            }, () -> buf.writeBoolean(false));
        }, buf -> {
            if (buf.readBoolean()) {
                int length = ByteBufUtils.readVarInt(buf);
                byte[] bytes = new byte[length];
                buf.readBytes(bytes);
                return codec.parse(RegistryOps.create(YabnOps.COMPRESSED, Heracles.getRegistryAccess()), YabnParser.parse(new ArrayByteReader(bytes)))
                    .result()
                    .orElseThrow(() -> new RuntimeException(failedToParse));
            }
            throw new RuntimeException(notFound);
        });
    }

    public static <T, O> O throwStackoverflow(T t, Function<T, O> mapper) {
        try {
            return mapper.apply(t);
        } catch (StackOverflowError e) {
            throw new RuntimeException("Stackoverflow error while parsing: " + t);
        }
    }

    public static String safeSubstring(String string, int start, int end) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        if (start < 0) {
            start = 0;
        }
        if (end > string.length()) {
            end = string.length();
        }
        return string.substring(start, end);
    }
}
