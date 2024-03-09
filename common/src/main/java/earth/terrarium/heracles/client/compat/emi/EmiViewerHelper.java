package earth.terrarium.heracles.client.compat.emi;

import com.mojang.blaze3d.platform.InputConstants;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class EmiViewerHelper {

    public static void showItem(ItemStack stack) {
        // These keybinds are hardcoded because EMI does not expose the keybinds to the API and away to know if they are pressed
        boolean usages = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_U);
        boolean recipes = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_R);
        if (usages) {
            EmiApi.displayUses(EmiStack.of(stack));
        } else if (recipes) {
            EmiApi.displayRecipes(EmiStack.of(stack));
        }
    }
}
