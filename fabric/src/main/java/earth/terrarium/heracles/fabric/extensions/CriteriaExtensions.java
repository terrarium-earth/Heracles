package earth.terrarium.heracles.fabric.extensions;

import earth.terrarium.heracles.condition.Criteria;
import earth.terrarium.heracles.condition.PlayerAcquiredCriteria;
import earth.terrarium.heracles.fabric.HeraclesComponents;
import net.minecraft.server.level.ServerPlayer;
import net.msrandom.extensions.annotations.ClassExtension;
import net.msrandom.extensions.annotations.ImplementsBaseElement;

@ClassExtension(Criteria.class)
public class CriteriaExtensions {
    @ImplementsBaseElement
    public static PlayerAcquiredCriteria getAcquiredCriteria(ServerPlayer player) {
        return HeraclesComponents.ACQUIRED_CRITERIA_KEY.get(player);
    }
}
