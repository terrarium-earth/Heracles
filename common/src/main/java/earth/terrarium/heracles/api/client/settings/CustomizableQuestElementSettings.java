package earth.terrarium.heracles.api.client.settings;

import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.client.settings.base.QuestIconSetting;
import earth.terrarium.heracles.api.client.settings.base.TextSetting;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import net.minecraft.Optionull;
import net.minecraft.world.item.Items;

import java.util.function.BiFunction;

public interface CustomizableQuestElementSettings<T extends CustomizableQuestElement> {
    default SettingInitializer.CreationData create(T questElement) {
        SettingInitializer.CreationData settings = new SettingInitializer.CreationData();
        settings.put("title", TextSetting.INSTANCE, getDefaultTitle(questElement));
        settings.put("icon", QuestIconSetting.INSTANCE, getDefaultIcon(questElement));
        return settings;
    }

    default T create(T questElement, SettingInitializer.Data data, BiFunction<String, QuestIcon<?>, T> generator) {
        return generator.apply(
            data.get("title", TextSetting.INSTANCE).orElse(getDefaultTitle(questElement)),
            data.get("icon", QuestIconSetting.INSTANCE).orElse(getDefaultIcon(questElement))
        );
    }

    default String getDefaultTitle(T questElement) {
        return Optionull.mapOrDefault(questElement, CustomizableQuestElement::title, "");
    }

    default ItemQuestIcon getDefaultIcon(T questElement) {
        return Optionull.mapOrDefault(questElement, obj -> {
            if (obj.icon() instanceof ItemQuestIcon questIcon) return questIcon;
            return new ItemQuestIcon(Items.AIR);
        }, new ItemQuestIcon(Items.AIR));
    }
}
