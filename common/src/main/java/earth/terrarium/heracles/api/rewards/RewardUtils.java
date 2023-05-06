package earth.terrarium.heracles.api.rewards;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public final class RewardUtils {

    public static void giveItem(ServerPlayer player, ItemStack stack) {
        if (player.addItem(stack)) {
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
        } else {
            ItemEntity itemEntity = player.drop(stack, false);
            if (itemEntity != null) {
                itemEntity.setNoPickUpDelay();
                itemEntity.setTarget(player.getUUID());
            }
        }
    }
}
