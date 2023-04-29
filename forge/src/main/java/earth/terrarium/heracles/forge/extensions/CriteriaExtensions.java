package earth.terrarium.heracles.forge.extensions;

import earth.terrarium.heracles.condition.PlayerAcquiredCriteria;
import earth.terrarium.heracles.condition.Criteria;
import earth.terrarium.heracles.forge.HeraclesForge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.msrandom.extensions.annotations.ClassExtension;
import net.msrandom.extensions.annotations.ImplementsBaseElement;

@ClassExtension(Criteria.class)
public class CriteriaExtensions {
    @ImplementsBaseElement
    public static PlayerAcquiredCriteria getAcquiredCriteria(ServerPlayer player) {
        return player.getCapability(CapabilityManager.get(HeraclesForge.ACQUIRED_CRITERIA_CAPABILITY_TOKEN))
                .resolve()
                .orElseThrow();
    }
}
