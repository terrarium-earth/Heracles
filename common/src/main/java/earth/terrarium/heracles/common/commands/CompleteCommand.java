package earth.terrarium.heracles.common.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;

public class CompleteCommand {

    private static final SuggestionProvider<CommandSourceStack> QUESTS = (context, builder) -> {
        SharedSuggestionProvider.suggest(
            QuestHandler.quests()
                .keySet()
                .stream()
                .map(StringArgumentType::escapeIfRequired),
            builder
        );
        return builder.buildFuture();
    };

    public static LiteralArgumentBuilder<CommandSourceStack> complete() {
        return Commands.literal("complete")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("quest", StringArgumentType.string())
                .suggests(QUESTS)
                .then(Commands.argument("target", EntityArgument.players())
                    .executes(context -> complete(EntityArgument.getPlayers(context, "target"), context))
                )
                .executes(context -> complete(List.of(context.getSource().getPlayerOrException()), context))
            );
    }

    private static int complete(Collection<ServerPlayer> players, CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String quest = StringArgumentType.getString(context, "quest");
        Quest questObj = QuestHandler.get(quest);
        if (questObj == null) {
            source.sendFailure(Component.translatable("commands.heracles.complete.failed", quest));
            return 0;
        }
        for (ServerPlayer player : players) {
            QuestProgressHandler.getProgress(source.getServer(), player.getUUID()).completeQuest(quest, questObj, player);
        }
        source.sendSuccess(
            () -> Component.translatable("commands.heracles.complete.success", quest),
            false
        );
        return 1;
    }
}
