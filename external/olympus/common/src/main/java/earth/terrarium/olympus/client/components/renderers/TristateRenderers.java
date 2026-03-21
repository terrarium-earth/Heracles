package earth.terrarium.olympus.client.components.renderers;

import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.constants.MinecraftColors;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.UIIcons;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class TristateRenderers {

    public static Component getText(TriState state) {
        return switch (state) {
            case TRUE -> Component.translatable("olympus.ui.tristate.true");
            case FALSE -> Component.translatable("olympus.ui.tristate.false");
            case UNDEFINED -> Component.translatable("olympus.ui.tristate.undefined");
        };
    }

    public static Identifier getIcon(TriState state) {
        return switch (state) {
            case TRUE -> UIIcons.CHECKMARK;
            case FALSE -> UIIcons.CROSS;
            case UNDEFINED -> UIIcons.DASH;
        };
    }

    public static Color getColor(TriState state) {
        return switch (state) {
            case TRUE -> MinecraftColors.DARK_GREEN;
            case FALSE -> MinecraftColors.RED;
            case UNDEFINED -> MinecraftColors.DARK_GRAY;
        };
    }

    public static WidgetSprites getButtonSprites(TriState state) {
        return switch (state) {
            case TRUE -> UIConstants.PRIMARY_BUTTON;
            case FALSE -> UIConstants.DANGER_BUTTON;
            case UNDEFINED -> UIConstants.DARK_BUTTON;
        };
    }

    public static <T extends AbstractWidget> WidgetRenderer<T> iconWithText(TriState state) {
        return WidgetRenderers.<T>textWithIcon(getText(state), getIcon(state))
                .withShadow()
                .withTextLeftIconLeft()
                .withIconSize(12)
                .withGap(6)
                .withColor(getColor(state));
    }

    public static <T extends AbstractWidget> WidgetRenderer<T> icon(TriState state) {
        return WidgetRenderers.<T>icon(getIcon(state))
                .withShadow()
                .withColor(getColor(state))
                .withCentered(12, 12);
    }
}
