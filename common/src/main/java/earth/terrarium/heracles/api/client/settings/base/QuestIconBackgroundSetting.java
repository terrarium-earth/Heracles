package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.components.widgets.dropdown.Dropdown;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public record QuestIconBackgroundSetting() implements Setting<ResourceLocation, Dropdown<ResourceLocation>> {
    public static final QuestIconBackgroundSetting INSTANCE = new QuestIconBackgroundSetting();

    private static final Map<ResourceLocation, Component> BACKGROUNDS = new LinkedHashMap<>();

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
    public Dropdown<ResourceLocation> createWidget(Dropdown<ResourceLocation> old, int width, ResourceLocation value) {
        return new Dropdown<>(old, width, 24, BACKGROUNDS, value);
    }

    @Override
    public ResourceLocation getValue(Dropdown<ResourceLocation> widget) {
        return widget.selected();
    }
}