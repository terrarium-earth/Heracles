package earth.terrarium.heracles.common.menus.quest;

import com.teamresourceful.resourcefullib.common.menu.MenuContent;
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record QuestContent(ResourceLocation id, boolean editing) implements MenuContent<QuestContent> {

    public static final MenuContentSerializer<QuestContent> SERIALIZER = new Serializer();

    @Override
    public MenuContentSerializer<QuestContent> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements MenuContentSerializer<QuestContent> {

        @Override
        public @Nullable QuestContent from(FriendlyByteBuf buffer) {
            return new QuestContent(buffer.readResourceLocation(), buffer.readBoolean());
        }

        @Override
        public void to(FriendlyByteBuf buffer, QuestContent content) {
            buffer.writeResourceLocation(content.id());
            buffer.writeBoolean(content.editing());
        }
    }
}
