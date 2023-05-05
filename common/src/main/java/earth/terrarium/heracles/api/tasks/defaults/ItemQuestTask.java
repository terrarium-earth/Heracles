package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public record ItemQuestTask(
    String id, Set<Item> item, NbtPredicate nbt, int target
) implements QuestTask<ItemStack, ItemQuestTask> {

    public static final QuestTaskType<ItemQuestTask> TYPE = new Type();

    @Override
    public int test(ItemStack input) {
        if (item.contains(input.getItem()) && nbt.matches(input) && input.getCount() >= target()) {
            input.shrink(target());
            return target();
        }
        return 0;
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
                CodecExtras.set(BuiltInRegistries.ITEM.byNameCodec()).fieldOf("item").forGetter(ItemQuestTask::item),
                NbtPredicate.CODEC.fieldOf("nbt").orElse(NbtPredicate.ANY).forGetter(ItemQuestTask::nbt),
                Codec.INT.fieldOf("amount").orElse(1).forGetter(ItemQuestTask::target)
            ).apply(instance, ItemQuestTask::new));
        }
    }
}
