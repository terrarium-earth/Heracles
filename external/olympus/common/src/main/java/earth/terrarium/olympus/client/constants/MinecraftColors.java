package earth.terrarium.olympus.client.constants;

import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.ChatFormatting;

import java.util.Objects;

public class MinecraftColors {

    public static final Color BLACK = create(ChatFormatting.BLACK);
    public static final Color DARK_BLUE = create(ChatFormatting.DARK_BLUE);
    public static final Color DARK_GREEN = create(ChatFormatting.DARK_GREEN);
    public static final Color DARK_AQUA = create(ChatFormatting.DARK_AQUA);
    public static final Color DARK_RED = create(ChatFormatting.DARK_RED);
    public static final Color DARK_PURPLE = create(ChatFormatting.DARK_PURPLE);
    public static final Color GOLD = create(ChatFormatting.GOLD);
    public static final Color GRAY = create(ChatFormatting.GRAY);
    public static final Color DARK_GRAY = create(ChatFormatting.DARK_GRAY);
    public static final Color BLUE = create(ChatFormatting.BLUE);
    public static final Color GREEN = create(ChatFormatting.GREEN);
    public static final Color AQUA = create(ChatFormatting.AQUA);
    public static final Color RED = create(ChatFormatting.RED);
    public static final Color LIGHT_PURPLE = create(ChatFormatting.LIGHT_PURPLE);
    public static final Color YELLOW = create(ChatFormatting.YELLOW);
    public static final Color WHITE = create(ChatFormatting.WHITE);

    public static final Color[] COLORS = new Color[] {
            BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY,
            DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE
    };

    private static Color create(ChatFormatting formatting) {
        return new Color(Objects.requireNonNull(formatting.getColor(), "Formatting must be a color."));
    }
}
