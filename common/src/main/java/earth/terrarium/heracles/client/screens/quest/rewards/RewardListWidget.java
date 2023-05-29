package earth.terrarium.heracles.client.screens.quest.rewards;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.client.QuestRewardWidgets;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.screens.quest.AddDisplayWidget;
import earth.terrarium.heracles.client.screens.quest.HeadingWidget;
import earth.terrarium.heracles.client.utils.MouseClick;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class RewardListWidget extends AbstractContainerEventHandler implements Renderable {

    private final List<MutablePair<QuestReward<?>, DisplayWidget>> widgets = new ArrayList<>();

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private final String questId;
    private final Quest quest;

    private double scrollAmount;
    private int lastFullHeight;

    private MouseClick mouse = null;

    private final BiConsumer<QuestReward<?>, Boolean> onClick;
    private final Runnable onCreate;

    public RewardListWidget(
        int x, int y, int width, int height,
        String questId, Quest quest,
        BiConsumer<QuestReward<?>, Boolean> onClick, Runnable onCreate
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lastFullHeight = this.height;
        this.questId = questId;
        this.quest = quest;
        this.onClick = onClick;
        this.onCreate = onCreate;
    }

    public void update(String id, Quest quest) {
        this.widgets.clear();
        if (!quest.rewards().isEmpty()) {
            this.widgets.add(new MutablePair<>(null, new HeadingWidget(Component.nullToEmpty("Rewards"), 0xFF00DD00)));
            for (QuestReward<?> reward : quest.rewards().values()) {
                DisplayWidget widget = QuestRewardWidgets.create(reward);
                if (widget == null) continue;
                this.widgets.add(new MutablePair<>(reward, widget));
            }
        }
        ClientQuests.get(id).ifPresent(entry -> {
            if (entry.children().isEmpty()) return;
            this.widgets.add(new MutablePair<>(null, new HeadingWidget(Component.nullToEmpty("Dependents"), 0xFF000080)));
            for (ClientQuests.QuestEntry dependent : entry.children()) {
                this.widgets.add(new MutablePair<>(null, new QuestDependentWidget(dependent.value())));
            }
        });
        if (this.onCreate != null) {
            this.widgets.add(new MutablePair<>(null, new AddDisplayWidget(this.onCreate)));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = this.x;
        int y = this.y;

        int fullHeight = 0;
        Pair<QuestReward<?>, Boolean> clicked = null;

        try (var scissor = RenderUtils.createScissor(Minecraft.getInstance(), graphics, x, y, width + 30, height)) {
            for (var pair : this.widgets) {
                var widget = pair.getRight();
                if (this.mouse != null && widget.mouseClicked(this.mouse.x() - x, this.mouse.y() - (y - this.scrollAmount), this.mouse.button(), this.width)) {
                    this.mouse = null;
                }

                int tempY = y - (int) this.scrollAmount;

                widget.render(graphics, scissor.stack(), x, tempY, this.width, mouseX, mouseY, this.isMouseOver(mouseX, mouseY), partialTick);

                var itemheight = widget.getHeight(this.width);

                boolean hovered = mouseX > x && mouseX < x + this.width + 14 && mouseY > tempY && mouseY < tempY + itemheight;

                if (hovered && pair.left != null && this.onClick != null) {
                    boolean editHovered = mouseX > x + width + 1 && mouseX < x + width + 12 && mouseY > tempY + 1 && mouseY < tempY + 12;
                    graphics.blit(AbstractQuestScreen.HEADING, x + width + 1, tempY + 1, 33, editHovered ? 26 : 15, 11, 11, 256, 256);
                    CursorUtils.setCursor(editHovered, CursorScreen.Cursor.POINTER);
                    if (mouse != null && mouse.x() > x + width + 1 && mouse.x() < x + width + 12 && mouse.y() > tempY + 1 && mouse.y() < tempY + 12 && mouse.button() == 0) {
                        clicked = Pair.of(pair.left, false);
                    }

                    boolean removeHovered = mouseX > x + width + 1 && mouseX < x + width + 12 && mouseY > tempY + 13 && mouseY < tempY + 24;
                    graphics.blit(AbstractQuestScreen.HEADING, x + width + 1, tempY + 13, 11, removeHovered ? 70 : 59, 11, 11, 256, 256);
                    CursorUtils.setCursor(removeHovered, CursorScreen.Cursor.POINTER);
                    if (mouse != null && mouse.x() > x + width + 1 && mouse.x() < x + width + 12 && mouse.y() > tempY + 13 && mouse.y() < tempY + 24 && mouse.button() == 0) {
                        clicked = Pair.of(pair.left, true);
                    }
                }

                y += itemheight;
                fullHeight += itemheight;
            }
            if (clicked != null) {
                this.onClick.accept(clicked.getLeft(), clicked.getRight());
            }

            this.mouse = null;
            this.lastFullHeight = fullHeight;
        }
        this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0D, Math.max(0, this.lastFullHeight - this.height));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        this.scrollAmount = Mth.clamp(this.scrollAmount - scrollAmount * 10, 0.0D, Math.max(0, this.lastFullHeight - this.height));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            this.mouse = new MouseClick(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width + 30 && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of();
    }

    public void updateReward(QuestReward<?> reward) {
        for (var pair : this.widgets) {
            if (pair.left != null && pair.left.id().equals(reward.id())) {
                var widget = QuestRewardWidgets.create(ModUtils.cast(reward));
                if (widget != null) {
                    pair.left = reward;
                    pair.right = widget;
                }
                break;
            }
        }
        this.quest.rewards().put(reward.id(), reward);
        ClientQuests.setDirty(this.questId);
        ClientQuests.get(this.questId).ifPresent(entry -> entry.value().rewards().put(reward.id(), reward));
    }
}
