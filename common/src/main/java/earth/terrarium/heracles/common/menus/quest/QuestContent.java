package earth.terrarium.heracles.common.menus.quest;

import com.teamresourceful.resourcefullib.common.menu.MenuContent;
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.utils.PacketHelper;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record QuestContent(String id, Quest quest, QuestProgress progress) implements MenuContent<QuestContent> {

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
            QuestProgress progress = PacketHelper.readWithYabn(Heracles.getRegistryAccess(), buffer, QuestProgress.codec(quest), true)
                .getOrThrow(false, System.err::println);
            return new QuestContent(id, quest, progress);
        }

        @Override
        public void to(FriendlyByteBuf buffer, QuestContent content) {
            buffer.writeUtf(content.id());
            PacketHelper.writeWithYabn(Heracles.getRegistryAccess(), buffer, Quest.CODEC, content.quest(), true);
            PacketHelper.writeWithYabn(Heracles.getRegistryAccess(), buffer, QuestProgress.codec(content.quest()), content.progress(), true);
        }
    }
}
