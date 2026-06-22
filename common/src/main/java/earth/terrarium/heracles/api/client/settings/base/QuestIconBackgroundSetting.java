package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

public record QuestIconBackgroundSetting() implements Setting<ResourceLocation, Button> {
    public static final QuestIconBackgroundSetting INSTANCE = new QuestIconBackgroundSetting();

    private static final Map<ResourceLocation, Component> BACKGROUNDS = new LinkedHashMap<>();
    private static final WeakHashMap<Button, DropdownState<ResourceLocation>> DATA = new WeakHashMap<>();

    static {
        add("default");
        add("circles");
        add("diamonds");
        add("gears");
        add("hearts");
        add("hexagons");
        add("octagons");
        add("pentagons");
        add("rounded_squares");
    }

    private static void add(String name) {
        BACKGROUNDS.put(
            Heracles.id("textures/gui/quest_backgrounds/" + name + ".png"),
            Component.translatable("gui.heracles.quest_background." + name)
        );
    }

    @Override
    public Button createWidget(Button old, int width, ResourceLocation value) {
        DropdownState<ResourceLocation> state;
        if (old != null && DATA.containsKey(old)) {
            state = DATA.get(old);
        } else {
            state = DropdownState.of(value);
        }
        Button button = Widgets.dropdown(
            state,
            new ArrayList<>(BACKGROUNDS.keySet()),
            BACKGROUNDS::get,
            btn -> btn.withSize(width, 24),
            dropdown -> {}
        );
        DATA.put(button, state);
        return button;
    }

    @Override
    public ResourceLocation getValue(Button widget) {
        DropdownState<ResourceLocation> state = DATA.get(widget);
        return state != null ? state.get() : null;
    }
}
