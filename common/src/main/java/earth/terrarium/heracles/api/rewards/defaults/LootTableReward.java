package earth.terrarium.heracles.api.rewards.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record LootTableReward(
    String id, String title, QuestIcon<?> icon, ResourceLocation lootTable
) implements QuestReward<LootTableReward>, CustomizableQuestElement {

    public static final QuestRewardType<LootTableReward> TYPE = new Type();

    @Override
    public Stream<ItemStack> reward(ServerPlayer player) {
        LootParams lootParams = new LootParams.Builder(player.serverLevel())
            .withParameter(LootContextParams.ORIGIN, player.position())
            .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
            .create(LootContextParamSets.CHEST);
        LootTable table = player.server.getLootData().getLootTable(lootTable);
        if (table == LootTable.EMPTY) {
            player.sendSystemMessage(Component.translatable("gui.heracles.error.loot.not_found", lootTable));
            return Stream.empty();
        }
        List<ItemStack> rewards = new ArrayList<>();
        table.getRandomItems(lootParams, item -> mergeItemStacks(rewards, item.copy()));
        for (ItemStack reward : rewards) {
            if (player.addItem(reward.copy())) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            } else {
                ItemEntity itemEntity = player.drop(reward.copy(), false);
                if (itemEntity != null) {
                    itemEntity.setNoPickUpDelay();
                    itemEntity.setTarget(player.getUUID());
                }
            }
        }
        return rewards.stream();
    }

    public static void mergeItemStacks(List<ItemStack> items, ItemStack stack) {
        for (ItemStack item : items) {
            if (ItemStack.isSameItemSameTags(item, stack)) {
                item.grow(stack.getCount());
                return;
            }
        }
        items.add(stack);
    }

    @Override
    public QuestRewardType<LootTableReward> type() {
        return TYPE;
    }

    private static class Type implements QuestRewardType<LootTableReward> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "loottable");
        }

        @Override
        public Codec<LootTableReward> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.fieldOf("title").orElse("").forGetter(LootTableReward::title),
                QuestIcons.CODEC.fieldOf("icon").orElse(new ItemQuestIcon(Items.AIR)).forGetter(LootTableReward::icon),
                ResourceLocation.CODEC.fieldOf("loot_table").forGetter(LootTableReward::lootTable)
            ).apply(instance, LootTableReward::new));
        }
    }
}
