package earth.terrarium.heracles.client.widgets.modals;

import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.theme.EditorTheme;
import earth.terrarium.heracles.api.client.theme.ModalsTheme;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.client.widgets.buttons.ThemedButton;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class EditObjectModal extends BaseModal {

    private final Map<String, GuiEventListener> widgets = new LinkedHashMap<>();

    private ResourceLocation id;

    private double scrollAmount;
    private int lastFullHeight;

    private Consumer<SettingInitializer.Data> save;
    private Component title = CommonComponents.EMPTY;

    public EditObjectModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, (int) (screenWidth * 0.75f), (int) (screenHeight * 0.8f));

        addChild(ThemedButton.builder(ConstantComponents.SAVE, b -> {
            this.setVisible(false);
            if (save != null) {
                SettingInitializer.Data data = new SettingInitializer.Data();
                widgets.forEach(data::put);
                save.accept(data);
            }
        }).bounds(this.x + this.width - 58, this.y + this.height - 20, 50, 16).build());
    }

    @Override
    protected void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blitNineSliced(TEXTURE, x, y, width, height, 4, 4, 4, 4, 128, 128, 0, 0);
        graphics.blitNineSliced(TEXTURE, x + 7, y + 18, width - 14, height - 40, 1, 1, 1, 1, 128, 128, 128, 0);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.drawString(
            font,
            this.title, this.x + 10, this.y + 7, ModalsTheme.getTitle(),
            false
        );

        int fullHeight = 0;
        int x = this.x + (int) (this.width * 0.55f);
        int y = this.y + 20;
        int width = this.width - 20;
        int height = this.height - 46;

        try (var ignored = RenderUtils.createScissor(Minecraft.getInstance(), graphics, this.x + 10, y, width + 4, height)) {
            for (var entry : this.widgets.entrySet()) {
                y += 2;
                LayoutElement element = (LayoutElement) entry.getValue();
                element.setY(y);
                element.setX(x);
                y += element.getHeight();
                y += 2;
                fullHeight += element.getHeight() + 4;
            }
            this.lastFullHeight = fullHeight;

            var children = new ArrayList<>(this.widgets.entrySet());
            Collections.reverse(children);
            for (var child : children) {
                if (child.getValue() instanceof Renderable renderable) {
                    Component title = Component.translatable(this.id.toLanguageKey("setting", child.getKey()));
                    int renderY = ((LayoutElement) renderable).getY();
                    graphics.drawString(
                        font,
                        title, this.x + 15, renderY + 2, EditorTheme.getModalEditSettingTitle(),
                        false
                    );
                    renderable.render(graphics, mouseX, mouseY, partialTick);
                }
            }
        }
        renderChildren(graphics, mouseX, mouseY, partialTick);
        this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0D, Math.max(0, this.lastFullHeight - height));
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        List<GuiEventListener> children = new ArrayList<>(super.children());
        children.addAll(this.widgets.values());
        return children;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        this.scrollAmount = Mth.clamp(this.scrollAmount - scrollAmount * 10, 0.0D, Math.max(0, this.lastFullHeight - this.height));
        return true;
    }

    public void init(ResourceLocation id, SettingInitializer.CreationData data, Consumer<SettingInitializer.Data> save) {
        this.id = id;
        this.widgets.clear();
        GuiEventListener closeButton = this.children().get(0);
        this.children().clear();
        this.addChild(closeButton);
        for (String s : data.data().keySet()) {
            this.widgets.put(s, data.get(null, (int) (this.width * 0.4f), s));
        }
        this.save = save;
    }

    public void setTitle(Component title) {
        this.title = title;
    }
}
