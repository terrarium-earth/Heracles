package earth.terrarium.heracles.client.compat;

import com.teamresourceful.resourcefullib.common.utils.modinfo.ModInfoUtils;
import earth.terrarium.heracles.client.compat.rei.ReiViewerHelper;
import net.minecraft.world.item.ItemStack;

public class RecipeViewerHelper {

    public static final boolean isReiInstalled = ModInfoUtils.isModLoaded("roughlyenoughitems");

    public static void showItem(ItemStack stack) {
        if (stack != null && isReiInstalled) {
            ReiViewerHelper.showItem(stack);
        }
    }
}
