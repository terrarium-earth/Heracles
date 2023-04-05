package earth.terrarium.heracles.forge.extensions;

import com.mojang.serialization.Codec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.condition.QuestCondition;
import earth.terrarium.heracles.forge.HeraclesForge;
import earth.terrarium.heracles.reward.QuestReward;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.server.level.ServerPlayer;
import net.msrandom.extensions.annotations.ClassExtension;
import net.msrandom.extensions.annotations.ImplementsBaseElement;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Function;
import java.util.stream.Stream;

@ClassExtension(Heracles.class)
public class HeraclesExtensions {
    @ImplementsBaseElement
    public static void grantCriteria(ServerPlayer player, Stream<Criterion> criterion) {
        throw new NotImplementedException();
    }

    @ImplementsBaseElement
    public static Codec<Function<DeserializationContext, Codec<? extends QuestCondition>>> getConditionRegistryCodec() {
        return HeraclesForge.CONDITION_REGISTRY.get().getCodec();
    }

    @ImplementsBaseElement
    public static Codec<Codec<? extends QuestReward>> getRewardRegistryCodec() {
        return HeraclesForge.REWARD_REGISTRY.get().getCodec();
    }
}
