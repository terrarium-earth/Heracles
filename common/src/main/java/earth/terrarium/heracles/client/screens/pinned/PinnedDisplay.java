package earth.terrarium.heracles.client.screens.pinned;

import earth.terrarium.heracles.client.handlers.ClientQuests;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public record PinnedDisplay(
    ClientQuests.QuestEntry quest,
    float completion,
    Component title,
    List<FormattedCharSequence> tasks
) {
}
