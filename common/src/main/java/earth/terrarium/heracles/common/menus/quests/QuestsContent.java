package earth.terrarium.heracles.common.menus.quests;

import com.teamresourceful.resourcefullib.common.menu.MenuContent;
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record QuestsContent(boolean canEdit) implements MenuContent<QuestsContent> {

    public static final MenuContentSerializer<QuestsContent> SERIALIZER = new Serializer();

    @Override
    public MenuContentSerializer<QuestsContent> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements MenuContentSerializer<QuestsContent> {

        @Override
        public @Nullable QuestsContent from(FriendlyByteBuf buffer) {
            return new QuestsContent(buffer.readBoolean());
        }

        @Override
        public void to(FriendlyByteBuf buffer, QuestsContent content) {
            buffer.writeBoolean(content.canEdit());
        }
    }
}
