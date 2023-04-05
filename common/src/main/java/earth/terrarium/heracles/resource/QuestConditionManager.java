package earth.terrarium.heracles.resource;

import earth.terrarium.heracles.condition.QuestCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.PredicateManager;

import java.util.Map;

public class QuestConditionManager extends CodecResourceReloader<QuestCondition> {
    private static QuestConditionManager instance;

    public QuestConditionManager(PredicateManager predicateManager) {
        super("quests/condition", QuestCondition::dispatchCodec, predicateManager);
    }

    public static QuestConditionManager getInstance() {
        return instance;
    }

    public Map<ResourceLocation, QuestCondition> getConditions() {
        return getValues();
    }
}
