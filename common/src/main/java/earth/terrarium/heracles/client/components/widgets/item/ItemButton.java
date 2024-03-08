package earth.terrarium.heracles.client.components.widgets.item;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.utils.UIUtils;
import earth.terrarium.heracles.common.utils.ItemValue;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class ItemButton extends BaseWidget {

    private final boolean allowsTags;
    private final AtomicReference<@NotNull ItemValue> reference;

    public ItemButton(ItemButton button, TagKey<Item> tag, int width, int height, boolean allowsTags) {
        this(button, new ItemValue(tag), width, height, allowsTags);
    }

    public ItemButton(ItemButton button, ItemStack stack, int width, int height, boolean allowsTags) {
        this(button, new ItemValue(stack), width, height, allowsTags);
    }

    public ItemButton(ItemButton button, ItemValue value, int width, int height, boolean allowsTags) {
        super(width, height);

        this.allowsTags = allowsTags;
        this.reference = Optionull.mapOrDefault(button, ItemButton::reference, new AtomicReference<>(value));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation sprite = UIConstants.BUTTON.get(this.isHovered(), !this.active);
        UIUtils.blitWithEdge(graphics, sprite, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 3);

        ItemValue value = this.reference.get();


        if (!value.isEmpty()) {
            int offset = (this.height - 16) / 2;
            graphics.renderFakeItem(value.getDefaultInstance(), this.getX() + offset, this.getY() + offset);

            UIUtils.renderScrollingString(
                graphics,
                value.getDisplayName(),
                this.getX() + offset * 2 + 16,
                this.getY(),
                this.getWidth() - offset * 3 - 16,
                this.getHeight(),
                0x505050,
                false
            );
        } else {
            Font font = Minecraft.getInstance().font;
            int textX = this.getX() + (this.height - 8) / 2;
            int textY = this.getY() + (this.getHeight() - 8) / 2;
            graphics.drawString(font, "Nothing", textX, textY, 0x505050, false);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        Screen screen = Minecraft.getInstance().screen;
        Minecraft.getInstance().setScreen(new ItemScreen(screen, this, this.allowsTags));
    }

    public AtomicReference<ItemValue> reference() {
        return this.reference;
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return CursorScreen.Cursor.POINTER;
    }
}
