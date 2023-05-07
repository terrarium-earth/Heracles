package earth.terrarium.heracles.common.menus.quests;

import com.teamresourceful.resourcefullib.common.menu.MenuContent;
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record QuestsContent(
    String group,
    Object2BooleanMap<String> quests,
    boolean canEdit
) implements MenuContent<QuestsContent> {

    public static final MenuContentSerializer<QuestsContent> SERIALIZER = new Serializer();

    @Override
    public MenuContentSerializer<QuestsContent> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements MenuContentSerializer<QuestsContent> {

        @Override
        public @Nullable QuestsContent from(FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            Object2BooleanMap<String> quests = new Object2BooleanOpenHashMap<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                quests.put(buffer.readUtf(), buffer.readBoolean());
            }
            return new QuestsContent(group, quests, buffer.readBoolean());
        }

        @Override
        public void to(FriendlyByteBuf buffer, QuestsContent content) {
            buffer.writeUtf(content.group);
            buffer.writeVarInt(content.quests.size());
            for (Object2BooleanMap.Entry<String> entry : Object2BooleanMaps.fastIterable(content.quests)) {
                buffer.writeUtf(entry.getKey());
                buffer.writeBoolean(entry.getBooleanValue());
            }
            buffer.writeBoolean(content.canEdit());
        }
    }
}
