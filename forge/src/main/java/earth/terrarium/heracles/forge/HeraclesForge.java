package earth.terrarium.heracles.forge;

import com.mojang.serialization.Codec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.condition.AllOfQuestCondition;
import earth.terrarium.heracles.condition.AnyOfQuestCondition;
import earth.terrarium.heracles.condition.QuestCondition;
import earth.terrarium.heracles.reward.FunctionQuestReward;
import earth.terrarium.heracles.reward.LootQuestReward;
import earth.terrarium.heracles.reward.QuestReward;
import earth.terrarium.heracles.reward.RecipesQuestReward;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Function;
import java.util.function.Supplier;

@Mod(Heracles.MOD_ID)
public class HeraclesForge {
    private static final DeferredRegister<Function<DeserializationContext, Codec<? extends QuestCondition>>> CONDITION_TYPE_REGISTRAR = DeferredRegister.create(Heracles.QUEST_CONDITION_TYPE_REGISTRY_KEY, Heracles.MOD_ID);
    private static final DeferredRegister<Codec<? extends QuestReward>> REWARD_TYPE_REGISTRAR = DeferredRegister.create(Heracles.QUEST_REWARD_TYPE_REGISTRY_KEY, Heracles.MOD_ID);

    public static final Supplier<IForgeRegistry<Function<DeserializationContext, Codec<? extends QuestCondition>>>> CONDITION_REGISTRY = CONDITION_TYPE_REGISTRAR.makeRegistry(() ->
            new RegistryBuilder<Function<DeserializationContext, Codec<? extends QuestCondition>>>()
                    .setName(Heracles.QUEST_CONDITION_TYPE_REGISTRY_KEY.location())
    );

    public static final Supplier<IForgeRegistry<Codec<? extends QuestReward>>> REWARD_REGISTRY = REWARD_TYPE_REGISTRAR.makeRegistry(() ->
            new RegistryBuilder<Codec<? extends QuestReward>>()
                    .setName(Heracles.QUEST_REWARD_TYPE_REGISTRY_KEY.location())
    );

    public HeraclesForge() {
        CONDITION_TYPE_REGISTRAR.register(AllOfQuestCondition.KEY, () -> AllOfQuestCondition::simpleCodec);
        CONDITION_TYPE_REGISTRAR.register(AnyOfQuestCondition.KEY, () -> AnyOfQuestCondition::simpleCodec);

        REWARD_TYPE_REGISTRAR.register(LootQuestReward.KEY, LootQuestReward.MAP_CODEC::codec);
        REWARD_TYPE_REGISTRAR.register(RecipesQuestReward.KEY, RecipesQuestReward.MAP_CODEC::codec);
        REWARD_TYPE_REGISTRAR.register(FunctionQuestReward.KEY, () -> FunctionQuestReward.CODEC);
    }
}
