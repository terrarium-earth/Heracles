package earth.terrarium.heracles.client.widgets.modals;

import com.mojang.datafixers.util.Either;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.utils.ThemeColors;
import earth.terrarium.heracles.client.widgets.StateImageButton;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.utils.ItemValue;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

public class ItemModal extends BaseModal {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/icons.png");
    public static final Component TITLE = Component.translatable("gui.heracles.choose_item");
    public static final Component MODE_TOOLTIP = Component.translatable("gui.heracles.switch_mode");
    private static final int WIDTH = 168;
    private static final int HEIGHT = 173;

    private static final int ITEM_SIZE = 19;
    private static final int ITEM_COLUMNS = 8;
    private static final int ITEM_ROWS = 6;

    private final List<ItemValue> items = new ArrayList<>();
    private Consumer<Either<ItemStack, TagKey<Item>>> callback;
    private Either<ItemStack, TagKey<Item>> current = null;
    private boolean tagsAllowed = true;

    private final EditBox search;
    private final StateImageButton modeButton;

    public ItemModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT, 2);
        this.modeButton = addChild(new StateImageButton(x + 7, y + 5, 11, 11, 168, 0, 11, TEXTURE, 256, 256, 3, this::update));
        this.modeButton.setTooltip(Tooltip.create(MODE_TOOLTIP));
        this.search = addChild(new EditBox(Minecraft.getInstance().font, x + 8, y + 19, 152, 14, ConstantComponents.SEARCH));
    }

    public void update(int state) {
        items.clear();
        switch (state) {
            case 0 -> {
                if (current != null && current.map(stack -> !stack.is(Items.AIR), tag -> true)) {
                    Heracles.getRegistryAccess().registry(Registries.ITEM)
                        .ifPresent(registry -> items.add(new ItemValue(current)));
                }
                BuiltInRegistries.ITEM.stream().map(ItemValue::new).forEach(items::add);
            }
            case 1 -> {
                if (Minecraft.getInstance().player != null) {
                    for (ItemStack item : Minecraft.getInstance().player.inventoryMenu.getItems()) {
                        if (item.isEmpty()) continue;
                        items.add(new ItemValue(item));
                    }
                }
            }
            case 2 ->  {
                if (tagsAllowed) {
                    Heracles.getRegistryAccess().registry(Registries.ITEM)
                        .ifPresent(registry ->
                            registry.getTagNames().map(ItemValue::new).forEach(items::add)
                        );
                }
            }
        }
    }

    @Override
    protected void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(TEXTURE, x, y, 0, 0, WIDTH, HEIGHT, 256, 256);
        renderChildren(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int textX = (WIDTH - font.width(TITLE)) / 2;
        graphics.drawString(
            font,
            TITLE, x + textX, y + 6, ThemeColors.MODAL_ICONS_TITLE,
            false
        );

        int y = this.y + 43;
        int x = this.x + 8;

        List<ItemValue> values = updateItems(this.search.getValue());

        List<Component> tooltip = new ArrayList<>();

        int max = Math.min(values.size(), 48);
        for (int i = 0; i < max; i++) {
            int row = i / ITEM_COLUMNS;
            int column = i % ITEM_COLUMNS;

            int itemX = x + (column * ITEM_SIZE);
            int itemY = y + (row * ITEM_SIZE);

            ItemValue value = values.get(i);

            graphics.blit(TEXTURE, itemX, itemY, 168, 22, ITEM_SIZE, ITEM_SIZE, 256, 256);
            if (mouseX >= itemX && mouseX < itemX + ITEM_SIZE && mouseY >= itemY && mouseY < itemY + ITEM_SIZE) {
                graphics.fill(itemX + 1, itemY + 1, itemX + ITEM_SIZE - 1, itemY + ITEM_SIZE - 1, 0x80A0A0A0);
                CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
                tooltip.add(value.getDisplayName());
                tooltip.add(value.getNamespace());
            }
            try (var pose = new CloseablePoseStack(graphics)) {
                pose.translate(0, 0, -100);
                graphics.renderFakeItem(value.getDefaultInstance(), itemX + 2, itemY + 1);
            }
        }
        if (!tooltip.isEmpty()) {
            graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
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

        List<ItemValue> values = updateItems(this.search.getValue());

        int max = Math.min(values.size(), 48);

        for (int i = 0; i < max; i++) {
            int row = i / ITEM_COLUMNS;
            int column = i % ITEM_COLUMNS;

            int itemX = x + (column * ITEM_SIZE);
            int itemY = y + (row * ITEM_SIZE);

            if (mouseX >= itemX && mouseX < itemX + ITEM_SIZE && mouseY >= itemY && mouseY < itemY + ITEM_SIZE) {
                callback.accept(values.get(i).item());
            }
        }
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            update(this.modeButton.state());
        }
    }

    private List<ItemValue> updateItems(String search) {
        search = search.toLowerCase(Locale.ROOT).trim();

        List<ItemValue> filteredItems = new ArrayList<>();
        for (ItemValue item : items) {
            if (filteredItems.size() >= ITEM_COLUMNS * ITEM_ROWS) break;
            Component desc = ModUtils.throwStackoverflow(item, ItemValue::getDisplayName);
            Component namespace = ModUtils.throwStackoverflow(item, ItemValue::getNamespace);
            if (desc.getString().toLowerCase(Locale.ROOT).contains(search) || namespace.getString().toLowerCase(Locale.ROOT).contains(search) || item.getId().toString().startsWith(search) || item.getId().getPath().startsWith(search)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    public void setCallback(Consumer<Either<ItemStack, TagKey<Item>>> callback) {
        this.callback = callback;
    }

    public void setCurrent(Either<ItemStack, TagKey<Item>> current) {
        this.current = current;
    }

    public void setTagsAllowed(boolean tagsAllowed) {
        this.tagsAllowed = tagsAllowed;
    }
}
