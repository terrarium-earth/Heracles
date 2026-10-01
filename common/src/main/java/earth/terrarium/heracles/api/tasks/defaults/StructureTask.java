package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.tasks.CacheableQuestTaskType;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import earth.terrarium.heracles.common.utils.RegistryValue;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public record StructureTask(
    String id, String title, QuestIcon<?> icon, RegistryValue<Structure> structures, boolean accurate
) implements QuestTask<BooleanObjectPair<Collection<Structure>>, NumericTag, StructureTask>, CustomizableQuestElement {

    public static final CacheableQuestTaskType<StructureTask, Cache> TYPE = new Type();

    @Override
    public NumericTag test(QuestTaskType<?> type, NumericTag progress, BooleanObjectPair<Collection<Structure>> input) {
        final Collection<Structure> structures = input.right();
        final boolean accurate = input.leftBoolean();
        if (this.accurate && !accurate) return progress;

        final RegistryAccess access = Heracles.getRegistryAccess();
        final Registry<Structure> registry = access.registry(Registries.STRUCTURE).orElse(null);
        if (registry != null) {
            for (Structure structure : structures) {
                if (structures().is(registry.wrapAsHolder(structure))) {
                    return storage().of(progress, true);
                }
            }
        }
        return storage().of(progress, false);
    }

    @Override
    public float getProgress(NumericTag progress) {
        return storage().readBoolean(progress) ? 1.0F : 0.0F;
    }

    @Override
    public BooleanTaskStorage storage() {
        return BooleanTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<StructureTask> type() {
        return TYPE;
    }

    private static class Type implements CacheableQuestTaskType<StructureTask, Cache> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "structure");
        }

        @Override
        public Codec<StructureTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.optionalFieldOf("title", "").forGetter(StructureTask::title),
                QuestIcons.CODEC.optionalFieldOf("icon", ItemQuestIcon.AIR).forGetter(StructureTask::icon),
                RegistryValue.codec(Registries.STRUCTURE).fieldOf("structures").forGetter(StructureTask::structures),
                Codec.BOOL.optionalFieldOf("accurate", false).forGetter(StructureTask::accurate)
            ).apply(instance, StructureTask::new));
        }

        @Override
        public Cache cache(Collection<Quest> quests) {
            final RegistryAccess access = Heracles.getRegistryAccess();
            final Registry<Structure> registry = access.registry(Registries.STRUCTURE).orElse(null);
            if (registry == null) return null;
            final Cache cache = new Cache(new HashSet<>(), new HashSet<>());
            for (Quest quest : quests) {
                for (QuestTask<?, ?, ?> task : quest.tasks().values()) {
                    if (task instanceof StructureTask structureTask) {
                        var structures = structureTask.accurate() ? cache.accurate() : cache.inaccurate();
                        structureTask.structures.value()
                            .ifLeft(structures::add)
                            .ifRight(key -> registry.getTag(key).ifPresent(tag -> tag.forEach(structures::add)));
                    }
                }
            }
            return cache;
        }
    }

    public record Cache(Set<Holder<Structure>> accurate, Set<Holder<Structure>> inaccurate) {
    }
}
