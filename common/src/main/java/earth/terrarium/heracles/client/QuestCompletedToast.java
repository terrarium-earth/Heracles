package earth.terrarium.heracles.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.Quest;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuestCompletedToast implements Toast {
    private static final long DISPLAY_TIME = 5000L;
    private static final Component TITLE_TEXT = Component.translatable("rewards." + Heracles.MOD_ID + ".toast");
    private final Map<Quest, Set<Item>> questItems = new LinkedHashMap<>();
    private List<Pair<Quest, ItemStack>> renderItems;
    private long lastChanged;

    public QuestCompletedToast(Quest quest, List<Item> items) {
        questItems.put(quest, new LinkedHashSet<>(items));
    }

    @Override
    @NotNull
    public Toast.Visibility render(PoseStack poseStack, ToastComponent toastComponent, long timeSinceLastVisible) {
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

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            GuiComponent.blit(poseStack, 0, 0, 0, 32, width(), height());
            toastComponent.getMinecraft().font.draw(poseStack, TITLE_TEXT, 30.0F, 7.0F, 0xFF500050);
            toastComponent.getMinecraft().font.draw(poseStack, entry.getFirst().rewardText(), 30.0F, 18.0F, 0xFF000000);

            toastComponent.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(new PoseStack(), entry.getSecond(), 8, 8);

            return timeSinceLastVisible - lastChanged >= DISPLAY_TIME * questItems.size() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }
    }

    public static void addOrUpdate(ToastComponent toastComponent, Quest quest, List<Item> items) {
        QuestCompletedToast questCompletedToast = toastComponent.getToast(QuestCompletedToast.class, NO_TOKEN);
        if (questCompletedToast == null) {
            toastComponent.addToast(new QuestCompletedToast(quest, items));
        } else {
            Set<Item> itemSet = questCompletedToast.questItems.get(quest);
            if (itemSet == null) {
                questCompletedToast.questItems.put(quest, new LinkedHashSet<>(items));
            } else {
                itemSet.addAll(items);
            }

            questCompletedToast.renderItems = null;
        }
    }
}
