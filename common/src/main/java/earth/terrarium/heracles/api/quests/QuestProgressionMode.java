package earth.terrarium.heracles.api.quests;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Specifies when a player can make progress on a quest.
 * <p>
 * This is inspired by <a href="https://docs.feed-the-beast.com/docs/mods/suite/Quests/Developer/Quests/Settings#progression-mode">a similar setting in FTB Quests</a>.
 */
public enum QuestProgressionMode implements StringRepresentable {
    /**
     * Players can make progress on this quest’s tasks only if the quest is unlocked.
     */
    LINEAR,
    /**
     * Players can make progress on this quest’s tasks at any time
     * but can claim the rewards only once it is unlocked.
     */
    FLEXIBLE;

    @Override
    public @NotNull String getSerializedName() {
        return "quest.heracles.%s".formatted(name().toLowerCase(Locale.ROOT));
    }
}
