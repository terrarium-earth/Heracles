package earth.terrarium.heracles.common.menus.quest;

import com.teamresourceful.resourcefullib.common.menu.MenuContent;
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import earth.terrarium.heracles.common.utils.PacketHelper;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record QuestContent(String id,
                           Quest quest,
                           QuestProgress progress,
                           Map<String, ModUtils.QuestStatus> quests
) implements MenuContent<QuestContent> {

    public static final MenuContentSerializer<QuestContent> SERIALIZER = new Serializer();

    @Override
    public MenuContentSerializer<QuestContent> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements MenuContentSerializer<QuestContent> {

        @Override
        public @Nullable QuestContent from(FriendlyByteBuf buffer) {
            String id = buffer.readUtf();
            Quest quest = PacketHelper.readWithYabn(Heracles.getRegistryAccess(), buffer, Quest.CODEC, true)
                .getOrThrow(false, System.err::println);
            QuestProgress progress = new QuestProgress(quest, buffer.readNbt());
            Map<String, ModUtils.QuestStatus> quests = new HashMap<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                quests.put(buffer.readUtf(), buffer.readEnum(ModUtils.QuestStatus.class));
            }
            return new QuestContent(id, quest, progress, quests);
        }

        @Override
        public void to(FriendlyByteBuf buffer, QuestContent content) {
            buffer.writeUtf(content.id());
            PacketHelper.writeWithYabn(Heracles.getRegistryAccess(), buffer, Quest.CODEC, content.quest(), true);
            buffer.writeNbt(content.progress().save());
            buffer.writeVarInt(content.quests.size());
            for (var entry : content.quests.entrySet()) {
                buffer.writeUtf(entry.getKey());
                buffer.writeEnum(entry.getValue());
            }
        }
    }
}
