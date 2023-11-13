package earth.terrarium.heracles.api.client.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.ApiStatus;

public record Theme(
    QuestsScreenTheme questsScreen,
    QuestScreenTheme questScreen,
    ToastsTheme toasts,
    PinnedQuestsTheme pinnedQuests,
    ModalsTheme modals,
    EditorTheme editor,
    GenericTheme generic
) {

    public static final Codec<Theme> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        QuestsScreenTheme.CODEC.fieldOf("quests_screen").orElse(QuestsScreenTheme.DEFAULT).forGetter(Theme::questsScreen),
        QuestScreenTheme.CODEC.fieldOf("quest_screen").orElse(QuestScreenTheme.DEFAULT).forGetter(Theme::questScreen),
        ToastsTheme.CODEC.fieldOf("toasts").orElse(ToastsTheme.DEFAULT).forGetter(Theme::toasts),
        PinnedQuestsTheme.CODEC.fieldOf("pinned_quests").orElse(PinnedQuestsTheme.DEFAULT).forGetter(Theme::pinnedQuests),
        ModalsTheme.CODEC.fieldOf("modals").orElse(ModalsTheme.DEFAULT).forGetter(Theme::modals),
        EditorTheme.CODEC.fieldOf("editor").orElse(EditorTheme.DEFAULT).forGetter(Theme::editor),
        GenericTheme.CODEC.fieldOf("generic").orElse(GenericTheme.DEFAULT).forGetter(Theme::generic)
    ).apply(instance, Theme::new));

    public static final Theme DEFAULT = new Theme(
        QuestsScreenTheme.DEFAULT,
        QuestScreenTheme.DEFAULT,
        ToastsTheme.DEFAULT,
        PinnedQuestsTheme.DEFAULT,
        ModalsTheme.DEFAULT,
        EditorTheme.DEFAULT,
        GenericTheme.DEFAULT
    );

    private static Theme instance = DEFAULT;

    public static Theme getInstance() {
        return instance;
    }

    @ApiStatus.Internal
    public static void setInstance(Theme theme) {
        instance = theme;
    }

}
