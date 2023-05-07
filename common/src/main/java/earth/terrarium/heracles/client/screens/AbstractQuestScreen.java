package earth.terrarium.heracles.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class AbstractQuestScreen<T extends AbstractContainerMenu> extends AbstractContainerCursorScreen<T> {

    public static final ResourceLocation HEADING = new ResourceLocation(Heracles.MOD_ID, "textures/gui/heading.png");

    protected boolean hasBackButton = true;

    public AbstractQuestScreen(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        this.imageWidth = this.width;
        this.imageHeight = this.height;
        super.init();
        if (hasBackButton) {
            addRenderableWidget(new ImageButton(1, 1, 11, 11, 0, 15, 11, HEADING, 256, 256, (button) -> {
                goBack();
            })).setTooltip(Tooltip.create(Component.literal("Back")));
        }
        addRenderableWidget(new ImageButton(this.width - 12, 1, 11, 11, 11, 15, 11, HEADING, 256, 256, (button) -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
        })).setTooltip(Tooltip.create(Component.literal("Close")));
    }

    protected void goBack() {

    }

    @Override
    protected void renderBg(PoseStack stack, float partialTick, int mouseX, int mouseY) {
        fill(stack, 0, 0, width, height, 0xD0000000);
        RenderUtils.bindTexture(HEADING);
        Gui.blitRepeating(stack, 0, 0, this.width, 15, 0, 0, 128, 15);
        int sidebarWidth = (int) (this.width * 0.25f) - 2;
        Gui.blitRepeating(stack, sidebarWidth, 15, 2, this.height - 15, 128, 0, 2, 256);
        Gui.fill(stack, sidebarWidth, 0, sidebarWidth + 2, 13, 0x80808080);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        int center = (int) ((this.width * 0.25f) + ((this.width * 0.75f) / 2f));
        this.font.draw(poseStack, this.title, center - (this.font.width(this.title) / 2f), 3, 0x404040);
    }
}
