package earth.terrarium.heracles.client.compat.rei;

import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.rei.api.client.config.ConfigObject;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.world.item.ItemStack;

public class ReiViewerHelper {

    public static void showItem(ItemStack stack) {
        if (isDown(ConfigObject.getInstance().getRecipeKeybind())) {
            showRecipes(stack);
        } else if (isDown(ConfigObject.getInstance().getUsageKeybind())) {
            showUsage(stack);
        }
    }

    public static void showRecipes(ItemStack stack) {
        EntryStack<?> entry = EntryStack.of(VanillaEntryTypes.ITEM, stack);
        ViewSearchBuilder.builder().addRecipesFor(entry).open();
    }

    public static void showUsage(ItemStack stack) {
        EntryStack<?> entry = EntryStack.of(VanillaEntryTypes.ITEM, stack);
        ViewSearchBuilder.builder().addUsagesFor(entry).open();
    }

    private static boolean isDown(ModifierKeyCode code) {
        return code.matchesCurrentKey() || code.matchesCurrentMouse();
    }
}
