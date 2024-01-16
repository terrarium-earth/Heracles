package earth.terrarium.heracles.api.client.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.color.Color;

public record GenericTheme(
    Color buttonActive,
    Color buttonInactive
) {

    public static final GenericTheme DEFAULT = new GenericTheme(
        new Color(0xFFFFFF),
        new Color(0xA0A0A0)
    );

    public static final Codec<GenericTheme> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Color.CODEC.fieldOf("buttonActive").orElse(DEFAULT.buttonActive()).forGetter(GenericTheme::buttonActive),
        Color.CODEC.fieldOf("buttonInactive").orElse(DEFAULT.buttonInactive()).forGetter(GenericTheme::buttonInactive)
    ).apply(instance, GenericTheme::new));

    public static int getButton(boolean active) {
        GenericTheme theme = Theme.getInstance().generic();
        return active ? theme.buttonActive().getValue() : theme.buttonInactive().getValue();
    }
}
