package earth.terrarium.heracles.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.DummyTask;
import earth.terrarium.heracles.common.handlers.pinned.PinnedQuestHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

public class ModCommands {

    public static void init(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(Heracles.MOD_ID)
            .then(Commands.literal("pin")
                .then(Commands.argument("quest", StringArgumentType.string())
                .suggests(((context, builder) -> {
                    SharedSuggestionProvider.suggest(QuestHandler.quests().keySet(), builder);
                    return builder.buildFuture();
                }))
                .executes(context1 -> {
                String quest = StringArgumentType.getString(context1, "quest");
                var pinned = PinnedQuestHandler.getPinned(context1.getSource().getPlayerOrException());
                if (pinned.contains(quest)) {
                    pinned.remove(quest);
                } else {
                    pinned.add(quest);
                }
                PinnedQuestHandler.sync(context1.getSource().getPlayerOrException());
                return 1;
            })))
            .then(Commands.literal("reset")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("quest", StringArgumentType.string())
                    .suggests(((context, builder) -> {
                        SharedSuggestionProvider.suggest(QuestHandler.quests().keySet(), builder);
                        return builder.buildFuture();
                    }))
                .executes(context1 -> {
                String quest = StringArgumentType.getString(context1, "quest");
                QuestProgressHandler.getProgress(context1.getSource().getServer(), context1.getSource().getPlayerOrException().getUUID()).getProgress(quest).reset();
                return 1;
            })))
            .then(Commands.literal("dummy")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("id", StringArgumentType.string())
                    .executes(context1 -> {
                        String quest = StringArgumentType.getString(context1, "id");
                        QuestProgressHandler.getProgress(context1.getSource().getServer(), context1.getSource().getPlayerOrException().getUUID())
                            .testAndProgressTaskType(context1.getSource().getPlayerOrException(), quest, DummyTask.TYPE);
                        return 1;
                    })
                )
            )
        );
    }
}
