package earth.terrarium.heracles.client.tags;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.rewards.client.QuestRewardWidgets;
import earth.terrarium.heracles.api.tasks.client.QuestTaskWidgets;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import earth.terrarium.hermes.api.TagElement;
import earth.terrarium.hermes.api.themes.Theme;

import java.util.Map;

public record WidgetTagElement(DisplayWidget widget) implements TagElement {

    public static WidgetTagElement ofTask(Map<String, String> parameters) {
        String questId = parameters.get("quest");
        Quest quest = ClientQuests.get(questId).map(ClientQuests.QuestEntry::value).orElse(null);
        if (quest == null) return new WidgetTagElement(null);
        var task = quest.tasks().get(parameters.get("task"));
        if (task == null) return new WidgetTagElement(null);
        QuestProgress progress = ClientQuests.getProgress(questId);
        return new WidgetTagElement(QuestTaskWidgets.create(ModUtils.cast(task), progress.getTask(task)));
    }

    public static WidgetTagElement ofReward(Map<String, String> parameters) {
        String questId = parameters.get("quest");
        Quest quest = ClientQuests.get(questId).map(ClientQuests.QuestEntry::value).orElse(null);
        if (quest == null) return new WidgetTagElement(null);
        var reward = quest.rewards().get(parameters.get("reward"));
        if (reward == null) return new WidgetTagElement(null);
        return new WidgetTagElement(QuestRewardWidgets.create(reward));
    }

    @Override
    public void render(Theme theme, PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        if (widget == null) return;
        widget.render(pose, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
    }

    @Override
    public int getHeight(int width) {
        if (widget == null) return 0;
        return widget.getHeight(width);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int width) {
        if (widget == null) return false;
        return widget.mouseClicked(mouseX, mouseY, button, width);
    }
}
