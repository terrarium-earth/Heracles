package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import earth.terrarium.heracles.mixins.common.PlayerAdvancementAccessor;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public record AdvancementTask(
    String id, String title, QuestIcon<?> icon, Set<ResourceLocation> advancements
) implements QuestTask<AdvancementHolder, NumericTag, AdvancementTask>, CustomizableQuestElement {

    public static final QuestTaskType<AdvancementTask> TYPE = new Type();

    @Override
    public NumericTag test(QuestTaskType<?> type, NumericTag progress, AdvancementHolder input) {
        return storage().of(progress, advancements.contains(input.id()));
    }

    @Override
    public NumericTag init(QuestTaskType<?> type, NumericTag progress, ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return progress;
        ServerAdvancementManager manager = server.getAdvancements();
        for (ResourceLocation id : advancements) {
            AdvancementHolder advancement = manager.get(id);
            if (advancement == null) continue;
            PlayerAdvancementAccessor advancements = (PlayerAdvancementAccessor) player.getAdvancements();
            AdvancementProgress advancementProgress = advancements.progress().get(advancement.value());
            if (advancementProgress == null) continue;
            if (!advancementProgress.isDone()) continue;
            progress = test(type, progress, advancement);
        }
        return progress;
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
    public QuestTaskType<AdvancementTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<AdvancementTask> {

        @Override
        public ResourceLocation id() {
            return Heracles.id("advancement");
        }

        @Override
        public MapCodec<AdvancementTask> codec(String id) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.lenientOptionalFieldOf("title", "").forGetter(AdvancementTask::title),
                QuestIcons.CODEC.lenientOptionalFieldOf("icon", ItemQuestIcon.AIR).forGetter(AdvancementTask::icon),
                CodecExtras.set(ResourceLocation.CODEC).fieldOf("advancements").forGetter(AdvancementTask::advancements)
            ).apply(instance, AdvancementTask::new));
        }
    }
}
