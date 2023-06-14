package earth.terrarium.heracles.client.widgets.modals.icon;

import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.client.widgets.ToggleImageButton;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class IconModal extends BaseModal {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/icons.png");
    private static final int WIDTH = 168;
    private static final int HEIGHT = 173;

    private static final int ITEM_SIZE = 19;
    private static final int ITEM_COLUMNS = 8;
    private static final int ITEM_ROWS = 6;

    private final List<Item> items = new ArrayList<>();
    private Consumer<Item> callback;

    private final EditBox search;

    public IconModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT);

        addChild(new ToggleImageButton(x + 7, y + 5, 11, 11, 168, 0, 11, TEXTURE, 256, 256, (b) -> {
            if (b) {
                items.clear();
                if (Minecraft.getInstance().player != null) {
                    for (ItemStack item : Minecraft.getInstance().player.inventoryMenu.getItems()) {
                        items.add(item.getItem());
                    }
                }
            } else {
                items.clear();
                BuiltInRegistries.ITEM.iterator().forEachRemaining(items::add);
            }
        })).setTooltip(Tooltip.create(Component.literal("Switch Mode")));

        this.search = addChild(new EditBox(Minecraft.getInstance().font, x + 8, y + 19, 152, 14, ConstantComponents.SEARCH));
    }

    @Override
    protected void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(TEXTURE, x, y, 0, 0, WIDTH, HEIGHT, 256, 256);
        renderChildren(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int textX = (WIDTH - font.width("Choose Icon")) / 2;
        graphics.drawString(
            font,
            "Choose Icon", x + textX, y + 6, 0x404040,
            false
        );

        int y = this.y + 43;
        int x = this.x + 8;

        List<Item> items = updateItems(this.search.getValue());

        int max = Math.min(items.size(), 48);
        for (int i = 0; i < max; i++) {
            int row = i / ITEM_COLUMNS;
            int column = i % ITEM_COLUMNS;

            int itemX = x + (column * ITEM_SIZE);
            int itemY = y + (row * ITEM_SIZE);

            Item item = items.get(i);

            graphics.blit(TEXTURE, itemX, itemY, 168, 22, ITEM_SIZE, ITEM_SIZE, 256, 256);
            if (mouseX >= itemX && mouseX < itemX + ITEM_SIZE && mouseY >= itemY && mouseY < itemY + ITEM_SIZE) {
                graphics.fill(itemX + 1, itemY + 1, itemX + ITEM_SIZE - 1, itemY + ITEM_SIZE - 1, 0x80A0A0A0);
                CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
                ClientUtils.setTooltip(item.getDescription());
            }
            try (var pose = new CloseablePoseStack(graphics)) {
                pose.translate(0, 0, -100);
                graphics.renderFakeItem(item.getDefaultInstance(), itemX + 2, itemY + 1);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isVisible()) return false;
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        if (mouseX < x || mouseX > x + WIDTH || mouseY < y || mouseY > y + HEIGHT) {
            setVisible(false);
        }

        int y = this.y + 43;
        int x = this.x + 8;

        if (callback == null) return true;

        List<Item> items = updateItems(this.search.getValue());

        int max = Math.min(items.size(), 48);

        for (int i = 0; i < max; i++) {
            int row = i / ITEM_COLUMNS;
            int column = i % ITEM_COLUMNS;

            int itemX = x + (column * ITEM_SIZE);
            int itemY = y + (row * ITEM_SIZE);

            Item item = items.get(i);

            if (mouseX >= itemX && mouseX < itemX + ITEM_SIZE && mouseY >= itemY && mouseY < itemY + ITEM_SIZE) {
                callback.accept(item);
            }
        }
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            items.clear();
            BuiltInRegistries.ITEM.iterator().forEachRemaining(items::add);
        }
    }

    private List<Item> updateItems(String search) {
        search = search.toLowerCase(Locale.ROOT).trim();

        List<Item> filteredItems = new ArrayList<>();
        for (Item item : items) {
            if (filteredItems.size() >= ITEM_COLUMNS * ITEM_ROWS) break;
            if (item == Items.AIR) continue;
            Component text = item.getDescription();
            if (text.getString().toLowerCase(Locale.ROOT).contains(search)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    public void setCallback(Consumer<Item> callback) {
        this.callback = callback;
    }
}
