package earth.terrarium.heracles.client.ui;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.widgets.WidgetSprites;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class UIConstants {

    public static final ResourceLocation CONTENT = Heracles.id("screen/content");
    public static final ResourceLocation SIDEBAR = Heracles.id("screen/sidebar");
    public static final ResourceLocation CONTENT_HEADER = Heracles.id("screen/content_header");
    public static final ResourceLocation SIDEBAR_HEADER = Heracles.id("screen/sidebar_header");
    public static final ResourceLocation OVERVIEW = Heracles.id("screen/overview");
    public static final ResourceLocation REWARD_OVERVIEW = Heracles.id("screen/overview_info");
    public static final ResourceLocation GROUPS = Heracles.id("groups/background");

    public static final ResourceLocation LOCKED_HEADING_LEFT = Heracles.id("headings/locked_left");
    public static final ResourceLocation LOCKED_HEADING_RIGHT = Heracles.id("headings/locked_right");
    public static final ResourceLocation IN_PROGRESS_HEADING_LEFT = Heracles.id("headings/in_progress_left");
    public static final ResourceLocation IN_PROGRESS_HEADING_RIGHT = Heracles.id("headings/in_progress_right");
    public static final ResourceLocation CLAIMABLE_HEADING_LEFT = Heracles.id("headings/claimable_left");
    public static final ResourceLocation CLAIMABLE_HEADING_RIGHT = Heracles.id("headings/claimable_right");
    public static final ResourceLocation CLAIMED_HEADING_LEFT = Heracles.id("headings/claimed_left");
    public static final ResourceLocation CLAIMED_HEADING_RIGHT = Heracles.id("headings/claimed_right");
    public static final ResourceLocation DEPENDENTS_HEADING_LEFT = Heracles.id("headings/dependents_left");
    public static final ResourceLocation DEPENDENTS_HEADING_RIGHT = Heracles.id("headings/dependents_right");

    public static final ResourceLocation MODAL = Heracles.id("modal/modal");
    public static final ResourceLocation MODAL_HEADER = Heracles.id("modal/modal_header");

    public static final ResourceLocation ITEM_BACKGROUND = Heracles.id("buttons/item_background");



    public static final WidgetSprites BUTTON = new WidgetSprites(
        Heracles.id("textures/gui/sprites/buttons/normal.png"),
        Heracles.id("textures/gui/sprites/buttons/hovered.png"),
        Heracles.id("textures/gui/sprites/buttons/disabled.png")
    );

    public static final WidgetSprites PRIMARY_BUTTON = new WidgetSprites(
        Heracles.id("textures/gui/sprites/buttons/primary/normal.png"),
        Heracles.id("textures/gui/sprites/buttons/primary/hovered.png"),
        Heracles.id("textures/gui/sprites/buttons/disabled.png")
    );

    public static final WidgetSprites DANGER_BUTTON = new WidgetSprites(
        Heracles.id("textures/gui/sprites/buttons/danger/normal.png"),
        Heracles.id("textures/gui/sprites/buttons/danger/hovered.png"),
        Heracles.id("textures/gui/sprites/buttons/disabled.png")
    );

    public static final WidgetSprites BACK = new WidgetSprites(
        Heracles.id("textures/gui/sprites/buttons/back/normal.png"),
        Heracles.id("textures/gui/sprites/buttons/back/hovered.png"),
        Heracles.id("textures/gui/sprites/buttons/back/disabled.png")
    );

    public static final WidgetSprites CLOSE = new WidgetSprites(
        Heracles.id("textures/gui/sprites/buttons/close/normal.png"),
        Heracles.id("textures/gui/sprites/buttons/close/hovered.png"),
        Heracles.id("textures/gui/sprites/buttons/close/disabled.png")
    );

    public static final WidgetSprites EDIT = new WidgetSprites(
        Heracles.id("textures/gui/sprites/buttons/edit/normal.png"),
        Heracles.id("textures/gui/sprites/buttons/edit/hovered.png"),
        Heracles.id("textures/gui/sprites/buttons/edit/disabled.png")
    );

    public static final WidgetSprites FILE = new WidgetSprites(
        Heracles.id("textures/gui/sprites/buttons/file/normal.png"),
        Heracles.id("textures/gui/sprites/buttons/file/hovered.png"),
        Heracles.id("textures/gui/sprites/buttons/file/disabled.png")
    );

    public static final WidgetSprites LIST_EDIT = new WidgetSprites(
        Heracles.id("textures/gui/sprites/lists/buttons/edit/normal.png"),
        Heracles.id("textures/gui/sprites/lists/buttons/edit/hovered.png")
    );

    public static final WidgetSprites LIST_DELETE = new WidgetSprites(
        Heracles.id("textures/gui/sprites/lists/buttons/delete/normal.png"),
        Heracles.id("textures/gui/sprites/lists/buttons/delete/hovered.png")
    );

    public static final WidgetSprites MODAL_CLOSE = new WidgetSprites(
        Heracles.id("textures/gui/sprites/modal/buttons/close/normal.png"),
        Heracles.id("textures/gui/sprites/modal/buttons/close/hovered.png")
    );

    public static final WidgetSprites MODAL_SAVE = new WidgetSprites(
        Heracles.id("textures/gui/sprites/modal/buttons/save/normal.png"),
        Heracles.id("textures/gui/sprites/modal/buttons/save/hovered.png")
    );

    public static final WidgetSprites SEARCH_INVENTORY = new WidgetSprites(
        Heracles.id("textures/gui/sprites/buttons/inventory/normal.png"),
        Heracles.id("textures/gui/sprites/buttons/inventory/hovered.png")
    );

    public static final WidgetSprites SEARCH_ALL = new WidgetSprites(
        Heracles.id("textures/gui/sprites/buttons/registry/normal.png"),
        Heracles.id("textures/gui/sprites/buttons/registry/hovered.png")
    );

    public static final WidgetSprites ADD = new WidgetSprites(
        Heracles.id("textures/gui/sprites/heading/add.png"),
        Heracles.id("textures/gui/sprites/heading/add_selected.png")
    );

    public static final WidgetSprites SNAP_TO_GRID = new WidgetSprites(
        Heracles.id("textures/gui/sprites/heading/snap_to_grid.png"),
        Heracles.id("textures/gui/sprites/heading/snap_to_grid_selected.png")
    );

    public static final WidgetSprites SHOW_GRID = new WidgetSprites(
        Heracles.id("textures/gui/sprites/heading/show_grid.png"),
        Heracles.id("textures/gui/sprites/heading/show_grid_selected.png")
    );

    public static final WidgetSprites TOGGLE_MINIMAP = new WidgetSprites(
        Heracles.id("textures/gui/sprites/heading/toggle_minimap.png"),
        Heracles.id("textures/gui/sprites/heading/toggle_minimap_selected.png")
    );

    public static final WidgetSprites TOGGLE_MINIMAP_DOCKED = new WidgetSprites(
        Heracles.id("textures/gui/sprites/heading/dock_minimap.png"),
        Heracles.id("textures/gui/sprites/heading/dock_minimap_selected.png")
    );

    public static void blitWithEdge(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, int size) {
        graphics.blitSprite(texture, x, y, width, height);
    }
}
