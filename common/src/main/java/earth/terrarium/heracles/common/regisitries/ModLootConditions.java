package earth.terrarium.heracles.common.regisitries;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.loot.CompletedQuestCondition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class ModLootConditions {

    public static final ResourcefulRegistry<LootItemConditionType> TYPES = ResourcefulRegistries.create(BuiltInRegistries.LOOT_CONDITION_TYPE, Heracles.MOD_ID);

    public static final RegistryEntry<LootItemConditionType> COMPLETED_QUEST = TYPES.register("completed_quest", () -> new LootItemConditionType(new CompletedQuestCondition.Serializer()));
}
