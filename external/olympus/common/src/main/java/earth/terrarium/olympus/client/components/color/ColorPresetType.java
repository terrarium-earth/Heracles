package earth.terrarium.olympus.client.components.color;

import earth.terrarium.olympus.client.utils.Translatable;

import java.util.Locale;

public enum ColorPresetType implements Translatable {
    RECENTS,
    DEFAULTS,
    MC_COLORS,
    ;

    public static final ColorPresetType[] VALUES = values();
    public static final ColorPresetType[] WITHOUT_DEFAULT = {RECENTS, MC_COLORS};

    @Override
    public String getTranslationKey() {
        return "olympus.color.preset." + this.name().toLowerCase(Locale.ROOT);
    }
}