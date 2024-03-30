package earth.terrarium.heracles.client.compat;

import com.teamresourceful.resourcefullib.common.utils.modinfo.ModInfoUtils;
import earth.terrarium.heracles.client.compat.emi.EmiViewerHelper;
import earth.terrarium.heracles.client.compat.jei.HeraclesJeiModPlugin;
import earth.terrarium.heracles.client.compat.rei.ReiViewerHelper;
import net.minecraft.world.item.ItemStack;

public class RecipeViewerHelper {

    public static final boolean isReiInstalled = ModInfoUtils.isModLoaded("roughlyenoughitems");
    public static final boolean isEmiInstalled = ModInfoUtils.isModLoaded("emi");
    public static final boolean isJeiInstalled = ModInfoUtils.isModLoaded("jei");

    public static void showItem(ItemStack stack) {
        if (stack == null) return;
        if (stack.isEmpty()) return;
        if (isEmiInstalled) {
            EmiViewerHelper.showItem(stack);
        } else if (isJeiInstalled) {
            HeraclesJeiModPlugin.viewRecipes(stack);
        } else if (isReiInstalled) {
            ReiViewerHelper.showItem(stack);
        }
    }

    public static void showRecipes(ItemStack stack) {
        if (stack == null) return;
        if (stack.isEmpty()) return;
        if (isEmiInstalled) {
            EmiViewerHelper.showRecipes(stack);
        } else if (isJeiInstalled) {
            HeraclesJeiModPlugin.showRecipes(stack);
        } else if (isReiInstalled) {
            ReiViewerHelper.showRecipes(stack);
        }
    }

    public static void showUsage(ItemStack stack) {
        if (stack == null) return;
        if (stack.isEmpty()) return;
        if (isEmiInstalled) {
            EmiViewerHelper.showUsage(stack);
        } else if (isJeiInstalled) {
            HeraclesJeiModPlugin.showUsage(stack);
        } else if (isReiInstalled) {
            ReiViewerHelper.showUsage(stack);
        }
    }
}
