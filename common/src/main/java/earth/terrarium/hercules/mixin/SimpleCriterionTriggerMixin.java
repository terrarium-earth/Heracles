package earth.terrarium.hercules.mixin;

import earth.terrarium.hercules.Hercules;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.Predicate;

@Mixin(SimpleCriterionTrigger.class)
public abstract class SimpleCriterionTriggerMixin<T extends CriterionTriggerInstance> implements CriterionTrigger<T> {
    @SuppressWarnings("unchecked")
    @Inject(method = "trigger", at = @At("HEAD"))
    private void trigger(ServerPlayer player, Predicate<T> testTrigger, CallbackInfo ci) {
        Hercules.grantCriteria(player, player.server.registryAccess().registryOrThrow(Hercules.QUEST_CONDITION_REGISTRY_KEY)
                .stream()
                .flatMap(condition -> condition.allCriteria()
                        .filter(criterion -> getId().equals(Objects.requireNonNull(criterion.getTrigger()).getCriterion()))
                        .filter(criterion -> testTrigger.test((T) criterion.getTrigger()))
                )
        );
    }
}
