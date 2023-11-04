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
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
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

    private static final HeadingWidget LOCKED = new HeadingWidget(Component.translatable("quest.heracles.locked"), 0xFF000080);
    private static final HeadingWidget AVAILABLE = new HeadingWidget(Component.translatable("quest.heracles.available"), 0xFF5691FF);
    private static final HeadingWidget CLAIMED = new HeadingWidget(Component.translatable("quest.heracles.claimed"), 0xFF04CB40);

    private final List<MutablePair<QuestReward<?>, DisplayWidget>> widgets = new ArrayList<>();

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final double overscrollTop;
    private final double overscrollBottom;

    private final ClientQuests.QuestEntry entry;
    private final QuestProgress progress;

    private double scrollAmount;
    private int lastFullHeight;

    private MouseClick mouse = null;

    private final BiConsumer<QuestReward<?>, Boolean> onClick;
    private final Runnable onCreate;

    public RewardListWidget(
        int x, int y, int width, int height, double overscrollTop, double overscrollBottom,
        ClientQuests.QuestEntry entry,
        QuestProgress progress,
        BiConsumer<QuestReward<?>, Boolean> onClick,
        Runnable onCreate
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.overscrollTop = overscrollTop;
        this.overscrollBottom = overscrollBottom;
        this.lastFullHeight = this.height;
        this.entry = entry;
        this.progress = progress;
        this.onClick = onClick;
        this.onCreate = onCreate;
        this.scrollAmount = -overscrollTop;
    }

    public RewardListWidget(
        int x, int y, int width, int height,
        ClientQuests.QuestEntry entry,
        QuestProgress progress,
        BiConsumer<QuestReward<?>, Boolean> onClick,
        Runnable onCreate
    ) {
        this(x, y, width, height, 0.0D, 0.0D, entry, progress, onClick, onCreate);
    }

    public void update(String group, String id, Quest quest) {
        List<MutablePair<QuestReward<?>, DisplayWidget>> locked = new ArrayList<>();
        List<MutablePair<QuestReward<?>, DisplayWidget>> available = new ArrayList<>();
        List<MutablePair<QuestReward<?>, DisplayWidget>> claimed = new ArrayList<>();
        for (QuestReward<?> reward : quest.rewards().values()) {

            DisplayWidget widget = QuestRewardWidgets.create(reward);
            if (widget == null) continue;
            if (progress.canClaim(reward.id())) {
                available.add(new MutablePair<>(reward, widget));
            } else if (progress.isComplete()) {
                claimed.add(new MutablePair<>(reward, widget));
            } else {
                locked.add(new MutablePair<>(reward, widget));
            }
        }

        this.widgets.clear();
        this.widgets.add(new MutablePair<>(null, new RewardListHeadingWidget(progress.isComplete(), this.entry.value().rewards().size(), progress.claimedRewards().size())));
        if (!locked.isEmpty()) {
            this.widgets.add(new MutablePair<>(null, LOCKED));
            this.widgets.addAll(locked);
        }
        if (!available.isEmpty()) {
            this.widgets.add(new MutablePair<>(null, AVAILABLE));
            this.widgets.addAll(available);
        }
        if (!claimed.isEmpty()) {
            this.widgets.add(new MutablePair<>(null, CLAIMED));
            this.widgets.addAll(claimed);
        }
        ClientQuests.get(id).ifPresent(entry -> {
            List<ClientQuests.QuestEntry> children = new ArrayList<>();
            for (ClientQuests.QuestEntry child : entry.children()) {
                if (!child.value().display().groups().containsKey(group)) {
                    children.add(child);
                }
            }
            if (children.isEmpty()) return;
            this.widgets.add(new MutablePair<>(null, new HeadingWidget(Component.nullToEmpty("Dependents"), 0xFF000080)));
            for (ClientQuests.QuestEntry dependent : children) {
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
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        this.scrollAmount = Mth.clamp(this.scrollAmount - scrollAmount * 10, -overscrollTop, Math.max(-overscrollTop, this.lastFullHeight - this.height + overscrollBottom));
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
        ClientQuests.updateQuest(this.entry, quest -> {
            quest.rewards().put(reward.id(), reward);
            return NetworkQuestData.builder().rewards(quest.rewards());
        });
    }
}
