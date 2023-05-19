package earth.terrarium.heracles.client.widgets.modals;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
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

    public EditObjectModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, (int) (screenWidth * 0.75f), (int) (screenHeight * 0.8f));

        addChild(Button.builder(Component.literal("Save"), b -> {
            this.setVisible(false);
            if (save != null) {
                SettingInitializer.Data data = new SettingInitializer.Data();
                widgets.forEach(data::put);
                save.accept(data);
            }
        }).bounds(this.x + this.width - 58, this.y + this.height - 20, 50, 16).build());
    }

    @Override
    protected void renderBackground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        RenderUtils.bindTexture(TEXTURE);

        CursorUtils.setCursor(true, CursorScreen.Cursor.DEFAULT);

        Gui.blitNineSliced(pose, x, y, width, height, 4, 4, 4, 4, 128, 128, 0, 0);
        Gui.blitNineSliced(pose, x + 7, y + 18, width - 14, height - 40, 1, 1, 1, 1, 128, 128, 128, 0);

    }

    @Override
    protected void renderForeground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        font.draw(pose, Component.literal("Edit Task"), this.x + 10, this.y + 7, 0x404040);

        int fullHeight = 0;
        int x = this.x + (int) (this.width * 0.55f);
        int y = this.y + 20;
        int width = this.width - 20;
        int height = this.height - 46;

        try (var ignored = RenderUtils.createScissorBoxStack(new ScissorBoxStack(), Minecraft.getInstance(), pose, this.x + 10, y, width + 4, height)) {
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
                    Component title = Component.translatable(this.id.toLanguageKey("task_entry", child.getKey()));
                    int renderY = ((LayoutElement) renderable).getY();
                    font.draw(pose, title, this.x + 15, renderY + 2, 0xFFFFFF);
                    renderable.render(pose, mouseX, mouseY, partialTick);
                }
            }
        }
        renderChildren(pose, mouseX, mouseY, partialTick);
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
            this.widgets.put(s, (GuiEventListener) data.get((int) (this.width * 0.4f), s));
        }
        this.save = save;
    }
}
