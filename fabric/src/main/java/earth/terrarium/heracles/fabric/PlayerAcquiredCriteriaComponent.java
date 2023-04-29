package earth.terrarium.heracles.fabric;

import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import earth.terrarium.heracles.condition.PlayerAcquiredCriteria;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

@SuppressWarnings("UnstableApiUsage")
public class PlayerAcquiredCriteriaComponent extends PlayerAcquiredCriteria implements PlayerComponent<PlayerAcquiredCriteriaComponent> {
    @Override
    public void readFromNbt(CompoundTag tag) {
        load(tag.getList("Criteria", Tag.TAG_STRING));
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.put("Criteria", save());
    }
}
