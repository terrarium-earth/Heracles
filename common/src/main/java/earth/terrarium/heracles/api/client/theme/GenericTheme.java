package earth.terrarium.heracles.api.client.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.color.Color;

public record GenericTheme(
    Color buttonMessageActive,
    Color buttonMessageInactive
) {

    public static final GenericTheme DEFAULT = new GenericTheme(
        new Color(0xFFFFFF),
        new Color(0xA0A0A0)
    );

    public static final Codec<GenericTheme> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Color.CODEC.fieldOf("buttonMessageActive").orElse(DEFAULT.buttonMessageActive()).forGetter(GenericTheme::buttonMessageActive),
        Color.CODEC.fieldOf("buttonMessageInactive").orElse(DEFAULT.buttonMessageInactive()).forGetter(GenericTheme::buttonMessageInactive)
    ).apply(instance, GenericTheme::new));

    public static int getButtonMessage(boolean active) {
        GenericTheme theme = Theme.getInstance().generic();
        return active ? theme.buttonMessageActive().getValue() : theme.buttonMessageInactive().getValue();
    }
}
