package earth.terrarium.heracles.client.screens.quest;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.menus.quest.QuestMenu;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class QuestScreen extends AbstractContainerCursorScreen<QuestMenu> {

    private static final ResourceLocation HEADING = new ResourceLocation(Heracles.MOD_ID, "textures/gui/heading.png");

    public QuestScreen(QuestMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
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
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTick, int mouseX, int mouseY) {
        this.renderBackground(stack);
        RenderUtils.bindTexture(HEADING);
        Gui.blitRepeating(stack, 0, 0, this.width, 15, 0, 0, 128, 15);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.title, 16, 3, 4210752);
    }
}
