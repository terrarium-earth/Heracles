package earth.terrarium.heracles.api.quests;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum QuestDisplayStatus implements StringRepresentable {
    LOCKED,
    IN_PROGRESS,
    COMPLETED,
    DEPENDENCIES_VISIBLE,
    ;

    @Override
    public @NotNull String getSerializedName() {
        return "quest.heracles.%s".formatted(name().toLowerCase(Locale.ROOT));
    }
}
