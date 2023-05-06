package earth.terrarium.heracles.client.screens.quest;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.Quest;
import earth.terrarium.heracles.client.screens.quest.rewards.RewardListWidget;
import earth.terrarium.heracles.client.screens.quest.tasks.TaskListWidget;
import earth.terrarium.heracles.client.widgets.SelectableButton;
import earth.terrarium.heracles.common.menus.quest.QuestMenu;
import net.minecraft.Optionull;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QuestScreen extends AbstractContainerCursorScreen<QuestMenu> {

    private static final ResourceLocation HEADING = new ResourceLocation(Heracles.MOD_ID, "textures/gui/heading.png");

    private SelectableButton overview;
    private SelectableButton tasks;
    private SelectableButton rewards;

    private TaskListWidget taskList;
    private RewardListWidget rewardList;

    public QuestScreen(QuestMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, Optionull.mapOrDefault(menu.quest(), Quest::title, component));
    }

    @Override
    protected void init() {
        this.imageWidth = this.width;
        this.imageHeight = this.height;
        super.init();
        addRenderableWidget(new ImageButton(1, 1, 11, 11, 0, 15, 11, HEADING, 256, 256, (button) -> {
            //TODO send open quests menu
        }));
        addRenderableWidget(new ImageButton(this.width - 12, 1, 11, 11, 11, 15, 11, HEADING, 256, 256, (button) -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
        }));
        int sidebarWidth = (int) (this.width * 0.25f) - 2;
        int buttonWidth = sidebarWidth - 10;

        this.overview = addRenderableWidget(new SelectableButton(5, 20, buttonWidth, 20, Component.literal("Overview"), () -> {
            clearSelected();
            this.overview.setSelected(true);
        }));
        this.tasks = addRenderableWidget(new SelectableButton(5, 45, buttonWidth, 20, Component.literal("Tasks"), () -> {
            clearSelected();
            this.tasks.setSelected(true);
        }));

        this.rewards = addRenderableWidget(new SelectableButton(5, 70, buttonWidth, 20, Component.literal("Rewards"), () -> {
            clearSelected();
            this.rewards.setSelected(true);
        }));

        this.overview.setSelected(true);

        int contentX = (int) (this.width * 0.31f);
        int contentY = 30;
        int contentWidth = (int) (this.width * 0.63f);
        int contentHeight = this.height - 45;

        this.taskList = new TaskListWidget(contentX, contentY, contentWidth, contentHeight, this.menu.quest(), this.menu.progress());
        this.taskList.update(this.menu.quest().tasks());

        this.rewardList = new RewardListWidget(contentX, contentY, contentWidth, contentHeight);
        this.rewardList.update(this.menu.quest().rewards());
    }

    private void clearSelected() {
        this.overview.setSelected(false);
        this.tasks.setSelected(false);
        this.rewards.setSelected(false);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTick, int mouseX, int mouseY) {
        fill(stack, 0, 0, width, height, 0xD0000000);
        RenderUtils.bindTexture(HEADING);
        Gui.blitRepeating(stack, 0, 0, this.width, 15, 0, 0, 128, 15);
        int sidebarWidth = (int) (this.width * 0.25f) - 2;
        Gui.blitRepeating(stack, sidebarWidth, 15, 2, this.height - 15, 128, 0, 2, 256);
        Gui.fill(stack, sidebarWidth, 0, sidebarWidth + 2, 13, 0x80808080);

        RenderSystem.disableDepthTest();
        if (this.tasks != null && this.tasks.isSelected()) {
            this.taskList.render(stack, mouseX, mouseY, partialTick);
        }
        if (this.rewards != null && this.rewards.isSelected()) {
            this.rewardList.render(stack, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        int center = (int) ((this.width * 0.25f) + ((this.width * 0.75f) / 2f));
        this.font.draw(poseStack, this.title, center - (this.font.width(this.title) / 2f), 3, 4210752);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.tasks != null && this.tasks.isSelected() && this.taskList.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.rewards != null && this.rewards.isSelected() && this.rewardList.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        List<GuiEventListener> children = new ArrayList<>();
        if (this.tasks != null && this.tasks.isSelected()) {
            children.add(this.taskList);
        }
        if (this.rewards != null && this.rewards.isSelected()) {
            children.add(this.rewardList);
        }
        children.addAll(super.children());
        return children;
    }
}
