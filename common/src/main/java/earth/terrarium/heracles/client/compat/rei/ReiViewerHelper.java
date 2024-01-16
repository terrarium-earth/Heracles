package earth.terrarium.heracles.client.compat.rei;

import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.rei.api.client.config.ConfigObject;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.world.item.ItemStack;

public class ReiViewerHelper {

    public static void showItem(ItemStack stack) {
        EntryStack<?> entry = EntryStack.of(VanillaEntryTypes.ITEM, stack);
        if (isDown(ConfigObject.getInstance().getRecipeKeybind())) {
            ViewSearchBuilder.builder().addRecipesFor(entry).open();
        } else if (isDown(ConfigObject.getInstance().getUsageKeybind())) {
            ViewSearchBuilder.builder().addUsagesFor(entry).open();
        }
    }

    private static boolean isDown(ModifierKeyCode code) {
        return code.matchesCurrentKey() || code.matchesCurrentMouse();
    }
}
