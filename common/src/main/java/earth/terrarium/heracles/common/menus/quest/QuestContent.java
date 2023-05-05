package earth.terrarium.heracles.common.menus.quest;

import com.teamresourceful.resourcefullib.common.menu.MenuContent;
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer;
import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import earth.terrarium.heracles.api.Quest;
import earth.terrarium.heracles.common.handlers.QuestProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record QuestContent(ResourceLocation id, Quest quest,
                           QuestProgress progress) implements MenuContent<QuestContent> {

    public static final MenuContentSerializer<QuestContent> SERIALIZER = new Serializer();

    @Override
    public MenuContentSerializer<QuestContent> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements MenuContentSerializer<QuestContent> {

        @Override
        public @Nullable QuestContent from(FriendlyByteBuf buffer) {
            return new QuestContent(
                buffer.readResourceLocation(),
                PacketHelper.readWithYabn(buffer, Quest.CODEC, true).getOrThrow(false, System.err::println),
                PacketHelper.readWithYabn(buffer, QuestProgress.CODEC, true).getOrThrow(false, System.err::println)
            );
        }

        @Override
        public void to(FriendlyByteBuf buffer, QuestContent content) {
            buffer.writeResourceLocation(content.id());
            PacketHelper.writeWithYabn(buffer, Quest.CODEC, content.quest(), true);
            PacketHelper.writeWithYabn(buffer, QuestProgress.CODEC, content.progress(), true);
        }
    }
}
