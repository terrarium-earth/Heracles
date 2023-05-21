package earth.terrarium.heracles.client.widgets.modals;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;

public class ConfirmModal extends BaseModal {

    private static final int WIDTH = 168;
    private static final int HEIGHT = 57;

    private Runnable onConfirm;

    public ConfirmModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT);

        addChild(Button.builder(CommonComponents.GUI_CANCEL,
                b -> this.visible = false).bounds(this.x + 7, this.y + 24, 65, 20)
            .tooltip(Tooltip.create(CommonComponents.GUI_CANCEL))
            .build());

        addChild(Button.builder(CommonComponents.GUI_CONTINUE,
                b -> {
                    if (this.onConfirm != null) {
                        this.onConfirm.run();
                    }
                    this.visible = false;
                }).bounds(this.x + 96, this.y + 24, 65, 20)
            .tooltip(Tooltip.create(CommonComponents.GUI_CONTINUE))
            .build());
    }

    @Override
    protected void renderBackground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        RenderUtils.bindTexture(TEXTURE);

        pose.pushPose();
        pose.translate(0, 0, 150);
        Gui.blitNineSliced(pose, x, y, width, height, 4, 4, 4, 4, 128, 128, 0, 0);
        renderChildren(pose, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderForeground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        font.draw(pose, "Confirm", x + 10, y + 6, 0x404040);
        pose.popPose();
    }

    public void setCallback(Runnable runnable) {
        this.onConfirm = runnable;
    }
}
