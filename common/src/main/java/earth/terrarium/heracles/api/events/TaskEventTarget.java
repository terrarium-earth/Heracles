package earth.terrarium.heracles.api.events;

import earth.terrarium.heracles.api.tasks.QuestTask;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

/**
 * (Unstable API) A target for event handlers.
 *
 * @apiNote This API comes with no support nor stability guarantees whatsoever. Use at your own risk.
 */
@ApiStatus.Experimental
public record TaskEventTarget(QuestTask task, ServerPlayer player) {
    public static TaskEventTarget create(QuestTask task, ServerPlayer player) {
        return new TaskEventTarget(task, player);
    }
}
