package earth.terrarium.heracles.api.client.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.color.Color;

public record PinnedQuestsTheme(
    Color title,
    Color quest,
    Color task
) {

    public static final PinnedQuestsTheme DEFAULT = new PinnedQuestsTheme(
        new Color(0x808080),
        new Color(0xFFFFFF),
        new Color(0xFFFFFF)
    );

    public static final Codec<PinnedQuestsTheme> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Color.CODEC.fieldOf("title").orElse(DEFAULT.title()).forGetter(PinnedQuestsTheme::title),
        Color.CODEC.fieldOf("quest").orElse(DEFAULT.quest()).forGetter(PinnedQuestsTheme::quest),
        Color.CODEC.fieldOf("task").orElse(DEFAULT.task()).forGetter(PinnedQuestsTheme::task)
    ).apply(instance, PinnedQuestsTheme::new));

    public static int getTitle() {
        return Theme.getInstance().pinnedQuests().title().getValue();
    }

    public static int getQuest() {
        return Theme.getInstance().pinnedQuests().quest().getValue();
    }

    public static int getTask() {
        return Theme.getInstance().pinnedQuests().task().getValue();
    }
}
