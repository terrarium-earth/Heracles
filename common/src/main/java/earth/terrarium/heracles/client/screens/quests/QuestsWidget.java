package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QuestsWidget extends AbstractContainerEventHandler implements Renderable, NarratableEntry {

    private final List<QuestWidget> widgets = new ArrayList<>();
    private final List<ClientQuests.QuestEntry> entries = new ArrayList<>();

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

    public void update(List<Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus>> quests) {
        this.widgets.clear();
        this.entries.clear();
        for (Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus> quest : quests) {
            this.widgets.add(new QuestWidget(quest.getFirst(), quest.getSecond()));
            this.entries.add(quest.getFirst());
        }
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        int x = this.x;
        int y = this.y;

        try (var scissor = RenderUtils.createScissorBoxStack(new ScissorBoxStack(), Minecraft.getInstance(), pose, x, y, width, height)) {

            float lineWidth = RenderSystem.getShaderLineWidth();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            RenderSystem.enableBlend();
            GlStateManager._depthMask(false);
            GlStateManager._disableCull();
            RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
            RenderSystem.lineWidth(10f);
            bufferBuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
            for (ClientQuests.QuestEntry entry : this.entries) {
                var position = entry.value().display().position();

                for (ClientQuests.QuestEntry child : entry.children()) {
                    var childPosition = child.value().display().position();

                    bufferBuilder.vertex(pose.last().pose(), (float)x + position.x() + 12, (float)y + position.y() + 12, 0f)
                        .color(255, 255, 255, 255)
                        .normal(1f, 0f, 0f)
                        .endVertex();
                    bufferBuilder.vertex(pose.last().pose(), (float)x + childPosition.x() + 12, (float)y + childPosition.y() + 12, 0f)
                        .color(255, 255, 255, 255)
                        .normal(1f, 0f, 0f)
                        .endVertex();
                }
            }
            BufferUploader.drawWithShader(bufferBuilder.end());
            RenderSystem.disableBlend();
            GlStateManager._enableCull();
            GlStateManager._depthMask(true);
            RenderSystem.lineWidth(lineWidth);

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
}
