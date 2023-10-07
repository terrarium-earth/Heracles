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
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;

public record BiomeTask(
    String id, String title, QuestIcon<?> icon, RegistryValue<Biome> biomes
) implements QuestTask<Holder<Biome>, ByteTag, BiomeTask>, CustomizableQuestElement {

    public static final QuestTaskType<BiomeTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, Holder<Biome> input) {
        return storage().of(progress, biomes.is(input));
    }

    @Override
    public float getProgress(ByteTag progress) {
        return storage().readBoolean(progress) ? 1.0F : 0.0F;
    }

    @Override
    public BooleanTaskStorage storage() {
        return BooleanTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<BiomeTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<BiomeTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "biome");
        }

        @Override
        public Codec<BiomeTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.fieldOf("title").orElse("").forGetter(BiomeTask::title),
                QuestIcons.CODEC.fieldOf("icon").orElse(new ItemQuestIcon(Items.AIR)).forGetter(BiomeTask::icon),
                RegistryValue.codec(Registries.BIOME).fieldOf("biomes").forGetter(BiomeTask::biomes)
            ).apply(instance, BiomeTask::new));
        }
    }
}
