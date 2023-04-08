package earth.terrarium.heracles.forge;

import earth.terrarium.heracles.condition.PlayerAcquiredCriteria;
import earth.terrarium.heracles.resource.CriteriaManager;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class PlayerAcquiredCriteriaCapability implements ICapabilitySerializable<ListTag>, PlayerAcquiredCriteria {
    private final Set<Criterion> criteria = new HashSet<>();

    @Override
    public void acquireCriteria(Stream<Criterion> criteria) {
        criteria.forEach(this.criteria::add);
    }

    @Override
    public Stream<Criterion> getAcquiredCriteria() {
        return criteria.stream();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        return capability == CapabilityManager.get(HeraclesForge.ACQUIRED_CRITERIA_CAPABILITY_TOKEN) ?
                LazyOptional.of(() -> this).cast() :
                LazyOptional.empty();
    }

    @Override
    public ListTag serializeNBT() {
        ListTag tag = new ListTag();

        for (Criterion criterion : criteria) {
            ResourceLocation id = CriteriaManager.getInstance().getCriteria().inverse().get(criterion);

            if (id != null) {
                tag.add(StringTag.valueOf(id.toString()));
            }
        }

        return tag;
    }

    @Override
    public void deserializeNBT(ListTag arg) {
        criteria.clear();

        for (int i = 0; i < arg.size(); i++) {
            Criterion criterion = CriteriaManager.getInstance().getCriteria().get(new ResourceLocation(arg.getString(i)));

            if (criterion != null) {
                criteria.add(criterion);
            }
        }
    }
}
