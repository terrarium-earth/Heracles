package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record LocationTask(
    String id, Item icon, Component title, Component desc, LocationPredicate predicate
) implements QuestTask<ServerPlayer, ByteTag, LocationTask> {

    public static final QuestTaskType<LocationTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, ServerPlayer input) {
        return storage().of(progress, predicate.matches(input.serverLevel(), input.getX(), input.getY(), input.getZ()));
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
    public QuestTaskType<LocationTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<LocationTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "location");
        }

        @Override
        public Codec<LocationTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("icon").orElse(Items.FILLED_MAP).forGetter(LocationTask::icon),
                ExtraCodecs.COMPONENT.fieldOf("title").forGetter(LocationTask::title),
                ExtraCodecs.COMPONENT.fieldOf("description").forGetter(LocationTask::desc),
                CodecExtras.passthrough(LocationPredicate::serializeToJson, net.minecraft.advancements.critereon.LocationPredicate::fromJson).fieldOf("predicate").forGetter(LocationTask::predicate)
            ).apply(instance, LocationTask::new));
        }
    }
}
