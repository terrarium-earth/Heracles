package earth.terrarium.heracles.client.screens.quest;

import com.mojang.blaze3d.systems.RenderSystem;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.widgets.SelectableTabButton;
import earth.terrarium.heracles.client.widgets.base.TemporyWidget;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.menus.quest.QuestMenu;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.OpenGroupPacket;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimRewardsPacket;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseQuestScreen extends AbstractQuestScreen<QuestMenu> {

    private SelectableTabButton overview;
    private SelectableTabButton tasks;
    private SelectableTabButton rewards;
    private Button claimRewards;

    public BaseQuestScreen(QuestMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, Optionull.mapOrDefault(quest(menu), quest -> quest.display().title(), component));
        ClientQuests.updateProgress(Map.of(menu.id(), menu.progress()));
    }

    @Override
    protected void init() {
        super.init();
        int sidebarWidth = (int) (this.width * 0.25f) - 2;
        int buttonWidth = sidebarWidth - 10;

        this.overview = addRenderableWidget(new SelectableTabButton(5, 20, buttonWidth, 20, ConstantComponents.Quests.OVERVIEW, () -> {
            clearSelected();
            this.overview.setSelected(true);
        }));

        this.tasks = addRenderableWidget(new SelectableTabButton(5, 45, buttonWidth, 20, ConstantComponents.Tasks.TITLE, () -> {
            clearSelected();
            this.tasks.setSelected(true);
        }));

        this.rewards = addRenderableWidget(new SelectableTabButton(5, 70, buttonWidth, 20, ConstantComponents.Rewards.TITLE, () -> {
            clearSelected();
            this.rewards.setSelected(true);
        }));

        this.claimRewards = addRenderableWidget(Button.builder(ConstantComponents.Rewards.CLAIM, button -> {
            NetworkHandler.CHANNEL.sendToServer(new ClaimRewardsPacket(this.menu.id()));
            this.claimRewards.active = false;
        }).bounds(5, this.height - 25, buttonWidth, 20).build());
        this.claimRewards.active = this.menu.progress().isComplete() && this.menu.progress().claimedRewards().size() < this.quest().rewards().size();

        this.overview.setSelected(true);
    }

    @Override
    protected void goBack() {
        NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket(this.menu.fromGroup(), this instanceof QuestEditScreen));
    }

    private void clearSelected() {
        this.overview.setSelected(false);
        this.tasks.setSelected(false);
        this.rewards.setSelected(false);
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
        if (this.overview != null && this.overview.isSelected() && getDescriptionWidget() instanceof Renderable renderable) {
            renderable.render(graphics, mouseX, mouseY, partialTick);
        }
        if (getDescriptionError() != null && this.overview.isSelected()) {
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
        if (getTaskList() != null && this.tasks.isSelected()) {
            children.add(getTaskList());
        }
        if (getRewardList() != null && this.rewards.isSelected()) {
            children.add(getRewardList());
        }
        if (getDescriptionWidget() != null && this.overview.isSelected()) {
            children.add(getDescriptionWidget());
        }
        children.addAll(super.children());
        return children;
    }

    public Quest quest() {
        return quest(this.menu);
    }

    public static Quest quest(QuestMenu menu) {
        return ClientQuests.get(menu.id()).map(ClientQuests.QuestEntry::value).orElse(null);
    }

    public abstract GuiEventListener getTaskList();

    public abstract GuiEventListener getRewardList();

    public abstract GuiEventListener getDescriptionWidget();

    public abstract String getDescriptionError();
}
