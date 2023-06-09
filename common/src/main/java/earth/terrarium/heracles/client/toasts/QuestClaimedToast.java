package earth.terrarium.heracles.client.toasts;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class QuestClaimedToast implements Toast {
    private static final long DISPLAY_TIME = 5000L;
    private static final Component TITLE_TEXT = Component.translatable("rewards." + Heracles.MOD_ID + ".toast");
    private final Map<Quest, Set<Item>> questItems = new LinkedHashMap<>();
    private List<Pair<Quest, ItemStack>> renderItems;
    private long lastChanged;

    public QuestClaimedToast(Quest quest, List<Item> items) {
        questItems.put(quest, new LinkedHashSet<>(items));
    }

    @Override
    @NotNull
    public Toast.Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        if (renderItems == null) {
            lastChanged = timeSinceLastVisible;

            renderItems = new ArrayList<>();

            List<Pair<Quest, Iterator<Item>>> iterators = new ArrayList<>();
            for (Map.Entry<Quest, Set<Item>> entry : questItems.entrySet()) {
                iterators.add(new Pair<>(entry.getKey(), entry.getValue().iterator()));
            }

            while (true) {
                // Create alternating display between quest items
                boolean done = true;
                for (Pair<Quest, Iterator<Item>> pair : iterators) {
                    if (pair.getSecond().hasNext()) {
                        done = false;
                        renderItems.add(new Pair<>(pair.getFirst(), pair.getSecond().next().getDefaultInstance()));
                    }
                }

                if (done) {
                    break;
                }
            }
        }

        if (questItems.isEmpty()) {
            return Toast.Visibility.HIDE;
        } else {
            Pair<Quest, ItemStack> entry = renderItems.get((int) (timeSinceLastVisible / Math.max(1L, (DISPLAY_TIME * questItems.size()) / renderItems.size()) % renderItems.size()));

            graphics.blit(TEXTURE, 0, 0, 0, 0, width(), height());
            graphics.drawString(
                toastComponent.getMinecraft().font,
                TITLE_TEXT, 30, 7, 0xFF800080,
                false
            );

            graphics.renderFakeItem(entry.getSecond(), 8, 8);

            return timeSinceLastVisible - lastChanged >= DISPLAY_TIME * questItems.size() * toastComponent.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }
    }

    public static void addOrUpdate(ToastComponent toastComponent, String id, List<Item> items) {
        ClientQuests.get(id).ifPresent(entry -> {
            Quest quest = entry.value();
            QuestClaimedToast questClaimedToast = toastComponent.getToast(QuestClaimedToast.class, NO_TOKEN);
            if (questClaimedToast == null) {
                toastComponent.addToast(new QuestClaimedToast(quest, items));
            } else {
                Set<Item> itemSet = questClaimedToast.questItems.get(quest);
                if (itemSet == null) {
                    questClaimedToast.questItems.put(quest, new LinkedHashSet<>(items));
                } else {
                    itemSet.addAll(items);
                }

                questClaimedToast.renderItems = null;
            }
        });
    }
}
