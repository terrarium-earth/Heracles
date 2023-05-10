package earth.terrarium.heracles.fabric;

import com.mojang.brigadier.arguments.StringArgumentType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.team.TeamProvider;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
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
                    ModUtils.openQuest(player, id);
                    return 1;
                })))
                .then(Commands.literal("tests").then(Commands.argument("group", StringArgumentType.string()).executes(context1 -> {
                    ModUtils.openGroup(context1.getSource().getPlayerOrException(), StringArgumentType.getString(context1, "group"));
                    return 1;
                })))
                .then(Commands.literal("edit").then(Commands.argument("group", StringArgumentType.string()).executes(context1 -> {
                    ModUtils.editGroup(context1.getSource().getPlayerOrException(), StringArgumentType.getString(context1, "group"));
                    return 1;
                })))
            );
        });

        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((level, killer, entity) -> {
            if (killer instanceof ServerPlayer player) {
                QuestProgressHandler.getProgress(level.getServer(), killer.getUUID())
                    .testAndProgressTaskType(player, entity, KillEntityQuestTask.class);
            }
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> Heracles.setRegistryAccess(server::registryAccess));
    }
}
