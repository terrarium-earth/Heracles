package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.PairQuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.IntegerTaskStorage;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record GatherItemTask(
        String id, RegistryValue<Item> item, NbtPredicate nbt, int target, CollectionType collectionType
) implements PairQuestTask<Object, Container, NumericTag, GatherItemTask> {

    public static final QuestTaskType<GatherItemTask> TYPE = new Type();

    @Override
    public NumericTag test(QuestTaskType<?> type, NumericTag progress, Object input, Container container) {
        if (this.collectionType == CollectionType.MANUAL) {
            if (id.equals(input)) {
                return manual(progress, container);
            }
        } else if (input instanceof ItemStack stack && this.item.is(stack.getItemHolder()) && nbt.matches(stack)) {
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
            if (this.collectionType == CollectionType.CONSUME) {
                int shrink = target() - storage().readInt(progress);
                for (ItemStack itemStack : list) {
                    if (shrink <= 0) break;
                    int amountShrank = Math.min(itemStack.getCount(), shrink);
                    itemStack.shrink(amountShrank);
                    shrink -= amountShrank;
                }
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
            Codec<GatherItemTask> newCodec = RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                RegistryValue.codec(Registries.ITEM).fieldOf("item").forGetter(GatherItemTask::item),
                NbtPredicate.CODEC.fieldOf("nbt").orElse(NbtPredicate.ANY).forGetter(GatherItemTask::nbt),
                Codec.INT.fieldOf("amount").orElse(1).forGetter(GatherItemTask::target),
                EnumCodec.of(CollectionType.class).fieldOf("collection").orElse(CollectionType.AUTOMATIC).forGetter(GatherItemTask::collectionType)
            ).apply(instance, GatherItemTask::new));

            return CodecExtras.eitherLeft(Codec.either(newCodec, legacyCodec(id)));
        }

        private Codec<GatherItemTask> legacyCodec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                RegistryValue.codec(Registries.ITEM).fieldOf("item").forGetter(GatherItemTask::item),
                NbtPredicate.CODEC.fieldOf("nbt").orElse(NbtPredicate.ANY).forGetter(GatherItemTask::nbt),
                Codec.INT.fieldOf("amount").orElse(1).forGetter(GatherItemTask::target),
                Codec.BOOL.fieldOf("manual").orElse(false).forGetter(task -> task.collectionType == CollectionType.MANUAL)
            ).apply(instance, (i, item, nbt, amount, manual) -> new GatherItemTask(i, item, nbt, amount, manual ? CollectionType.MANUAL : CollectionType.CONSUME)));
        }
    }

    public enum CollectionType implements StringRepresentable {
        MANUAL,
        AUTOMATIC,
        CONSUME;

        @Override
        public @NotNull String getSerializedName() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }
}
