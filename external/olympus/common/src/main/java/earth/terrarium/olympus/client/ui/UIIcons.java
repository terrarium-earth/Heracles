package earth.terrarium.olympus.client.ui;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class UIIcons {

    public static final List<Identifier> ICONS = new ArrayList<>();

    public static final Identifier BATTERY_EMPTY = create("battery_empty");
    public static final Identifier BATTERY = BATTERY_EMPTY;

    public static final Identifier BATTERY_LOW = create("battery_low");
    public static final Identifier BATTERY_QUARTER = BATTERY_LOW;

    public static final Identifier BATTERY_MEDIUM = create("battery_medium");
    public static final Identifier BATTERY_HALF = BATTERY_MEDIUM;

    public static final Identifier BATTERY_FULL = create("battery_full");
    public static final Identifier BATTERY_FILLED = BATTERY_FULL;

    public static final Identifier BOOKMARK = create("bookmark");

    public static final Identifier BOX = create("box");
    public static final Identifier ARCHIVE = BOX;

    public static final Identifier CALENDAR = create("calendar");

    public static final Identifier CHECKMARK = create("checkmark");
    public static final Identifier CHECK = CHECKMARK;

    public static final Identifier CLIPBOARD = create("clipboard");

    public static final Identifier CLIPBOARD_TEXT = create("clipboard_text");

    public static final Identifier CONTROLLER = create("controller");
    public static final Identifier GAMEPAD = CONTROLLER;

    public static final Identifier COPY = create("copy");

    public static final Identifier CROP = create("crop");

    public static final Identifier CROSS = create("cross");
    public static final Identifier X = CROSS;

    public static final Identifier DASH = create("dash");
    public static final Identifier MINUS = DASH;

    public static final Identifier DOWNLOAD = create("download");
    public static final Identifier ARROW_DOWN = DOWNLOAD;

    public static final Identifier EDIT = create("edit");
    public static final Identifier PENCIL = EDIT;

    public static final Identifier EXTERNAL_LINK = create("external_link");

    public static final Identifier EYE_DROPPER = create("eye_dropper");
    public static final Identifier COLOR_PICKER = EYE_DROPPER;
    public static final Identifier DROPPER = EYE_DROPPER;

    public static final Identifier FILE = create("file");

    public static final Identifier FOLDER = create("folder");

    public static final Identifier GRID = create("grid");

    public static final Identifier HEART = create("heart");
    public static final Identifier FAVORITE = HEART;

    public static final Identifier LAYOUT = create("layout");
    public static final Identifier TABLE = LAYOUT;

    public static final Identifier LINK = create("link");
    public static final Identifier CHAIN = LINK;

    public static final Identifier LIST = create("list");
    public static final Identifier MENU = LIST;

    public static final Identifier LOGIN = create("login");
    public static final Identifier SIGN_IN = LOGIN;

    public static final Identifier LOGOUT = create("logout");
    public static final Identifier SIGN_OUT = LOGOUT;

    public static final Identifier MAGNIFYING_GLASS = create("magnifying_glass");
    public static final Identifier SEARCH = MAGNIFYING_GLASS;

    public static final Identifier MAGNIFYING_GLASS_MINUS = create("magnifying_glass_minus");
    public static final Identifier SEARCH_MINUS = MAGNIFYING_GLASS_MINUS;

    public static final Identifier MAGNIFYING_GLASS_PLUS = create("magnifying_glass_plus");
    public static final Identifier SEARCH_PLUS = MAGNIFYING_GLASS_PLUS;

    public static final Identifier MAXIMIZE = create("maximize");
    public static final Identifier EXPAND = MAXIMIZE;

    public static final Identifier MINIMIZE = create("minimize");
    public static final Identifier COLLAPSE = MINIMIZE;

    public static final Identifier REFRESH = create("refresh");

    public static final Identifier MODRINTH = create("modrinth");

    public static final Identifier MONITOR = create("monitor");
    public static final Identifier SCREEN = MONITOR;
    public static final Identifier DISPLAY = MONITOR;

    public static final Identifier MOUSE = create("mouse");

    public static final Identifier NEWPAPER = create("newspaper");
    public static final Identifier NEWS = NEWPAPER;

    public static final Identifier PERSON = create("person");
    public static final Identifier USER = PERSON;

    public static final Identifier PERSON_CROSS = create("person_cross");
    public static final Identifier USER_CROSS = PERSON_CROSS;
    public static final Identifier PERSON_X = PERSON_CROSS;
    public static final Identifier USER_X = PERSON_CROSS;

    public static final Identifier PERSON_PLUS = create("person_plus");
    public static final Identifier USER_PLUS = PERSON_PLUS;
    public static final Identifier PERSON_ADD = PERSON_PLUS;
    public static final Identifier USER_ADD = PERSON_PLUS;

    public static final Identifier PERSON_MINUS = create("person_minus");
    public static final Identifier USER_MINUS = PERSON_MINUS;
    public static final Identifier PERSON_REMOVE = PERSON_MINUS;
    public static final Identifier USER_REMOVE = PERSON_MINUS;

    public static final Identifier PLUS = create("plus");
    public static final Identifier ADD = PLUS;

    public static final Identifier SAVE = create("save");
    public static final Identifier DISK = SAVE;

    public static final Identifier SPEAKER = create("speaker");

    public static final Identifier TAG = create("tag");
    public static final Identifier LABEL = TAG;

    public static final Identifier TEST_TUBE = create("test_tube");

    public static final Identifier THUMBS_DOWN = create("thumbs_down");
    public static final Identifier DOWNVOTE = THUMBS_DOWN;

    public static final Identifier THUMBS_UP = create("thumbs_up");
    public static final Identifier UPVOTE = THUMBS_UP;

    public static final Identifier TRASH = create("trash");
    public static final Identifier DELETE = TRASH;

    public static final Identifier TV = create("tv");
    public static final Identifier TELEVISION = TV;

    public static final Identifier VOLUME_HIGH = create("volume_high");

    public static final Identifier VOLUME_LOW = create("volume_low");

    public static final Identifier VOLUME_MEDIUM = create("volume_medium");

    public static final Identifier VOLUME_NONE = create("volume_none");

    public static final Identifier VOLUME_OFF = create("volume_off");
    public static final Identifier MUTE = VOLUME_OFF;

    // These 2 icons are special and are size 10x10 instead of the normal 12x12
    public static final Identifier CHEVRON_DOWN = UIConstants.id("icons/chevron_down");
    public static final Identifier CHEVRON_UP = UIConstants.id("icons/chevron_up");

    private static Identifier create(String name) {
        Identifier icon = UIConstants.id("icons/%s".formatted(name));
        ICONS.add(icon);
        return icon;
    }

    public static List<Identifier> getIcons() {
        return ICONS;
    }
}
