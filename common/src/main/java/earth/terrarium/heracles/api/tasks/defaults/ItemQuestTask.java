package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.IntegerTaskStorage;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ItemQuestTask(
    String id, HolderSet<Item> item, NbtPredicate nbt, int target
) implements QuestTask<ItemStack, ItemQuestTask> {

    public static final QuestTaskType<ItemQuestTask> TYPE = new Type();

    @Override
    public Tag test(Tag progress, ItemStack input) {
        if (input.is(item::contains) && nbt.matches(input) && input.getCount() >= target()) {
            input.shrink(target());
            return storage().of(progress, target());
        }
        return progress;
    }

    @Override
    public float getProgress(Tag progress) {
        return storage().readInt(progress) / (float) target();
    }

    @Override
    public IntegerTaskStorage storage() {
        return IntegerTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<ItemQuestTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<ItemQuestTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "item");
        }

        @Override
        public Codec<ItemQuestTask> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(ItemQuestTask::id),
                RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("item").forGetter(ItemQuestTask::item),
                NbtPredicate.CODEC.fieldOf("nbt").orElse(NbtPredicate.ANY).forGetter(ItemQuestTask::nbt),
                Codec.INT.fieldOf("amount").orElse(1).forGetter(ItemQuestTask::target)
            ).apply(instance, ItemQuestTask::new));
        }
    }
}
