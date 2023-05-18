package earth.terrarium.heracles.client.screens.quest.rewards;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.client.QuestRewardWidgets;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.quest.HeadingWidget;
import earth.terrarium.heracles.client.utils.MouseClick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RewardListWidget extends AbstractContainerEventHandler implements Renderable {

    private final List<DisplayWidget> widgets = new ArrayList<>();

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private double scrollAmount;
    private int lastFullHeight;

    private MouseClick mouse = null;

    public RewardListWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lastFullHeight = this.height;
    }

    public void update(String id, Quest quest) {
        this.widgets.clear();
        this.widgets.add(new HeadingWidget(Component.nullToEmpty("Rewards"), 0xFF00DD00));
        for (QuestReward<?> reward : quest.rewards().values()) {
            DisplayWidget widget = QuestRewardWidgets.create(reward);
            if (widget == null) continue;
            this.widgets.add(widget);
        }
        ClientQuests.get(id).ifPresent(entry -> {
            if (entry.children().isEmpty()) return;
            this.widgets.add(new HeadingWidget(Component.nullToEmpty("Dependents"), 0xFF000080));
            for (ClientQuests.QuestEntry dependent : entry.children()) {
                this.widgets.add(new QuestDependentWidget(dependent.value()));
            }
        });
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        int x = this.x;
        int y = this.y;

        int fullHeight = 0;
        try (var scissor = RenderUtils.createScissorBoxStack(new ScissorBoxStack(), Minecraft.getInstance(), pose, x, y, width, height)) {
            for (DisplayWidget widget : this.widgets) {
                if (this.mouse != null && widget.mouseClicked(this.mouse.x() - x, this.mouse.y() - (y - this.scrollAmount), this.mouse.button(), this.width)) {
                    this.mouse = null;
                }
                widget.render(pose, scissor.stack(), x, y - (int) this.scrollAmount, this.width, mouseX, mouseY, this.isMouseOver(mouseX, mouseY), partialTick);
                var itemheight = widget.getHeight(this.width);
                y += itemheight;
                fullHeight += itemheight;
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
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of();
    }
}
