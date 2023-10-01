package earth.terrarium.heracles.api.tasks;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum CollectionType implements StringRepresentable {
    MANUAL,
    AUTOMATIC,
    CONSUME;

    @Override
    public @NotNull String getSerializedName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
