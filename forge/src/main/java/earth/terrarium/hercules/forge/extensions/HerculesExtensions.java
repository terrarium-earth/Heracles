package earth.terrarium.hercules.forge.extensions;

import com.mojang.serialization.Codec;
import earth.terrarium.hercules.Hercules;
import earth.terrarium.hercules.condition.QuestCondition;
import earth.terrarium.hercules.forge.HerculesForge;
import earth.terrarium.hercules.reward.QuestReward;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.msrandom.extensions.annotations.ClassExtension;
import net.msrandom.extensions.annotations.ImplementsBaseElement;
import org.apache.commons.lang3.NotImplementedException;

import java.util.stream.Stream;

@ClassExtension(Hercules.class)
public class HerculesExtensions {
    @ImplementsBaseElement
    public static void grantCriteria(ServerPlayer player, Stream<Criterion> criterion) {
        throw new NotImplementedException();
    }

    @ImplementsBaseElement
    public static Codec<Codec<? extends QuestCondition>> getConditionRegistryCodec() {
        return HerculesForge.CONDITION_REGISTRY.get().getCodec();
    }

    @ImplementsBaseElement
    public static Codec<Codec<? extends QuestReward>> getRewardRegistryCodec() {
        return HerculesForge.REWARD_REGISTRY.get().getCodec();
    }
}
