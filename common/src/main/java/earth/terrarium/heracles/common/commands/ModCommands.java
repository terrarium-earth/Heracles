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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ModCommands {

    public static void init(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(Heracles.MOD_ID)
            .then(Commands.literal("pin")
                .then(Commands.argument("quest", StringArgumentType.string())
                    .suggests(((context, builder) -> {
                        SharedSuggestionProvider.suggest(QuestHandler.quests().keySet(), builder);
                        return builder.buildFuture();
                    }))
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
                    })))
            .then(Commands.literal("reset")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("quest", StringArgumentType.string())
                    .suggests(((context, builder) -> {
                        SharedSuggestionProvider.suggest(QuestHandler.quests().keySet(), builder);
                        return builder.buildFuture();
                    }))
                    .executes(context -> {
                        CommandSourceStack source = context.getSource();
                        ServerPlayer player = source.getPlayerOrException();
                        String quest = StringArgumentType.getString(context, "quest");
                        QuestProgressHandler.getProgress(source.getServer(), player.getUUID()).resetQuest(quest, player);
                        source.sendSystemMessage(Component.translatable("commands.heracles.reset.success", quest));
                        return 1;
                    })))
            .then(Commands.literal("dummy")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("id", StringArgumentType.string())
                    .executes(context -> {
                        CommandSourceStack source = context.getSource();
                        ServerPlayer player = source.getPlayerOrException();
                        String quest = StringArgumentType.getString(context, "id");
                        QuestProgressHandler.getProgress(source.getServer(), player.getUUID()).testAndProgressTaskType(player, quest, DummyTask.TYPE);
                        return 1;
                    })
                )
            )
        );
    }
}
