package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.IntegerTaskStorage;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record GatherItemTask(
    String id, RegistryValue<Item> item, NbtPredicate nbt, int target
) implements QuestTask<Pair<ItemStack, Inventory>, NumericTag, GatherItemTask> {

    public static final QuestTaskType<GatherItemTask> TYPE = new Type();

    @Override
    public NumericTag test(QuestTaskType<?> type, NumericTag progress, Pair<ItemStack, Inventory> input) {
        ItemStack stack = input.getFirst();
        if (this.item.is(stack.getItemHolder()) && nbt.matches(stack)) {
            Inventory inventory = input.getSecond();
            List<ItemStack> list = new ArrayList<>();
            int amount = 0;
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack itemStack = inventory.getItem(i);
                if (this.item.is(itemStack.getItemHolder()) && nbt.matches(itemStack)) {
                    amount += itemStack.getCount();
                    list.add(itemStack);
                }
            }
            if (amount >= target()) {
                int shrink = target();
                for (ItemStack itemStack : list) {
                    if (shrink <= 0) break;
                    int amountShrank = Math.min(itemStack.getCount(), shrink);
                    itemStack.shrink(amountShrank);
                    shrink -= amountShrank;
                }
                return storage().of(progress, target());
            }
        }
        return progress;
    }

    @Override
    public float getProgress(NumericTag progress) {
        return storage().readInt(progress) / (float) target();
    }

    @Override
    public IntegerTaskStorage storage() {
        return IntegerTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<GatherItemTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<GatherItemTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "item");
        }

        @Override
        public Codec<GatherItemTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                RegistryValue.codec(Registries.ITEM).fieldOf("item").forGetter(GatherItemTask::item),
                NbtPredicate.CODEC.fieldOf("nbt").orElse(NbtPredicate.ANY).forGetter(GatherItemTask::nbt),
                Codec.INT.fieldOf("amount").orElse(1).forGetter(GatherItemTask::target)
            ).apply(instance, GatherItemTask::new));
        }
    }
}
