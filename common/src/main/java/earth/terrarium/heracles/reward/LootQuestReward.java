package earth.terrarium.heracles.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record LootQuestReward(List<ResourceLocation> loot) implements QuestReward {
    public static final String KEY = "loot";

    public static final MapCodec<LootQuestReward> MAP_CODEC = ResourceLocation.CODEC.listOf().fieldOf("loot").xmap(LootQuestReward::new, LootQuestReward::loot);

    @Override
    public Stream<Item> reward(ServerPlayer player) {
        LootContext lootContext = new LootContext.Builder(player.getLevel())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withRandom(player.getRandom())
                .create(LootContextParamSets.ADVANCEMENT_REWARD);

        boolean inventoryChanged = false;
        List<Item> itemsRewarded = new ArrayList<>();

        for (ResourceLocation resourceLocation : loot()) {
            for (ItemStack itemStack : player.server.getLootTables().get(resourceLocation).getRandomItems(lootContext)) {
                itemsRewarded.add(itemStack.getItem());

                if (player.addItem(itemStack)) {
                    player.level.playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.ITEM_PICKUP,
                            SoundSource.PLAYERS,
                            0.2F,
                            ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
                    );

                    inventoryChanged = true;
                    continue;
                }

                ItemEntity item = player.drop(itemStack, false);
                if (item != null) {
                    item.setNoPickUpDelay();
                    item.setOwner(player.getUUID());
                }
            }
        }

        if (inventoryChanged) {
            player.containerMenu.broadcastChanges();
        }

        return itemsRewarded.stream();
    }

    @Override
    public Codec<? extends QuestReward> codec() {
        return MAP_CODEC.codec();
    }
}
