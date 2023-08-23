package earth.terrarium.heracles.api.tasks.client.display;

import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.defaults.*;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class TaskTitleFormatters {

    public static void init() {
        TaskTitleFormatter.register(KillEntityQuestTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, task.target() == 1), task.entity().entityType().getDescription()));
        TaskTitleFormatter.register(GatherItemTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, task.target() == 1), task.item().getDisplayName(Item::getDescription)));
        TaskTitleFormatter.register(AdvancementTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, task.advancements().size() == 1)));
        TaskTitleFormatter.register(ChangedDimensionTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, true)));
        TaskTitleFormatter.register(RecipeTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, task.recipes().size() == 1), Optionull.firstOrDefault(task.titles(), CommonComponents.EMPTY)));
        TaskTitleFormatter.register(StructureTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, true), task.structures().getDisplayName((id, structure) -> Component.translatableWithFallback(Util.makeDescriptionId("structure", id), id.toString()))));
        TaskTitleFormatter.register(BiomeTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, true), task.biomes().getDisplayName((id, structure) -> Component.translatableWithFallback(Util.makeDescriptionId("biome", id), id.toString()))));
        TaskTitleFormatter.register(BlockInteractTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, true), task.block().getDisplayName(Block::getName)));
        TaskTitleFormatter.register(ItemInteractTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, true), task.item().getDisplayName(Item::getDescription)));
        TaskTitleFormatter.register(EntityInteractTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, true), task.entity().getDisplayName(EntityType::getDescription)));
        TaskTitleFormatter.register(CheckTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, true)));
        TaskTitleFormatter.register(DummyTask.TYPE, (task) -> task.title() == null ? CommonComponents.EMPTY : task.title());
        TaskTitleFormatter.register(XpTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, true)));
        TaskTitleFormatter.register(LocationTask.TYPE, LocationTask::title);
        TaskTitleFormatter.register(StatTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, true), Component.translatable(task.stat().toLanguageKey("stat")), task.target()));
        TaskTitleFormatter.register(CompositeTask.TYPE, (task) -> {
            List<Component> titles = new ArrayList<>();
            titles.add(Component.translatable(toTranslationKey(task, task.amount() == 1), task.amount()));
            task.tasks().values().stream().map(TaskTitleFormatter::create).forEach(titles::add);
            return CommonComponents.joinLines(titles);
        });
    }

    public static String toTranslationKey(QuestTask<?, ?, ?> task, boolean singular) {
        ResourceLocation id = task.type().id();
        return "task." + id.getNamespace() + "." + id.getPath() + ".title" + (singular ? ".singular" : ".plural");
    }
}
