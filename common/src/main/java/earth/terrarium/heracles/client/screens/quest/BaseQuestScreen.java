package earth.terrarium.heracles.client.screens.quest;

import com.mojang.blaze3d.systems.RenderSystem;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.widgets.SelectableTabButton;
import earth.terrarium.heracles.client.widgets.base.TemporyWidget;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.OpenGroupPacket;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimRewardsPacket;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseQuestScreen extends AbstractQuestScreen<QuestContent> {

    @Nullable
    protected SelectableTabButton overview;
    @Nullable
    private SelectableTabButton tasks;
    @Nullable
    private SelectableTabButton rewards;
    @Nullable
    private Button claimRewards;
    @Nullable
    private QuestProgressWidget progressWidget;

    public BaseQuestScreen(QuestContent content) {
        super(content, Optionull.mapOrDefault(quest(content), quest -> quest.display().title(), CommonComponents.EMPTY));
        ClientQuests.updateProgress(Map.of(content.id(), content.progress()));
    }

    public void updateProgress(@Nullable QuestProgress newProgress) {
        if (newProgress != null) {
            this.content.progress().copyFrom(newProgress);
        }
        if (this.claimRewards != null) {
            this.claimRewards.active = this.content.progress().isComplete() && this.content.progress().claimedRewards().size() < this.quest().rewards().size();
        }
        if (this.progressWidget != null) {
            this.progressWidget.update(this.quest().tasks().size(), (int) this.quest().tasks().values().stream().filter(t -> this.content.progress().getTask(t).isComplete()).count(), this.quest().rewards().size(), this.content.progress().claimedRewards().size());
        }
    }

    @Override
    protected void init() {
        super.init();
        int sidebarWidth = (int) (this.width * 0.25f) - 2;
        int buttonWidth = sidebarWidth - 10;

        boolean showRewards = isEditing() || ClientQuests.get(this.content.id())
            .map(ClientQuests.QuestEntry::value)
            .map(Quest::rewards)
            .map(rewards -> !rewards.isEmpty())
            .orElse(false);

        boolean showTasks = isEditing() || ClientQuests.get(this.content.id())
            .map(ClientQuests.QuestEntry::value)
            .map(Quest::tasks)
            .map(tasks -> !tasks.isEmpty())
            .orElse(false);

        if (showRewards || showTasks) {
            this.overview = addRenderableWidget(new SelectableTabButton(5, 20, buttonWidth, 20, ConstantComponents.Quests.OVERVIEW, () -> {
                clearSelected();
                if (this.overview != null) {
                    this.overview.setSelected(true);
                }
            }));
            this.overview.setSelected(true);
            this.progressWidget = addRenderableOnly(new QuestProgressWidget(5, this.height - (showRewards ? 60 : 35), buttonWidth));
        }

        int buttonY = 45;

        if (showTasks) {
            this.tasks = addRenderableWidget(new SelectableTabButton(5, buttonY, buttonWidth, 20, ConstantComponents.Tasks.TITLE, () -> {
                clearSelected();
                if (this.tasks != null) {
                    this.tasks.setSelected(true);
                }
            }));
            buttonY += 25;
        }

        if (showRewards) {
            this.rewards = addRenderableWidget(new SelectableTabButton(5, buttonY, buttonWidth, 20, ConstantComponents.Rewards.TITLE, () -> {
                clearSelected();
                if (this.rewards != null) {
                    this.rewards.setSelected(true);
                }
            }));

            this.claimRewards = addRenderableWidget(Button.builder(ConstantComponents.Rewards.CLAIM, button -> {
                NetworkHandler.CHANNEL.sendToServer(new ClaimRewardsPacket(this.content.id()));
                if (this.claimRewards != null) {
                    this.claimRewards.active = false;
                }
            }).bounds(5, this.height - 25, buttonWidth, 20).build());
        }
    }

    @Override
    protected void goBack() {
        NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket(this.content.fromGroup(), this instanceof QuestEditScreen));
    }

    private void clearSelected() {
        if (this.overview != null) {
            this.overview.setSelected(false);
        }
        if (this.tasks != null) {
            this.tasks.setSelected(false);
        }
        if (this.rewards != null) {
            this.rewards.setSelected(false);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);
        RenderSystem.disableDepthTest();
        if (this.tasks != null && this.tasks.isSelected() && getTaskList() instanceof Renderable renderable) {
            renderable.render(graphics, mouseX, mouseY, partialTick);
        }
        if (this.rewards != null && this.rewards.isSelected() && getRewardList() instanceof Renderable renderable) {
            renderable.render(graphics, mouseX, mouseY, partialTick);
        }
        if ((this.overview == null || this.overview.isSelected()) && getDescriptionWidget() instanceof Renderable renderable) {
            renderable.render(graphics, mouseX, mouseY, partialTick);
        }
        if (getDescriptionError() != null && (this.overview == null || this.overview.isSelected())) {
            int contentX = (int) (this.width * 0.31f) + 20;
            int contentY = 30;
            int contentWidth = (int) (this.width * 0.63f) - 40;
            int contentHeight = this.height - 45;
            for (FormattedCharSequence sequence : Minecraft.getInstance().font.split(Component.literal(getDescriptionError()), contentWidth)) {
                int textWidth = this.font.width(sequence);
                graphics.drawString(
                    this.font,
                    sequence, (int) (contentX + (contentWidth - textWidth) / 2f), (int) (contentY + (contentHeight - this.font.lineHeight) / 2f), 0xFF0000,
                    false
                );
                contentY += this.font.lineHeight;
            }
        }
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        List<GuiEventListener> listeners = new ArrayList<>();
        for (TemporyWidget widget : temporaryWidgets) {
            if (widget.isVisible() && widget instanceof GuiEventListener listener) {
                listeners.add(listener);
            }
        }
        if (!listeners.isEmpty()) {
            return listeners;
        }

        List<GuiEventListener> children = new ArrayList<>();
        if (getTaskList() != null && this.tasks != null && this.tasks.isSelected()) {
            children.add(getTaskList());
        }
        if (getRewardList() != null && this.rewards != null && this.rewards.isSelected()) {
            children.add(getRewardList());
        }
        if (getDescriptionWidget() != null && (this.overview == null || this.overview.isSelected())) {
            children.add(getDescriptionWidget());
        }
        children.addAll(super.children());
        return children;
    }

    @Override
    public @NotNull Component getTitle() {
        return content.progress().isComplete() ? Component.translatable("gui.heracles.quest.title.complete", super.getTitle()) : super.getTitle();
    }

    public Quest quest() {
        return quest(this.content);
    }

    public ClientQuests.QuestEntry entry() {
        return ClientQuests.get(this.content.id()).orElse(null);
    }

    public static Quest quest(QuestContent content) {
        return ClientQuests.get(content.id()).map(ClientQuests.QuestEntry::value).orElse(null);
    }

    public abstract GuiEventListener getTaskList();

    public abstract GuiEventListener getRewardList();

    public abstract GuiEventListener getDescriptionWidget();

    public abstract String getDescriptionError();

    public boolean isEditing() {
        return this instanceof QuestEditScreen;
    }

    @Override
    public boolean drawSidebar() {
        return this.overview != null;
    }

    public String getQuestId() {
        return this.content.id();
    }
}
