package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.tasks.CollectionType;
import earth.terrarium.heracles.api.tasks.PairQuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.IntegerTaskStorage;
import earth.terrarium.heracles.common.utils.RegistryValue;
import earth.terrarium.heracles.common.utils.XorMapCodec;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record GatherItemTask(
    String id, String title, QuestIcon<?> icon, RegistryValue<Item> item, DataComponentPredicate components, int target, CollectionType collectionType
) implements PairQuestTask<Optional<ItemStack>, Container, NumericTag, GatherItemTask>, CustomizableQuestElement {

    public static final QuestTaskType<GatherItemTask> TYPE = new Type();

    @Override
    public NumericTag test(QuestTaskType<?> type, NumericTag progress, Optional<ItemStack> stack, Container container) {
        if (this.collectionType == CollectionType.MANUAL) {
            if (stack.isEmpty()) {
                return manual(progress, container);
            }
        } else if (stack.isPresent()) {
            if (this.item.is(stack.get().getItemHolder()) && components.test(stack.get())) {
                return automatic(progress, container);
            }
        }
        return progress;
    }

    @Override
    public NumericTag init(QuestTaskType<?> type, NumericTag progress, ServerPlayer player) {
        if (this.collectionType != CollectionType.MANUAL) {
            return automatic(progress, player.getInventory());
        }
        return progress;
    }

    private NumericTag automatic(NumericTag progress, Container container) {
        List<ItemStack> list = new ArrayList<>();
        int amount = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (this.item.is(itemStack.getItemHolder()) && components.test(itemStack)) {
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
        return storage().set(amount);
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
            if (this.item.is(itemStack.getItemHolder()) && components.test(itemStack)) {
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
            return Heracles.id("item");
        }

        @Override
        public MapCodec<GatherItemTask> codec(String id) {
            MapCodec<GatherItemTask> newCodec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.lenientOptionalFieldOf("title", "").forGetter(GatherItemTask::title),
                QuestIcons.CODEC.lenientOptionalFieldOf("icon", ItemQuestIcon.AIR).forGetter(GatherItemTask::icon),
                RegistryValue.codec(Registries.ITEM).fieldOf("item").forGetter(GatherItemTask::item),
                DataComponentPredicate.CODEC.fieldOf("components").orElse(DataComponentPredicate.EMPTY).forGetter(GatherItemTask::components),
                Codec.INT.fieldOf("amount").orElse(1).forGetter(GatherItemTask::target),
                EnumCodec.of(CollectionType.class).fieldOf("collection").orElse(CollectionType.AUTOMATIC).forGetter(GatherItemTask::collectionType)
            ).apply(instance, GatherItemTask::new));

            MapCodec<GatherItemTask> legacy = legacyCodec(id);

            return XorMapCodec.create(newCodec, legacy).xmap(
                either -> either.map(t -> t, t -> t),
                Either::left
            );
        }

        private MapCodec<GatherItemTask> legacyCodec(String id) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                RecordCodecBuilder.point(id),
                RegistryValue.codec(Registries.ITEM).fieldOf("item").forGetter(GatherItemTask::item),
                DataComponentPredicate.CODEC.fieldOf("components").orElse(DataComponentPredicate.EMPTY).forGetter(GatherItemTask::components),
                Codec.INT.fieldOf("amount").orElse(1).forGetter(GatherItemTask::target),
                Codec.BOOL.fieldOf("manual").orElse(false).forGetter(task -> task.collectionType == CollectionType.MANUAL)
            ).apply(instance, (i, item, nbt, amount, manual) -> new GatherItemTask(i, "", new ItemQuestIcon(Items.AIR), item, nbt, amount, manual ? CollectionType.MANUAL : CollectionType.CONSUME)));
        }
    }

}
