package earth.terrarium.olympus.client.components.color;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.components.color.type.*;
import earth.terrarium.olympus.client.components.dropdown.DropdownBuilder;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.constants.MinecraftColors;
import earth.terrarium.olympus.client.ui.Overlay;
import earth.terrarium.olympus.client.ui.OverlayAlignment;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.UIIcons;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.function.Consumers;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ColorPickerOverlay extends Overlay {
    private static final int PADDING = 4;
    private static final int SPACING = 2;

    private final AbstractWidget widget;
    private final HsbState state;
    private final State<Color> color;
    private final DropdownState<ColorPresetType> type;
    private final boolean hasAlpha;

    private int x;
    private int y;
    private int width;
    private int height;
    private Color[] presets = new Color[0];

    private Identifier background = UIConstants.MODAL;
    private Identifier inset = UIConstants.MODAL_INSET;

    private Consumer<Button> eyedropperSettings = Consumers.nop();
    private BiConsumer<Button, DropdownState<ColorPresetType>> dropdownBtnSettings = (ignored1, ignored2) -> {};
    private BiConsumer<DropdownBuilder<ColorPresetType>, DropdownState<ColorPresetType>> dropdownSettings = (ignored1, ignored2) -> {};

    private OverlayAlignment alignment = OverlayAlignment.BOTTOM_LEFT;

    private LinearLayout colorSelectLayout;

    public ColorPickerOverlay(AbstractWidget widget, State<Color> state, boolean hasAlpha) {
        super(Minecraft.getInstance().screen);
        this.widget = widget;
        this.state = new HsbState(HsbColor.fromRgb(state.get().getValue()), color -> state.set(new Color(color.toRgba(hasAlpha))));
        this.hasAlpha = hasAlpha;
        this.color = state;
        this.type = DropdownState.of(ColorPresetType.MC_COLORS);
    }

    public ColorPickerOverlay withBackground(Identifier background) {
        this.background = background;
        return this;
    }

    public ColorPickerOverlay withInset(Identifier inset) {
        this.inset = inset;
        return this;
    }

    public ColorPickerOverlay withEyedropperSettings(Consumer<Button> settings) {
        this.eyedropperSettings = settings;
        return this;
    }

    public ColorPickerOverlay withDropdownButtonSettings(BiConsumer<Button, DropdownState<ColorPresetType>> settings) {
        this.dropdownBtnSettings = settings;
        return this;
    }

    public ColorPickerOverlay withDropdownSettings(BiConsumer<DropdownBuilder<ColorPresetType>, DropdownState<ColorPresetType>> settings) {
        this.dropdownSettings = settings;
        return this;
    }

    public ColorPickerOverlay withPresets(Color... presets) {
        this.presets = presets;
        this.type.set(ColorPresetType.DEFAULTS);
        return this;
    }

    public ColorPickerOverlay withAlignment(OverlayAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    @Override
    protected void init() {
        GridLayout layout = new GridLayout().spacing(SPACING);

        colorSelectLayout = LinearLayout.vertical();

        colorSelectLayout.addChild(new SaturationBrightnessSelector(100, 50, this.state));
        colorSelectLayout.addChild(new HueSelector(100, 10, this.state));
        if (this.hasAlpha) {
            colorSelectLayout.addChild(new AlphaSelector(100, 10, this.state));
        }

        layout.addChild(colorSelectLayout, 0, 0, settings -> settings.padding(1));

        LinearLayout presets = LinearLayout.horizontal().spacing(SPACING);

        presets.addChild(Widgets.button((btn) -> {
            btn.withRenderer(WidgetRenderers.icon(UIIcons.EYE_DROPPER).withCentered(12, 12).withPaddingBottom(2));
            btn.withSize(14, 16);
            btn.withCallback(() -> EyedropperOverlay.open(state));
            eyedropperSettings.accept(btn);
        }));

        presets.addChild(Widgets.dropdown(type, List.of(
                this.presets.length == 0 ? ColorPresetType.WITHOUT_DEFAULT : ColorPresetType.VALUES
        ), (preset) -> Component.translatable(preset.getTranslationKey()), button -> {
            button.withSize(86, 16);
            button.withRenderer(type.withRenderer((value, open) ->
                    WidgetRenderers.textWithChevron(Component.translatable(value.getTranslationKey()), open)
                            .withColor(MinecraftColors.DARK_GRAY))
                    .withPadding(0, 4)
            );
            dropdownBtnSettings.accept(button, type);
        }, builder -> dropdownSettings.accept(builder, type)));

        layout.addChild(presets, 1, 0);

        layout.addChild(new PresetsSelector(102, this.presets, this.type, this.color, this.hasAlpha, this.inset), 2, 0);

        layout.arrangeElements();

        this.width = 102 + PADDING * 2;
        this.height = layout.getHeight() + PADDING * 2;

        var pos = this.alignment.getPos(this.widget, this.width, this.height);

        this.x = pos.x;
        this.y = pos.y;
        layout.setPosition(this.x + PADDING, y + PADDING);
        layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(graphics, mouseX, mouseY, partialTicks);
        graphics.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, background, this.x, this.y, this.width, this.height);
        graphics.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, inset, this.colorSelectLayout.getX() - 1, this.colorSelectLayout.getY() - 1, this.colorSelectLayout.getWidth() + 2, this.colorSelectLayout.getHeight() + 2);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (!this.state.hasChanged()) return;
        RecentColorStorage.add(this.color.get());
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (event.input() != 0 || this.isMouseOver(event.x(), event.y())) {
            return super.mouseClicked(event, bl);
        }
        this.onClose();
        return false;
    }
}

