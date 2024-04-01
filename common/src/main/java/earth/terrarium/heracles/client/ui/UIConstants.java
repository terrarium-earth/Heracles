package earth.terrarium.heracles.client.ui;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.widgets.WidgetSprites;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class UIConstants {

    public static final ResourceLocation CONTENT = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/screen/content.png");
    public static final ResourceLocation SIDEBAR = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/screen/sidebar.png");
    public static final ResourceLocation CONTENT_HEADER = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/screen/content_header.png");
    public static final ResourceLocation SIDEBAR_HEADER = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/screen/sidebar_header.png");
    public static final ResourceLocation OVERVIEW = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/screen/overview.png");
    public static final ResourceLocation REWARD_OVERVIEW = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/screen/overview_info.png");
    public static final ResourceLocation GROUPS = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/groups/background.png");

    public static final ResourceLocation LOCKED_HEADING = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/headings/locked.png");
    public static final ResourceLocation IN_PROGRESS_HEADING = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/headings/in_progress.png");
    public static final ResourceLocation CLAIMABLE_HEADING = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/headings/claimable.png");
    public static final ResourceLocation CLAIMED_HEADING = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/headings/claimed.png");
    public static final ResourceLocation DEPENDENTS_HEADING = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/headings/dependents.png");

    public static final ResourceLocation MODAL = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/modal/modal.png");
    public static final ResourceLocation MODAL_HEADER = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/modal/modal_header.png");

    public static final ResourceLocation ITEM_BACKGROUND = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/item_background.png");

    public static final WidgetSprites BUTTON = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/hovered.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/disabled.png")
    );

    public static final WidgetSprites PRIMARY_BUTTON = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/primary/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/primary/hovered.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/disabled.png")
    );

    public static final WidgetSprites DANGER_BUTTON = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/danger/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/danger/hovered.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/disabled.png")
    );

    public static final WidgetSprites BACK = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/back/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/back/hovered.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/back/disabled.png")
    );

    public static final WidgetSprites CLOSE = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/close/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/close/hovered.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/close/disabled.png")
    );

    public static final WidgetSprites EDIT = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/edit/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/edit/hovered.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/edit/disabled.png")
    );

    public static final WidgetSprites FILE = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/file/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/file/hovered.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/file/disabled.png")
    );

    public static final WidgetSprites LIST_EDIT = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/lists/buttons/edit/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/lists/buttons/edit/hovered.png")
    );

    public static final WidgetSprites LIST_DELETE = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/lists/buttons/delete/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/lists/buttons/delete/hovered.png")
    );

    public static final WidgetSprites MODAL_CLOSE = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/modal/buttons/close/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/modal/buttons/close/hovered.png")
    );

    public static final WidgetSprites MODAL_SAVE = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/modal/buttons/save/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/modal/buttons/save/hovered.png")
    );

    public static final WidgetSprites SEARCH_INVENTORY = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/inventory/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/inventory/hovered.png")
    );

    public static final WidgetSprites SEARCH_ALL = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/registry/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/registry/hovered.png")
    );

    public static void blitWithEdge(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, int size) {
        graphics.blitNineSliced(texture, x, y, width, height, size, 256, 256, 0, 0);
    }
}
