package earth.terrarium.heracles.api.events;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestEntry;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

/**
 * (Unstable API) A target for event handlers.
 *
 * @apiNote This API comes with no support nor stability guarantees whatsoever. Use at your own risk.
 */
@ApiStatus.Experimental
public record QuestEventTarget(String id, Quest quest, ServerPlayer player) {
    public static QuestEventTarget create(QuestEntry entry, ServerPlayer player) {
        return new QuestEventTarget(entry.id(), entry.quest(), player);
    }
}
