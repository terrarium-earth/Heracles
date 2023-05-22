package earth.terrarium.heracles.common.regisitries;

import com.teamresourceful.resourcefullib.common.registry.ItemLikeResourcefulRegistry;
import com.teamresourceful.resourcefullib.common.registry.ItemLikeResourcefulRegistry.Entry;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.items.QuestBookItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final ItemLikeResourcefulRegistry<Item> ITEMS = new ItemLikeResourcefulRegistry<>(BuiltInRegistries.ITEM, Heracles.MOD_ID);

    public static final Entry<Item> QUEST_BOOK = ITEMS.register("quest_book", () -> new QuestBookItem(new Item.Properties()));

}
