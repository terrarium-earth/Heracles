package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.client.ui.modals.EditRewardsMapModal;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;

public record RewardsMapSetting() implements Setting<Map<String, QuestReward<?>>, Button> {

    public static final RewardsMapSetting INSTANCE = new RewardsMapSetting();

    private static final WeakHashMap<Button, AtomicReference<Map<String, QuestReward<?>>>> DATA = new WeakHashMap<>();

    @Override
    public Button createWidget(@Nullable Button old, int width, Map<String, QuestReward<?>> value) {
        AtomicReference<Map<String, QuestReward<?>>> ref;
        if (old != null && DATA.containsKey(old)) {
            ref = DATA.get(old);
        } else {
            ref = new AtomicReference<>(new LinkedHashMap<>(value));
        }

        Button button = Widgets.button(btn -> {
            btn.withSize(width, 24);
            btn.withRenderer(WidgetRenderers.text(Component.literal("Edit (" + ref.get().size() + " rewards)")));
            btn.withCallback(() -> {
                Minecraft.getInstance().tell(() -> {
                    EditRewardsMapModal.open(ref.get(), updated -> {
                        ref.set(new LinkedHashMap<>(updated));
                    });
                });
            });
        });

        DATA.put(button, ref);
        return button;
    }

    @Override
    public Map<String, QuestReward<?>> getValue(Button widget) {
        AtomicReference<Map<String, QuestReward<?>>> ref = DATA.get(widget);
        return ref != null ? ref.get() : new LinkedHashMap<>();
    }
}
