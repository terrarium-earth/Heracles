package earth.terrarium.heracles.fabric;

import com.mojang.brigadier.arguments.StringArgumentType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.Quest;
import earth.terrarium.heracles.common.handlers.QuestHandler;
import earth.terrarium.heracles.common.handlers.QuestProgressHandler;
import earth.terrarium.heracles.common.menus.BasicContentMenuProvider;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.menus.quest.QuestMenu;
import earth.terrarium.heracles.common.team.TeamProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.server.level.ServerPlayer;

public class HeraclesFabric {
    public static final Registry<TeamProvider> TEAM_PROVIDER_REGISTRY = FabricRegistryBuilder.createSimple(Heracles.TEAM_PROVIDER_REGISTRY_KEY).buildAndRegister();

    public static void init() {
        Heracles.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, context, env) -> {
            dispatcher.register(Commands.literal(Heracles.MOD_ID)
                .then(Commands.literal("test").then(Commands.argument("quest", StringArgumentType.greedyString()).executes(context1 -> {
                    ServerPlayer player = context1.getSource().getPlayerOrException();
                    String id = StringArgumentType.getString(context1, "quest");
                    Quest quest = QuestHandler.get(id);
                    BasicContentMenuProvider.open(
                        new QuestContent(
                            id,
                            quest,
                            QuestProgressHandler.getProgress(player.server, player.getUUID()).getProgress(id)
                        ),
                        CommonComponents.EMPTY,
                        QuestMenu::new,
                        player
                    );
                    return 1;
                }))));
        });
    }
}
