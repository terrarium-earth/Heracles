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

public class PlayerAcquiredCriteriaCapability extends PlayerAcquiredCriteria implements ICapabilitySerializable<ListTag> {
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        return capability == CapabilityManager.get(HeraclesForge.ACQUIRED_CRITERIA_CAPABILITY_TOKEN) ?
                LazyOptional.of(() -> this).cast() :
                LazyOptional.empty();
    }

    @Override
    public ListTag serializeNBT() {
        return save();
    }

    @Override
    public void deserializeNBT(ListTag arg) {
        load(arg);
    }
}
