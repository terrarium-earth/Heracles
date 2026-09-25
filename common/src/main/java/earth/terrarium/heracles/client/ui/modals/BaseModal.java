package earth.terrarium.heracles.client.ui.modals;

import earth.terrarium.heracles.api.client.theme.ModalsTheme;
import earth.terrarium.heracles.client.ui.Overlay;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.components.widgets.buttons.SpriteButton;
import earth.terrarium.heracles.client.utils.UIUtils;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class BaseModal extends Overlay {

    protected static final int PADDING = 10;
    protected static final int INNER_PADDING = 5;
    protected static final int TITLE_BAR_HEIGHT = 15;

    protected int minHeight = 200;
    protected int minWidth = 300;
    protected float ratio = 0.75f;

    protected final Component title;

    protected int top;
    protected int left;
    protected int modalWidth;
    protected int modalHeight;

    protected int modalContentTop;
    protected int modalContentLeft;
    protected int modalContentWidth;
    protected int modalContentHeight;

    protected BaseModal(Component title, Screen background) {
        super(background);
        this.title = title;
    }

    @Override
    protected void init() {
        this.modalWidth = Math.round(Math.max(this.minWidth, this.width * this.ratio));
        this.modalHeight = Math.round(Math.max(this.minHeight, this.height * this.ratio));
        this.left = (this.width - this.modalWidth) / 2;
        this.top = (this.height - this.modalHeight) / 2;

        this.modalContentTop = this.top + TITLE_BAR_HEIGHT + INNER_PADDING;
        this.modalContentLeft = this.left + INNER_PADDING;
        this.modalContentWidth = this.modalWidth - INNER_PADDING * 2;
        this.modalContentHeight = this.modalHeight - TITLE_BAR_HEIGHT - INNER_PADDING * 2;

        GridLayout layout = initButtons(0);
        layout.arrangeElements();
        layout.setPosition(this.left + this.modalWidth - layout.getWidth() - INNER_PADDING, this.top + (TITLE_BAR_HEIGHT - layout.getHeight()) / 2);
        layout.visitWidgets(this::addRenderableWidget);
    }

    protected GridLayout initButtons(int position) {
        GridLayout layout = new GridLayout().columnSpacing(INNER_PADDING);

        layout.addChild(
            SpriteButton.create(11, 11, UIConstants.MODAL_CLOSE, this::onClose).withTooltip(ConstantComponents.CLOSE),
            0, position
        );
        return layout;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        UIUtils.blitWithEdge(graphics, UIConstants.MODAL, this.left, this.top, this.modalWidth, this.modalHeight, 3);
        UIUtils.blitWithEdge(graphics, UIConstants.MODAL_HEADER, this.left, this.top, this.modalWidth, TITLE_BAR_HEIGHT, 3);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        renderWidgets(graphics, mouseX, mouseY, partialTick);
        renderForeground(graphics, mouseX, mouseY, partialTick);
    }

    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.drawString(
            this.font,
            this.title, this.left + PADDING, (int) (this.top + (TITLE_BAR_HEIGHT - 9) / 2f) + 1,
            ModalsTheme.getTitle(), false
        );
    }
}
