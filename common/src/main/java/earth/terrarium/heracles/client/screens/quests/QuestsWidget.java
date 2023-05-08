package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QuestsWidget extends AbstractContainerEventHandler implements Renderable, NarratableEntry {

    private static final ResourceLocation ARROW = new ResourceLocation(Heracles.MOD_ID, "textures/gui/arrow.png");

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
            RenderUtils.bindTexture(ARROW);
            RenderSystem.enableBlend();

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();

            for (ClientQuests.QuestEntry entry : this.entries) {
                var position = entry.value().display().position();

                int px = x - (int) this.scrollAmount + position.x() + 16;
                int py = y + position.y() + 16;

                boolean isHovered = isMouseOver(mouseX, mouseY) && mouseX >= px - 16 && mouseX <= px - 16 + 24 && mouseY >= py - 16 && mouseY <= py - 16 + 24;

                RenderSystem.setShaderColor(0.9F, 0.9F, 0.9F, isHovered ? 0.45f : 0.25F);

                for (ClientQuests.QuestEntry child : entry.children()) {
                    var childPosition = child.value().display().position();

                    int cx = x - (int) this.scrollAmount + childPosition.x() + 16;
                    int cy = y + childPosition.y() + 16;

                    float length = Mth.sqrt(Mth.square(cx - px) + Mth.square(cy - py));

                    pose.pushPose();
                    pose.translate(px, py, 0);
                    pose.mulPose(Axis.ZP.rotation((float) Mth.atan2(cy - py, cx - px)));

                    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                    buffer.vertex(pose.last().pose(), 0, 0, 0).uv(0, 0).endVertex();
                    buffer.vertex(pose.last().pose(), 0, 5, 0).uv(0, 1).endVertex();
                    buffer.vertex(pose.last().pose(), length, 5, 0).uv(length / 3f, 1).endVertex();
                    buffer.vertex(pose.last().pose(), length, 0, 0).uv(length / 3f, 0).endVertex();
                    tesselator.end();

                    pose.popPose();
                }
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();

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
