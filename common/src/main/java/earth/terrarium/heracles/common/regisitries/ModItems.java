package earth.terrarium.heracles.common.regisitries;

import com.teamresourceful.resourcefullib.common.registry.ItemLikeResourcefulRegistry;
import com.teamresourceful.resourcefullib.common.registry.ItemLikeResourcefulRegistry.Entry;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.CommonConfig;
import earth.terrarium.heracles.common.items.BarrierItem;
import earth.terrarium.heracles.common.items.QuestBookItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class ModItems {

    public static final ItemLikeResourcefulRegistry<Item> ITEMS = new ItemLikeResourcefulRegistry<>(BuiltInRegistries.ITEM, Heracles.MOD_ID);

    public static final @Nullable Entry<Item> QUEST_BOOK = !CommonConfig.registerBook ? null : ITEMS.register("quest_book", () -> new QuestBookItem(new Item.Properties()));
    public static final @Nullable Entry<Item> BARRIER = ModBlocks.BARRIER_BLOCK == null ? null : ITEMS.register("barrier", () -> new BarrierItem(ModBlocks.BARRIER_BLOCK.get(), new Item.Properties()));

}
