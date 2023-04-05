package earth.terrarium.heracles.mixin;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.resource.QuestConditionManager;
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
    private void heracles$trigger(ServerPlayer player, Predicate<T> testTrigger, CallbackInfo ci) {
        Heracles.grantCriteria(player, QuestConditionManager.getInstance().getConditions().values()
                .stream()
                .flatMap(condition -> condition.allCriteria()
                        .filter(criterion -> getId().equals(Objects.requireNonNull(criterion.getTrigger()).getCriterion()))
                        .filter(criterion -> testTrigger.test((T) criterion.getTrigger()))
                )
        );
    }
}
