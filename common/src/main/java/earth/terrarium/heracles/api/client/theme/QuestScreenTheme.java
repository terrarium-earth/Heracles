package earth.terrarium.heracles.api.client.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.color.Color;

public record QuestScreenTheme(
    Color taskTitle,
    Color taskDescription,
    Color taskProgress,
    Color taskNestedTitle,
    Color taskSubmit,
    Color taskSubmitDisabled,
    Color rewardTitle,
    Color rewardDescription,
    Color taskRewardStatusHeading,
    Color summaryTitle,
    Color summaryProgress,
    Color summaryDescription,
    Color tabButton,
    Color tabButtonSelected
) {

    public static final QuestScreenTheme DEFAULT = new QuestScreenTheme(
        new Color(0xFFFFFF),
        new Color(0x808080),
        new Color(0xFFFFFF),
        new Color(0xA0A0A0),
        new Color(0xD0D0D0),
        new Color(0x707070),
        new Color(0xFFFFFF),
        new Color(0x808080),
        new Color(0x1E1E1E),
        new Color(0xFFFFFF),
        new Color(0xFFFFFF),
        new Color(0x696969),
        new Color(0xFFFFFF),
        new Color(0xEEEEEE)
    );

    public static final Codec<QuestScreenTheme> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Color.CODEC.fieldOf("taskTitle").orElse(DEFAULT.taskTitle()).forGetter(QuestScreenTheme::taskTitle),
        Color.CODEC.fieldOf("taskDescription").orElse(DEFAULT.taskDescription()).forGetter(QuestScreenTheme::taskDescription),
        Color.CODEC.fieldOf("taskProgress").orElse(DEFAULT.taskProgress()).forGetter(QuestScreenTheme::taskProgress),
        Color.CODEC.fieldOf("taskNestedTitle").orElse(DEFAULT.taskNestedTitle()).forGetter(QuestScreenTheme::taskNestedTitle),
        Color.CODEC.fieldOf("taskSubmit").orElse(DEFAULT.taskSubmit()).forGetter(QuestScreenTheme::taskSubmit),
        Color.CODEC.fieldOf("taskSubmitDisabled").orElse(DEFAULT.taskSubmitDisabled()).forGetter(QuestScreenTheme::taskSubmitDisabled),
        Color.CODEC.fieldOf("rewardTitle").orElse(DEFAULT.rewardTitle()).forGetter(QuestScreenTheme::rewardTitle),
        Color.CODEC.fieldOf("rewardDescription").orElse(DEFAULT.rewardDescription()).forGetter(QuestScreenTheme::rewardDescription),
        Color.CODEC.fieldOf("taskRewardStatusHeading").orElse(DEFAULT.taskRewardStatusHeading()).forGetter(QuestScreenTheme::taskRewardStatusHeading),
        Color.CODEC.fieldOf("summaryTitle").orElse(DEFAULT.summaryTitle()).forGetter(QuestScreenTheme::summaryTitle),
        Color.CODEC.fieldOf("summaryProgress").orElse(DEFAULT.summaryProgress()).forGetter(QuestScreenTheme::summaryProgress),
        Color.CODEC.fieldOf("summaryDescription").orElse(DEFAULT.summaryDescription()).forGetter(QuestScreenTheme::summaryDescription),
        Color.CODEC.fieldOf("tabButton").orElse(DEFAULT.tabButton()).forGetter(QuestScreenTheme::tabButton),
        Color.CODEC.fieldOf("tabButtonSelected").orElse(DEFAULT.tabButtonSelected()).forGetter(QuestScreenTheme::tabButtonSelected)
    ).apply(instance, QuestScreenTheme::new));

    public static int getTaskTitle() {
        return Theme.getInstance().questScreen().taskTitle().getValue();
    }

    public static int getTaskDescription() {
        return Theme.getInstance().questScreen().taskDescription().getValue();
    }

    public static int getTaskProgress() {
        return Theme.getInstance().questScreen().taskProgress().getValue();
    }

    public static int getTaskNestedTitle() {
        return Theme.getInstance().questScreen().taskNestedTitle().getValue();
    }

    public static int getTaskSubmit(boolean disabled) {
        return (disabled ? Theme.getInstance().questScreen().taskSubmitDisabled() : Theme.getInstance().questScreen().taskSubmit()).getValue();
    }

    public static int getRewardTitle() {
        return Theme.getInstance().questScreen().rewardTitle().getValue();
    }

    public static int getRewardDescription() {
        return Theme.getInstance().questScreen().rewardDescription().getValue();
    }

    public static int getTaskRewardStatusHeading() {
        return Theme.getInstance().questScreen().taskRewardStatusHeading().getValue();
    }

    public static int getSummaryTitle() {
        return Theme.getInstance().questScreen().summaryTitle().getValue();
    }

    public static int getSummaryProgress() {
        return Theme.getInstance().questScreen().summaryProgress().getValue();
    }

    public static int getSummaryDescription() {
        return Theme.getInstance().questScreen().summaryDescription().getValue();
    }

    public static int getTabButton(boolean selected) {
        return (selected ? Theme.getInstance().questScreen().tabButtonSelected() : Theme.getInstance().questScreen().tabButton()).getValue();
    }
}
