package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Collection;

public record StructureTask(
    String id, String title, QuestIcon<?> icon, RegistryValue<Structure> structures
) implements QuestTask<Collection<Structure>, NumericTag, StructureTask>, CustomizableQuestElement {

    public static final QuestTaskType<StructureTask> TYPE = new Type();

    @Override
    public NumericTag test(QuestTaskType<?> type, NumericTag progress, Collection<Structure> input) {
        final RegistryAccess access = Heracles.getRegistryAccess();
        final Registry<Structure> registry = access.registry(Registries.STRUCTURE).orElse(null);
        if (registry != null) {
            for (Structure structure : input) {
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

    private static class Type implements QuestTaskType<StructureTask> {

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
                RegistryValue.codec(Registries.STRUCTURE).fieldOf("structures").forGetter(StructureTask::structures)
            ).apply(instance, StructureTask::new));
        }
    }
}
