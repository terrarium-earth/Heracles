package earth.terrarium.olympus.client.ui.modals;

import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.base.BaseWidget;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.components.string.MultilineTextWidget;
import earth.terrarium.olympus.client.layouts.Layouts;
import earth.terrarium.olympus.client.ui.Overlay;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.UITexts;
import earth.terrarium.olympus.client.utils.Orientation;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ActionModal extends Overlay {

    private static final int PADDING = 5;
    private static final int BUTTON_GAP = 5;
    private static final int CONTENT_GAP = 5;
    private static final int HEADER_HEIGHT = 11;

    private final Builder builder;

    private Layout layout;

    protected ActionModal(Builder builder, Screen background) {
        super(background);

        this.builder = builder;
    }

    @Override
    protected void init() {
        super.init();

        var actions = this.builder.actions;
        int actionsHeight = actions.stream().mapToInt(AbstractWidget::getHeight).max().orElse(20);
        int actionsWidth = Math.max(
                actions.stream().mapToInt(AbstractWidget::getWidth).sum() + (actions.size() - 1) * BUTTON_GAP,
                this.builder.minWidth
        );

        var content = this.builder.content.stream().map(f -> f.apply(actionsWidth)).toList();
        int minContentHeight = this.builder.minHeight - HEADER_HEIGHT - actionsHeight - PADDING * 4;
        int contentHeight = content.stream().mapToInt(AbstractWidget::getHeight).sum() + content.size() * CONTENT_GAP;
        int contentWidth = content.stream().mapToInt(AbstractWidget::getWidth).max().orElse(10);

        int modalWidth = Math.max(contentWidth, actionsWidth) + PADDING * 2;

        var closeButton = Widgets.button()
                .withTexture(null)
                .withRenderer(WidgetRenderers.sprite(UIConstants.MODAL_CLOSE))
                .withCallback(this::onClose)
                .withTooltip(UITexts.BACK)
                .withSize(11, 11);

        var contentLayout = Layouts.column().withGap(CONTENT_GAP);
        content.forEach(widget ->
                contentLayout.withChild(Layouts.row()
                        .withChild(SpacerElement.width(PADDING))
                        .withChild(widget)
                        .withChild(SpacerElement.width(PADDING))
                )
        );
        if (contentHeight < minContentHeight) {
            contentLayout.withChild(SpacerElement.height(minContentHeight - contentHeight - CONTENT_GAP));
        }

        this.layout = Layouts.column()
                .withGap(PADDING)
                .withChild(Widgets.frame()
                        .withSize(modalWidth, HEADER_HEIGHT + PADDING * 2)
                        .withTexture(UIConstants.MODAL_HEADER)
                        .withContents(contents -> contents.addChild(
                                Widgets.labelled(this.font, this.builder.title, closeButton).withEqualSpacing(Orientation.HORIZONTAL)
                        ))
                        .withContentFill()
                        .withContentMargin(PADDING)
                )
                .withChildren(contentLayout)
                .withChild(Widgets.frame()
                        .withSize(modalWidth, actionsHeight + PADDING * 2)
                        .withTexture(UIConstants.MODAL_FOOTER)
                        .withContents(contents -> actions.forEach(contents::addChild))
                        .withEqualSpacing(Orientation.HORIZONTAL)
                        .withContentMargin(PADDING)
                )
                .build(this::addRenderableWidget);

        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        this.renderTransparentBackground(graphics);

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                UIConstants.MODAL,
                this.layout.getX() - 1, this.layout.getY() - 1,
                this.layout.getWidth() + 2, this.layout.getHeight() + 2
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<AbstractWidget> actions = new ArrayList<>();
        private final List<Int2ObjectFunction<AbstractWidget>> content = new ArrayList<>();

        private Component title;
        private int minWidth = 150;
        private int minHeight = 100;

        private Builder() {}

        public Builder withTitle(Component title) {
            this.title = title;
            return this;
        }

        public Builder withMinWidth(int minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        public Builder withMinHeight(int minHeight) {
            this.minHeight = minHeight;
            return this;
        }

        public Builder withContent(Int2ObjectFunction<AbstractWidget> widget) {
            this.content.add(widget);
            return this;
        }

        public Builder withContent(BaseWidget widget) {
            this.content.add(width -> widget.withSize(width, widget.getHeight()));
            return this;
        }

        public Builder withContent(Component text) {
            this.content.add(i -> new MultilineTextWidget(i, text, Minecraft.getInstance().font).alignLeft());
            return this;
        }

        public Builder withAction(AbstractWidget widget) {
            this.actions.add(widget);
            return this;
        }

        public void open() {
            Minecraft.getInstance().setScreen(new ActionModal(this, Minecraft.getInstance().screen));
        }
    }
}
