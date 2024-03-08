package earth.terrarium.heracles.api.client.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.color.Color;

public record ModalsTheme(
    Color title,
    Color rewardsAmount
) {

    public static final ModalsTheme DEFAULT = new ModalsTheme(
        new Color(0xFEFEFE),
        new Color(0x404040)
    );

    public static final Codec<ModalsTheme> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Color.CODEC.fieldOf("title").orElse(DEFAULT.title()).forGetter(ModalsTheme::title),
        Color.CODEC.fieldOf("rewardsAmount").orElse(DEFAULT.rewardsAmount()).forGetter(ModalsTheme::rewardsAmount)
    ).apply(instance, ModalsTheme::new));

    public static int getTitle() {
        return Theme.getInstance().modals().title().getValue();
    }

    public static int getRewardsAmount() {
        return Theme.getInstance().modals().rewardsAmount().getValue();
    }
}
