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
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record GatherItemTask(
    String id, RegistryValue<Item> item, NbtPredicate nbt, int target, boolean manual
) implements QuestTask<Pair<ItemStack, Container>, NumericTag, GatherItemTask> {

    public static final QuestTaskType<GatherItemTask> TYPE = new Type();

    @Override
    public NumericTag test(QuestTaskType<?> type, NumericTag progress, Pair<ItemStack, Container> input) {
        ItemStack stack = input.getFirst();
        Container container = input.getSecond();
        if (manual) {
            return manual(progress, container);
        }
        if (this.item.is(stack.getItemHolder()) && nbt.matches(stack)) {
            return automatic(progress, container);
        }
        return progress;
    }

    private NumericTag automatic(NumericTag progress, Container container) {
        List<ItemStack> list = new ArrayList<>();
        int amount = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (this.item.is(itemStack.getItemHolder()) && nbt.matches(itemStack)) {
                amount += itemStack.getCount();
                list.add(itemStack);
            }
        }
        if (amount >= target()) {
            int shrink = target() - storage().readInt(progress);
            for (ItemStack itemStack : list) {
                if (shrink <= 0) break;
                int amountShrank = Math.min(itemStack.getCount(), shrink);
                itemStack.shrink(amountShrank);
                shrink -= amountShrank;
            }
            return storage().set(target());
        }
        return progress;
    }

    private NumericTag manual(NumericTag progress, Container input) {
        final int currentAmount = storage().readInt(progress);
        final int amountNeeded = target() - currentAmount;
        int shrink = amountNeeded;
        if (shrink <= 0) return progress;
        int amountFound = 0;
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack itemStack = input.getItem(i);
            if (this.item.is(itemStack.getItemHolder()) && nbt.matches(itemStack)) {
                amountFound += itemStack.getCount();
                list.add(itemStack);
                if (amountFound >= shrink) break;
            }
        }
        if (amountFound > 0) {
            for (ItemStack itemStack : list) {
                if (shrink <= 0) break;
                int amountShrank = Math.min(itemStack.getCount(), shrink);
                itemStack.shrink(amountShrank);
                shrink -= amountShrank;
            }
            return storage().add(progress, Math.min(amountFound, amountNeeded));
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
                Codec.INT.fieldOf("amount").orElse(1).forGetter(GatherItemTask::target),
                Codec.BOOL.fieldOf("manual").orElse(false).forGetter(GatherItemTask::manual)
            ).apply(instance, GatherItemTask::new));
        }
    }
}
