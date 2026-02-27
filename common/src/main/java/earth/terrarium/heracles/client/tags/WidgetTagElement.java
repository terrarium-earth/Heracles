package earth.terrarium.heracles.client.tags;

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
import net.minecraft.client.gui.GuiGraphics;

import java.util.Map;

public record WidgetTagElement(DisplayWidget widget) implements TagElement {

    public static WidgetTagElement ofTask(Quest quest, String questId, Map<String, String> parameters) {
        if (quest == null) return new WidgetTagElement(null);
        var task = quest.tasks().get(parameters.get("task"));
        if (task == null) return new WidgetTagElement(null);
        QuestProgress progress = ClientQuests.getProgress(questId);
        ModUtils.QuestStatus status = ClientQuests.getStatus(questId).orElse(ModUtils.QuestStatus.LOCKED);
        return new WidgetTagElement(QuestTaskWidgets.create(questId, ModUtils.cast(task), progress.getTask(task), status));
    }

    public static WidgetTagElement ofReward(Quest quest, Map<String, String> parameters) {
        if (quest == null) return new WidgetTagElement(null);
        var reward = quest.rewards().get(parameters.get("reward"));
        if (reward == null) return new WidgetTagElement(null);
        return new WidgetTagElement(QuestRewardWidgets.create(reward));
    }

    @Override
    public void render(Theme theme, GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        if (widget == null) return;
        widget.render(graphics, new ScissorBoxStack(), x, y, width, mouseX, mouseY, hovered, partialTicks);
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
