package earth.terrarium.hercules.client;

import earth.terrarium.hercules.Quest;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;

import java.util.List;

public class HerculesClient {
    public static void displayItemsRewardedToast(Quest quest, List<Item> items) {
        QuestCompletedToast.addOrUpdate(Minecraft.getInstance().getToasts(), quest, items);
    }
}
