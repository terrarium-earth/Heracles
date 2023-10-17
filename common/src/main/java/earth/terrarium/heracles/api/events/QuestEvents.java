package earth.terrarium.heracles.api.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestEntry;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

/**
 * (Unstable API) Server-side {@link Quest} lifecycle events.
 *
 * @apiNote This API comes with no support nor stability guarantees whatsoever. Use at your own risk.
 */
@ApiStatus.Experimental
public final class QuestEvents {
    private static final Event<Completed> COMPLETED = EventFactory.createLoop();

    private QuestEvents() {}

    @ApiStatus.Internal
    public static void fireQuestCompleted(QuestEntry entry, ServerPlayer player) {
        COMPLETED.invoker().onComplete(QuestEvent.create(entry, player));
    }

    /**
     * Actions taken after quest completion.
     */
    @FunctionalInterface
    public interface Completed {
        void onComplete(QuestEvent quest);
    }
}
