package earth.terrarium.heracles.api.client.settings;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface SettingsProvider<T> {

    ResourceLocation id();

    @Nullable
    default SettingInitializer<T> factory() {
        return Settings.getFactory(this);
    }
}
