package earth.terrarium.heracles.client.ui.modals;

import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.SettingInitializer.CreationData;
import earth.terrarium.heracles.api.client.settings.SettingsProvider;
import earth.terrarium.heracles.api.client.theme.EditorTheme;
import earth.terrarium.heracles.client.components.widgets.buttons.SpriteButton;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EditObjectModal extends BaseModal {

    private final Map<String, AbstractWidget> widgets = new LinkedHashMap<>();

    private final ResourceLocation id;
    private final CreationData data;
    private final Consumer<SettingInitializer.Data> save;

    private double scrollAmount;
    private int fullHeight;

    protected EditObjectModal(Screen background, Component title, ResourceLocation id, CreationData data, Consumer<SettingInitializer.Data> save) {
        super(title, background);
        this.id = id;
        this.data = data;
        this.save = save;
    }

    private void save() {
        if (this.save != null) {
            SettingInitializer.Data data = new SettingInitializer.Data();
            this.widgets.forEach(data::put);
            this.save.accept(data);
        }
        this.onClose();
    }

    @Override
    protected void init() {
        super.init();

        Map<String, AbstractWidget> widgets = new LinkedHashMap<>(this.widgets);

        this.widgets.clear();

        for (String id : data.data().keySet()) {
            AbstractWidget widget = data.get(widgets.get (id), (int) (this.modalContentWidth * 0.4f) - INNER_PADDING, id);

            this.widgets.put(id, addWidget(widget));
        }
    }

    @Override
    protected GridLayout initButtons(int position) {
        GridLayout layout = super.initButtons(position + 1);
        layout.addChild(
            SpriteButton.create(11, 11, UIConstants.MODAL_SAVE, this::save).withTooltip(ConstantComponents.SAVE),
            0, position
        );
        return layout;
    }

    private void updateScroll(double amount) {
        this.scrollAmount = Mth.clamp(this.scrollAmount - amount * 10, 0.0D, Math.max(0, this.fullHeight - this.modalContentHeight));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        updateScroll(delta);
        return true;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        this.fullHeight = 0;
        int textWidth = (int) (this.modalContentWidth * 0.6f);
        int widgetWidth = (int) (this.modalContentWidth * 0.4f) - INNER_PADDING;

        try (var ignored = RenderUtils.createScissor(Minecraft.getInstance(), graphics, this.modalContentLeft, this.modalContentTop, this.modalContentWidth, this.modalContentHeight)) {
            int y = this.modalContentTop;
            for (var entry : this.widgets.entrySet()) {
                AbstractWidget widget = entry.getValue();
                String id = entry.getKey();
                int widgetX = this.modalContentLeft + textWidth + Math.max(widgetWidth - widget.getWidth(), 0);
                int widgetY = y + INNER_PADDING;
                widget.setPosition(widgetX, widgetY);

                int widgetHeight = widget.getHeight();
                int textY = (int) (y + (widgetHeight - 9) / 2f) + INNER_PADDING;

                graphics.drawString(
                    this.font,
                    Component.translatable(this.id.toLanguageKey("setting", id)),
                    this.modalContentLeft + INNER_PADDING, textY, EditorTheme.getModalEditSettingTitle(),
                    false
                );

                y += widget.getHeight() + INNER_PADDING * 2;
                this.fullHeight += widget.getHeight() + INNER_PADDING * 2;

                widget.render(graphics, mouseX, mouseY, partialTick);
            }
        }

        updateScroll(0);
    }

    public static void open(Component title, ResourceLocation id, CreationData data, Consumer<SettingInitializer.Data> save) {
        Screen background = Minecraft.getInstance().screen;
        EditObjectModal modal = new EditObjectModal(background, title, id, data, save);
        Minecraft.getInstance().setScreen(modal);
    }

    public static <T> void open(
        @Nullable SettingInitializer<?> setting, ResourceLocation typeId,
        Component title, String id, T value, Consumer<T> saver
    ) {
        if (setting == null) return;

        CreationData data = setting.create(ModUtils.cast(value));
        if (data.isEmpty()) {
            var newValue = setting.create(id, ModUtils.cast(value), new SettingInitializer.Data());
            if (newValue == null) return;
            saver.accept(ModUtils.cast(newValue));
        } else {
            EditObjectModal.open(
                title,
                typeId,
                data,
                newData -> {
                    var newValue = setting.create(id, ModUtils.cast(value), newData);
                    if (newValue == null) return;
                    saver.accept(ModUtils.cast(newValue));
                }
            );
        }

    }

    public static <T> void open(SettingsProvider<T> type, Component title, String id, T value, Consumer<T> saver) {
        open(type.factory(), type.id(), title, id, value, saver);
    }
}
