package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.client.ClientQuests;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QuestsWidget extends AbstractContainerEventHandler implements Renderable, NarratableEntry {

    private final List<QuestWidget> widgets = new ArrayList<>();

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private double scrollAmount;

    public QuestsWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update(List<ClientQuests.QuestEntry> quests) {
        this.widgets.clear();
        for (ClientQuests.QuestEntry quest : quests) {
            this.widgets.add(new QuestWidget(quest));
        }
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        int x = this.x;
        int y = this.y;

        try (var scissor = RenderUtils.createScissorBoxStack(new ScissorBoxStack(), Minecraft.getInstance(), pose, x, y, width, height)) {
            for (QuestWidget widget : this.widgets) {
                widget.render(pose, scissor.stack(), x - (int) this.scrollAmount, y, mouseX, mouseY, isMouseOver(mouseX, mouseY), partialTick);
            }
        }
        this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0D, Math.max(0, 1000 - this.width));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        this.scrollAmount = Mth.clamp(this.scrollAmount - scrollAmount * 10, 0.0D, Math.max(0, 1000 - this.width));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            for (QuestWidget widget : this.widgets) {
                if (widget.isMouseOver(mouseX - (this.x - (int) this.scrollAmount), mouseY - this.y)) {
                    widget.onClicked();
                    return true;
                }
            }
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

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }

    private record MouseClick(double x, double y, int button) {}
}
