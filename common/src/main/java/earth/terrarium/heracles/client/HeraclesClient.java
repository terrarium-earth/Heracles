package earth.terrarium.heracles.client;

import earth.terrarium.heracles.Quest;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;

import java.util.List;

public class HeraclesClient {
    public static void displayItemsRewardedToast(Quest quest, List<Item> items) {
        QuestCompletedToast.addOrUpdate(Minecraft.getInstance().getToasts(), quest, items);
    }
}
