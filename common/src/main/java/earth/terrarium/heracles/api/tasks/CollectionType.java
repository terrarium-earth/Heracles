package earth.terrarium.heracles.api.tasks;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum CollectionType implements StringRepresentable {
    MANUAL,
    AUTOMATIC,
    CONSUME;

    @Override
    public @NotNull String getSerializedName() {
        return "gui.heracles.tasks.collection_type.%s".formatted(name().toLowerCase());
    }
}
