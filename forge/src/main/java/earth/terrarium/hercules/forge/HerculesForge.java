package earth.terrarium.hercules.forge;

import com.mojang.serialization.Codec;
import earth.terrarium.hercules.Hercules;
import earth.terrarium.hercules.condition.QuestCondition;
import earth.terrarium.hercules.reward.QuestReward;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod(Hercules.MOD_ID)
public class HerculesForge {
    private static final DeferredRegister<Codec<? extends QuestCondition>> CONDITION_REGISTRAR = DeferredRegister.create(Hercules.QUEST_CONDITION_TYPE_REGISTRY_KEY, Hercules.MOD_ID);
    private static final DeferredRegister<Codec<? extends QuestReward>> REWARD_REGISTRAR = DeferredRegister.create(Hercules.QUEST_REWARD_TYPE_REGISTRY_KEY, Hercules.MOD_ID);

    public static final Supplier<IForgeRegistry<Codec<? extends QuestCondition>>> CONDITION_REGISTRY = CONDITION_REGISTRAR.makeRegistry(() ->
            new RegistryBuilder<Codec<? extends QuestCondition>>()
                    .setName(Hercules.QUEST_CONDITION_REGISTRY_KEY.location())
    );

    public static final Supplier<IForgeRegistry<Codec<? extends QuestReward>>> REWARD_REGISTRY = REWARD_REGISTRAR.makeRegistry(() ->
            new RegistryBuilder<Codec<? extends QuestReward>>()
                    .setName(Hercules.QUEST_REWARD_TYPE_REGISTRY_KEY.location())
    );
}
