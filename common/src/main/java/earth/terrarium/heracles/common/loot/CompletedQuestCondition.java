package earth.terrarium.heracles.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.regisitries.ModLootConditions;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public record CompletedQuestCondition(String quest) implements LootItemCondition {

    public static final MapCodec<CompletedQuestCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("quest").forGetter(CompletedQuestCondition::quest)
    ).apply(instance, CompletedQuestCondition::new));

    @Override
    public @NotNull LootItemConditionType getType() {
        return ModLootConditions.COMPLETED_QUEST.get();
    }

    @Override
    public boolean test(LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity == null) return false;
        if (!(entity instanceof Player player)) return false;
        return QuestProgressHandler.getProgress(context.getLevel().getServer(), player.getUUID()).isComplete(quest);
    }
}
