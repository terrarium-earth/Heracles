package earth.terrarium.heracles.api.client;

import com.mojang.blaze3d.platform.InputConstants;
import earth.terrarium.heracles.client.compat.RecipeViewerHelper;
import net.minecraft.world.item.ItemStack;

public interface ItemDisplayWidget extends DisplayWidget {

    ItemStack getCurrentItem();

    @Override
    default boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        boolean inBound = (mouseX >= 5 && mouseX < 37) && (mouseY >= 5 && mouseY < 37);
        if (inBound) {
            if (mouseButton == InputConstants.MOUSE_BUTTON_RIGHT) {
                RecipeViewerHelper.showUsage(this.getCurrentItem());
            } else if (mouseButton == InputConstants.MOUSE_BUTTON_LEFT) {
                RecipeViewerHelper.showRecipes(this.getCurrentItem());
            }
            return true;
        }
        return DisplayWidget.super.mouseClicked(mouseX, mouseY, mouseButton, width);
    }
}
