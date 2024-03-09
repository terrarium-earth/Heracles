package earth.terrarium.heracles.client.compat.jei;

import com.mojang.blaze3d.platform.InputConstants;
import earth.terrarium.heracles.Heracles;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@JeiPlugin
public class HeraclesJeiModPlugin implements IModPlugin {

    private static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "jei");
    private static IJeiRuntime runtime = null;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime runtime) {
        HeraclesJeiModPlugin.runtime = runtime;
    }

    public static void viewRecipes(ItemStack stack) {
        if (HeraclesJeiModPlugin.runtime == null) return;
        if (stack == null) return;
        if (stack.isEmpty()) return;
        // These keybinds are hardcoded because JEI does not provide away to check if a keybinding is pressed in the api without already knowing the key...
        boolean usages = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_U);
        boolean recipes = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_R);

        if (usages) {
            HeraclesJeiModPlugin.runtime.getJeiHelpers().getFocusFactory()
                .createFocus(RecipeIngredientRole.INPUT, VanillaTypes.ITEM_STACK, stack);
        } else if (recipes) {
            HeraclesJeiModPlugin.runtime.getJeiHelpers().getFocusFactory()
                .createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, stack);
        }
    }

}
