package earth.terrarium.heracles.client.ui.quests;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.QuestIconSetting;
import earth.terrarium.heracles.api.client.settings.base.TextSetting;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplay;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class QuestDetailsInitializer implements SettingInitializer<QuestDetailsInitializer.Details> {

    public static final QuestDetailsInitializer INSTANCE = new QuestDetailsInitializer();

    @Override
    public CreationData create(@Nullable Details object) {
        object = Objects.requireNonNullElse(object, Details.DEFAULT);
        CreationData settings = new CreationData();
        settings.put("icon", QuestIconSetting.INSTANCE, object.icon instanceof ItemQuestIcon icon ? icon : new ItemQuestIcon(Items.MAP));
        settings.put("title", TextSetting.INSTANCE, Details.asString(object.title));
        settings.put("subtitle", TextSetting.INSTANCE, Details.asString(object.subtitle));
        return settings;
    }

    @Override
    public Details create(String id, Details object, Data data) {
        Details details = Objects.requireNonNullElse(object, Details.DEFAULT);
        Optional<QuestIcon<?>> icon = data.get("icon", QuestIconSetting.INSTANCE).map(Function.identity());
        return new Details(
            icon.orElse(details.icon),
            object.iconBackground,
            data.get("title", TextSetting.INSTANCE).map(Component::translatable).orElse(details.title.copy()),
            data.get("subtitle", TextSetting.INSTANCE).map(Component::translatable).orElse(details.subtitle.copy())
        );
    }

    public record Details(QuestIcon<?> icon, ResourceLocation iconBackground, Component title, Component subtitle) {

        public static final Details DEFAULT = new Details(
            new ItemQuestIcon(Items.MAP),
            QuestDisplay.DEFAULT_BACKGROUND,
            Component.literal("New Quest"),
            Component.empty()
        );

        public Details(Quest quest) {
            this(quest.display().icon(), quest.display().iconBackground(), quest.display().title(), quest.display().subtitle());
        }

        public static String asString(Component component) {
            if (component.getContents() instanceof LiteralContents contents) {
                return contents.text().replace("\n", " ");
            } else if (component.getContents() instanceof TranslatableContents contents) {
                return contents.getKey().replace("\n", " ");
            }
            return component.getString();
        }

        public NetworkQuestData.Builder build(NetworkQuestData.Builder builder) {
            return builder
                .icon(this.icon)
                .background(this.iconBackground)
                .title(this.title)
                .subtitle(this.subtitle);
        }
    }
}
