package earth.terrarium.heracles.fabric.extensions;

import com.mojang.serialization.Codec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.condition.QuestCondition;
import earth.terrarium.heracles.fabric.HeraclesFabric;
import earth.terrarium.heracles.reward.QuestReward;
import earth.terrarium.heracles.team.TeamProvider;
import net.msrandom.extensions.annotations.ClassExtension;
import net.msrandom.extensions.annotations.ImplementsBaseElement;

@ClassExtension(Heracles.class)
public class HeraclesExtensions {
    @ImplementsBaseElement
    public static Codec<Codec<? extends QuestCondition>> getConditionRegistryCodec() {
        return HeraclesFabric.CONDITION_REGISTRY.byNameCodec();
    }

    @ImplementsBaseElement
    public static Codec<Codec<? extends QuestReward>> getRewardRegistryCodec() {
        return HeraclesFabric.REWARD_REGISTRY.byNameCodec();
    }

    @ImplementsBaseElement
    public static Iterable<TeamProvider> getTeamProviders() {
        return HeraclesFabric.TEAM_PROVIDER_REGISTRY;
    }
}
