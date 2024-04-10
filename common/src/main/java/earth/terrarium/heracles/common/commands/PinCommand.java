package earth.terrarium.heracles.common.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import earth.terrarium.heracles.common.handlers.pinned.PinnedQuestHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class PinCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> pin() {
        return Commands.literal("pin")
            .then(Commands.argument("quest", StringArgumentType.string())
                .suggests(ModCommands.QUESTS)
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    ServerPlayer player = source.getPlayerOrException();
                    String quest = StringArgumentType.getString(context, "quest");
                    var pinned = PinnedQuestHandler.getPinned(player);
                    if (pinned.contains(quest)) {
                        pinned.remove(quest);
                    } else {
                        pinned.add(quest);
                    }
                    PinnedQuestHandler.sync(player);
                    return 1;
            }));
    }
}
