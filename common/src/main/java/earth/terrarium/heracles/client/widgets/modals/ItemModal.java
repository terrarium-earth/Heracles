package earth.terrarium.heracles.client.widgets.modals;

import com.mojang.datafixers.util.Either;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.widgets.StateImageButton;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.utils.ModUtils;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ItemModal extends BaseModal {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/icons.png");
    private static final int WIDTH = 168;
    private static final int HEIGHT = 173;

    private static final int ITEM_SIZE = 19;
    private static final int ITEM_COLUMNS = 8;
    private static final int ITEM_ROWS = 6;

    private final List<Value> items = new ArrayList<>();
    private Consumer<Either<ItemStack, TagKey<Item>>> callback;
    private Either<ItemStack, TagKey<Item>> current = null;
    private boolean tagsAllowed = true;

    private final EditBox search;
    private final StateImageButton modeButton;

    public ItemModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT, 2);
        this.modeButton = addChild(new StateImageButton(x + 7, y + 5, 11, 11, 168, 0, 11, TEXTURE, 256, 256, 3, this::update));
        this.modeButton.setTooltip(Tooltip.create(Component.literal("Switch Mode")));
        this.search = addChild(new EditBox(Minecraft.getInstance().font, x + 8, y + 19, 152, 14, ConstantComponents.SEARCH));
    }

    public void update(int state) {
        items.clear();
        switch (state) {
            case 0 -> {
                if (current != null && current.map(stack -> !stack.is(Items.AIR), tag -> true)) {
                    Heracles.getRegistryAccess().registry(Registries.ITEM)
                        .ifPresent(registry -> items.add(Value.of(registry, current)));
                }
                BuiltInRegistries.ITEM.stream().map(Value::new).forEach(items::add);
            }
            case 1 -> {
                if (Minecraft.getInstance().player != null) {
                    for (ItemStack item : Minecraft.getInstance().player.inventoryMenu.getItems()) {
                        if (item.isEmpty()) continue;
                        items.add(new Value(item));
                    }
                }
            }
            case 2 ->  {
                if (tagsAllowed) {
                    Heracles.getRegistryAccess().registry(Registries.ITEM)
                        .ifPresent(registry ->
                            registry.getTagNames().map(key -> new Value(registry, key)).forEach(items::add)
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
        int textX = (WIDTH - font.width("Choose Item")) / 2;
        graphics.drawString(
            font,
            "Choose Item", x + textX, y + 6, 0x404040,
            false
        );

        int y = this.y + 43;
        int x = this.x + 8;

        List<Value> values = updateItems(this.search.getValue());

        Component tooltip = CommonComponents.EMPTY;

        int max = Math.min(values.size(), 48);
        for (int i = 0; i < max; i++) {
            int row = i / ITEM_COLUMNS;
            int column = i % ITEM_COLUMNS;

            int itemX = x + (column * ITEM_SIZE);
            int itemY = y + (row * ITEM_SIZE);

            Value value = values.get(i);

            graphics.blit(TEXTURE, itemX, itemY, 168, 22, ITEM_SIZE, ITEM_SIZE, 256, 256);
            if (mouseX >= itemX && mouseX < itemX + ITEM_SIZE && mouseY >= itemY && mouseY < itemY + ITEM_SIZE) {
                graphics.fill(itemX + 1, itemY + 1, itemX + ITEM_SIZE - 1, itemY + ITEM_SIZE - 1, 0x80A0A0A0);
                CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
                tooltip = value.getDescription();
            }
            try (var pose = new CloseablePoseStack(graphics)) {
                pose.translate(0, 0, -100);
                graphics.renderFakeItem(value.getDefaultInstance(), itemX + 2, itemY + 1);
            }
        }
        if (!tooltip.getString().isBlank()) {
            graphics.renderTooltip(font, tooltip, mouseX, mouseY);
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

        List<Value> values = updateItems(this.search.getValue());

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

    private List<Value> updateItems(String search) {
        search = search.toLowerCase(Locale.ROOT).trim();

        List<Value> filteredItems = new ArrayList<>();
        for (Value item : items) {
            if (filteredItems.size() >= ITEM_COLUMNS * ITEM_ROWS) break;
            Component text = ModUtils.throwStackoverflow(item, Value::getDescription);
            if (text.getString().toLowerCase(Locale.ROOT).contains(search)) {
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

    private record Value(Either<ItemStack, TagKey<Item>> item, List<ItemStack> values) {

        public Value(Registry<Item> registry, TagKey<Item> key) {
            this(Either.right(key), registry.getTag(key)
                .map(tag -> tag.stream().map(Holder::value).map(ItemStack::new).toList())
                .orElse(List.of()));
        }

        public Value(Item item) {
            this(Either.left(new ItemStack(item)), List.of(new ItemStack(item)));
        }

        public Value(ItemStack item) {
            this(Either.left(item.copy()), List.of(item.copy()));
        }

        public Component getDescription() {
            return this.item.map(
                ItemStack::getHoverName,
                RegistryValue::getDisplayName
            );
        }

        public ItemStack getDefaultInstance() {
            int index = (int) (System.currentTimeMillis() / 2000) % this.values.size();
            return this.values.get(index);
        }

        public static Value of(Registry<Item> registry, Either<ItemStack, TagKey<Item>> value) {
            return value.map(Value::new, key -> new Value(registry, key));
        }
    }
}
