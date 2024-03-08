package earth.terrarium.heracles.client.components.widgets.item;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.ClearableGridLayout;
import earth.terrarium.heracles.client.components.widgets.buttons.SpriteButton;
import earth.terrarium.heracles.client.components.widgets.textbox.TextBox;
import earth.terrarium.heracles.client.ui.Overlay;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.utils.UIUtils;
import earth.terrarium.heracles.common.utils.ItemValue;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ItemScreen extends Overlay {

    private static final int ENTRY_SIZE = 16;
    private static final int ENTRY_COLUMNS = 7;
    private static final int ENTRY_ROWS = 5;
    private static final int SEARCH_HEIGHT = 20;
    private static final int PADDING = 4 * 2;
    private static final int SPACING = 2;

    private static final Component SHOW_ALL = Component.literal("Show all items");
    private static final Component SHOW_INVENTORY = Component.literal("Show inventory");

    private final AtomicReference<ItemValue> reference;
    private final boolean allowsTags;
    private final ItemButton button;
    private final List<ItemValue> items = new ArrayList<>();

    private TextBox search;
    private SpriteButton inventoryToggle;
    private ClearableGridLayout grid;
    private int scroll = 0;
    private boolean showInventory = false;

    protected ItemScreen(Screen background, ItemButton button, boolean allowsTags) {
        super(background);
        this.reference = button.reference();
        this.button = button;
        this.allowsTags = allowsTags;
    }

    public int x() {
        return this.button.getX() + this.button.getWidth() - width();
    }

    public int y() {
        int y = this.button.getY() + this.button.getHeight();
        if (y + this.height() > this.background.height) {
            y = this.button.getY() - this.height();
        }
        return y;
    }

    public int width() {
        return ENTRY_COLUMNS * ENTRY_SIZE + (ENTRY_COLUMNS - 1) * SPACING + PADDING * 2;
    }

    public int height() {
        return ENTRY_SIZE * ENTRY_ROWS + (ENTRY_ROWS - 1) * SPACING + SEARCH_HEIGHT + PADDING * 3;
    }

    @Override
    protected void init() {
        GridLayout layout = new GridLayout(this.x() + PADDING, this.y() + PADDING);

        this.search = layout.addChild(
            new TextBox(this.search, "", this.width() - PADDING * 3 - SEARCH_HEIGHT, SEARCH_HEIGHT, Short.MAX_VALUE, s -> true, text -> {
                this.scroll = 0;
                update(text);
            }),
            0, 0
        );
        this.search.setPlaceholder(Component.literal("Search..."));

        //noinspection SuspiciousNameCombination
        this.inventoryToggle = layout.addChild(
            SpriteButton.create(SEARCH_HEIGHT, SEARCH_HEIGHT, UIConstants.SEARCH_INVENTORY, () -> toggleInventory(!this.showInventory)),
            0, 1,
            layout.newCellSettings().paddingHorizontal(PADDING)
        );


        this.grid = layout.addChild(new ClearableGridLayout(), 1, 0, layout.newCellSettings().paddingTop(PADDING));
        this.grid.spacing(SPACING);

        layout.arrangeElements();
        layout.visitWidgets(this::addRenderableWidget);

        this.toggleInventory(this.showInventory);
        this.setFocused(this.search);
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        UIUtils.blitWithEdge(graphics, UIConstants.MODAL_HEADER, this.x(), this.y(), this.width(), this.height(), 3);
    }

    public void toggleInventory(boolean newValue) {
        this.scroll = 0;
        this.showInventory = newValue;
        this.inventoryToggle = this.inventoryToggle
            .withSprites(this.showInventory ? UIConstants.SEARCH_ALL : UIConstants.SEARCH_INVENTORY)
            .withTooltip(this.showInventory ? SHOW_ALL : SHOW_INVENTORY);
        this.setupItems();
        this.update(this.search.getValue());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean isMouseOver = mouseX >= this.x() && mouseX <= this.x() + this.width() && mouseY >= this.y() && mouseY <= this.y() + this.height();
        if (isMouseOver) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        this.onClose();
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (super.mouseScrolled(mouseX, mouseY, delta)) return true;
        float itemMaxForSearch = 0;
        for (ItemValue item : this.items) {
            if (matches(item, this.search.getValue())) {
                itemMaxForSearch++;
            }
        }
        int maxRows = Mth.ceil(itemMaxForSearch / ENTRY_COLUMNS);
        int oldScroll = this.scroll;
        this.scroll = Mth.clamp(this.scroll - (int) delta * ENTRY_COLUMNS, 0, (maxRows - ENTRY_ROWS) * ENTRY_COLUMNS);
        if (oldScroll != this.scroll) {
            update(this.search.getValue());
        }
        return true;
    }

    public void setupItems() {
        this.items.clear();
        if (this.showInventory) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.inventoryMenu.getItems()
                    .stream()
                    .filter(Predicate.not(ItemStack::isEmpty))
                    .map(ItemValue::new)
                    .forEach(items::add);
            }
        } else {
            if (reference.get() != null && !reference.get().isEmpty()) {
                items.add(reference.get().copy());
            }
            BuiltInRegistries.ITEM.stream().map(ItemValue::new).forEach(items::add);
            if (this.allowsTags) {
                Heracles.getRegistryAccess().registry(Registries.ITEM)
                    .map(Registry::getTagNames)
                    .stream()
                    .flatMap(UnaryOperator.identity())
                    .map(ItemValue::new)
                    .forEach(items::add);
            }
        }
    }

    public void update(String text) {
        text = text.toLowerCase(Locale.ROOT);

        this.grid.visitWidgets(this::removeWidget);
        this.grid.clear();

        List<ItemValue> filteredItems = new ArrayList<>();
        int i = 0;
        for (ItemValue item : items) {
            if (filteredItems.size() >= ENTRY_COLUMNS * ENTRY_ROWS) break;
            if (matches(item, text)) {
                if (i++ < scroll) continue;
                filteredItems.add(item);
            }
        }

        ClearableGridLayout.RowHelper helper = this.grid.rows(1, ENTRY_COLUMNS);
        filteredItems.forEach(item -> helper.addChild(new ItemIconWidget(item, () -> {
            this.reference.set(item.copy());
            this.onClose();
        })));

        this.grid.arrangeElements();
        this.grid.visitWidgets(this::addRenderableWidget);
    }

    private static boolean matches(ItemValue value, String text) {
        if (text.startsWith("#")) {
            if (!value.isTag()) return false;
            text = text.substring(1);
        }
        Component desc = ModUtils.throwStackoverflow(value, ItemValue::getDisplayName);
        Component namespace = ModUtils.throwStackoverflow(value, ItemValue::getNamespace);
        return desc.getString().toLowerCase(Locale.ROOT).contains(text) ||
            namespace.getString().toLowerCase(Locale.ROOT).contains(text) ||
            value.getId().toString().startsWith(text) ||
            value.getId().getPath().startsWith(text);
    }
}
