package earth.terrarium.heracles.api.client.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.color.Color;

public record ToastsTheme(
    Color title,
    Color claimedTitle,
    Color tutorialTitle,
    Color content,
    Color tutorialContent,
    Color keybinding
) {

    public static final ToastsTheme DEFAULT = new ToastsTheme(
        new Color(0xB52CC8),
        new Color(0x800080),
        new Color(0x404040),
        new Color(0xFFFFFF),
        new Color(0x808080),
        new Color(0xA0A0A0)
    );

    public static final Codec<ToastsTheme> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Color.CODEC.fieldOf("title").orElse(DEFAULT.title()).forGetter(ToastsTheme::title),
        Color.CODEC.fieldOf("claimedTitle").orElse(DEFAULT.claimedTitle()).forGetter(ToastsTheme::claimedTitle),
        Color.CODEC.fieldOf("tutorialTitle").orElse(DEFAULT.tutorialTitle()).forGetter(ToastsTheme::tutorialTitle),
        Color.CODEC.fieldOf("content").orElse(DEFAULT.content()).forGetter(ToastsTheme::content),
        Color.CODEC.fieldOf("tutorialContent").orElse(DEFAULT.tutorialContent()).forGetter(ToastsTheme::tutorialContent),
        Color.CODEC.fieldOf("keybinding").orElse(DEFAULT.keybinding()).forGetter(ToastsTheme::keybinding)
    ).apply(instance, ToastsTheme::new));

    public static int getTitle() {
        return Theme.getInstance().toasts().title().getValue();
    }

    public static int getClaimedTitle() {
        return Theme.getInstance().toasts().claimedTitle().getValue();
    }

    public static int getTutorialTitle() {
        return Theme.getInstance().toasts().tutorialTitle().getValue();
    }

    public static int getContent() {
        return Theme.getInstance().toasts().content().getValue();
    }

    public static int getTutorialContent() {
        return Theme.getInstance().toasts().tutorialContent().getValue();
    }

    public static int getKeybinding() {
        return Theme.getInstance().toasts().keybinding().getValue();
    }
}
