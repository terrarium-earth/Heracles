package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record ItemInteractTask(
    String id, String title, QuestIcon<?> icon, RegistryValue<Item> item, NbtPredicate nbt
) implements QuestTask<ItemStack, ByteTag, ItemInteractTask>, CustomizableQuestElement {
    public static final QuestTaskType<ItemInteractTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, ItemStack input) {
        return storage().of(progress, item.is(input.getItemHolder()) && nbt().matches(input));
    }

    @Override
    public float getProgress(ByteTag progress) {
        return storage().readBoolean(progress) ? 1 : 0;
    }

    @Override
    public BooleanTaskStorage storage() {
        return BooleanTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<ItemInteractTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<ItemInteractTask> {
        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "item_interaction");
        }

        @Override
        public Codec<ItemInteractTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.fieldOf("title").orElse("").forGetter(ItemInteractTask::title),
                QuestIcons.CODEC.fieldOf("icon").orElse(new ItemQuestIcon(Items.AIR)).forGetter(ItemInteractTask::icon),
                RegistryValue.codec(Registries.ITEM).fieldOf("item").forGetter(ItemInteractTask::item),
                NbtPredicate.CODEC.fieldOf("nbt").orElse(NbtPredicate.ANY).forGetter(ItemInteractTask::nbt)
            ).apply(instance, ItemInteractTask::new));
        }
    }
}
