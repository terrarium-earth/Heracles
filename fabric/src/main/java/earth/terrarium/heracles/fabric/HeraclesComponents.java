package earth.terrarium.heracles.fabric;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import earth.terrarium.heracles.condition.PlayerAcquiredCriteria;

public class HeraclesComponents implements EntityComponentInitializer {
    public static final ComponentKey<PlayerAcquiredCriteriaComponent> ACQUIRED_CRITERIA_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(PlayerAcquiredCriteria.KEY, PlayerAcquiredCriteriaComponent.class);

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(ACQUIRED_CRITERIA_KEY, player -> new PlayerAcquiredCriteriaComponent());
    }
}
