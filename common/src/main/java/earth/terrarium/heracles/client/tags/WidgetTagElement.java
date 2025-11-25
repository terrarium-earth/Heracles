package earth.terrarium.heracles.client.tags;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.rewards.client.QuestRewardWidgets;
import earth.terrarium.heracles.api.tasks.client.QuestTaskWidgets;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import earth.terrarium.hermes.api.rendering.HtmlRenderer;
import earth.terrarium.hermes.api.rendering.HtmlStyle;
import earth.terrarium.hermes.elements.base.BasicBasicElement;
import earth.terrarium.hermes.libs.minemark.LayoutData;
import earth.terrarium.hermes.libs.minemark.LayoutStyle;
import earth.terrarium.hermes.libs.minemark.elements.Element;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.Attributes;

public class WidgetTagElement extends BasicBasicElement<HtmlStyle, HtmlRenderer> {

    private final DisplayWidget widget;

    public static @Nullable DisplayWidget taskWidget(@Nullable Attributes attributes) {
        if (attributes == null) return null;

        String questId = attributes.getValue("quest");

        Quest quest = ClientQuests.get(questId).map(ClientQuests.QuestEntry::value).orElse(null);
        if (quest == null) return null;

        var task = quest.tasks().get(attributes.getValue("task"));
        if (task == null) return null;

        QuestProgress progress = ClientQuests.getProgress(questId);
        ModUtils.QuestStatus status = ClientQuests.getStatus(questId).orElse(ModUtils.QuestStatus.LOCKED);

        return QuestTaskWidgets.create(questId, ModUtils.cast(task), progress.getTask(task), status);
    }

    public static WidgetTagElement ofTask(HtmlStyle style, LayoutStyle layoutStyle, @Nullable Element<HtmlStyle, HtmlRenderer> parent, String qName, @Nullable Attributes attributes) {
        return new WidgetTagElement(taskWidget(attributes), style, layoutStyle, parent, qName, attributes);
    }

    public static @Nullable DisplayWidget rewardWidget(@Nullable Attributes attributes) {
        if (attributes == null) return null;

        String questId = attributes.getValue("quest");
        Quest quest = ClientQuests.get(questId).map(ClientQuests.QuestEntry::value).orElse(null);
        if (quest == null) return null;

        var reward = quest.rewards().get(attributes.getValue("reward"));
        if (reward == null) return null;

        return QuestRewardWidgets.create(reward);
    }

    public static WidgetTagElement ofReward(HtmlStyle style, LayoutStyle layoutStyle, @Nullable Element<HtmlStyle, HtmlRenderer> parent, String qName, @Nullable Attributes attributes) {
        return new WidgetTagElement(rewardWidget(attributes), style, layoutStyle, parent, qName, attributes);
    }

    private WidgetTagElement(@Nullable DisplayWidget widget, HtmlStyle style, LayoutStyle layoutStyle, @Nullable Element<HtmlStyle, HtmlRenderer> parent, String qName, @Nullable Attributes attributes) {
        super(style, layoutStyle, parent, qName, attributes);

        this.widget = widget;
    }

    @Override
    protected void drawElement(float x, float y, float width, float height, float mouseX, float mouseY, HtmlRenderer htmlRenderer) {
        if (widget == null) return;
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        float partialTicks = Minecraft.getInstance().getFrameTimeNs();
        widget.render(htmlRenderer.getGraphics(), new ScissorBoxStack(), (int) x, (int) y, (int) width, (int) mouseX, (int) mouseY, hovered, partialTicks);
    }

    @Override
    protected float getWidth(LayoutData layoutData, HtmlRenderer htmlRenderer) {
        return 0;
    }

    @Override
    protected float getHeight(LayoutData layoutData, HtmlRenderer htmlRenderer) {
        if (widget == null) return 0;
        return widget.getHeight((int) layoutData.getX());
    }

}
