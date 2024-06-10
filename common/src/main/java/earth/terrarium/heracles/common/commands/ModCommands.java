package earth.terrarium.heracles.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.DummyTask;
import earth.terrarium.heracles.common.handlers.CommonConfig;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

public class ModCommands {

    public static final SuggestionProvider<CommandSourceStack> QUESTS = (context, builder) -> {
        SharedSuggestionProvider.suggest(
            QuestHandler.quests()
                .keySet()
                .stream()
                .map(StringArgumentType::escapeIfRequired),
            builder
        );
        return builder.buildFuture();
    };

    public static void init(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(Heracles.MOD_ID);
        root.then(PinCommand.pin());
        root.then(ResetCommand.reset());
        root.then(ResetCommand.resetAll());
        root.then(CompleteCommand.complete());
        root.then(Commands.literal("dummy")
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
        );
        if (CommonConfig.registerUtilities) root.then(BarrierCommand.barrier());

        dispatcher.register(root);
    }
}
