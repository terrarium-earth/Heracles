package earth.terrarium.heracles.common.regisitries;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.items.QuestBookItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(BuiltInRegistries.ITEM, Heracles.MOD_ID);

    public static final RegistryEntry<Item> QUEST_BOOK = ITEMS.register("quest_book", () -> new QuestBookItem(new Item.Properties()));

}
