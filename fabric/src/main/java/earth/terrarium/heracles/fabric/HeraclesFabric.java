package earth.terrarium.heracles.fabric;

import com.mojang.brigadier.arguments.StringArgumentType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.BlockInteractionTask;
import earth.terrarium.heracles.api.tasks.defaults.ItemInteractionTask;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import earth.terrarium.heracles.common.handlers.pinned.PinnedQuestHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.team.TeamProvider;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

public class HeraclesFabric {
    public static final Registry<TeamProvider> TEAM_PROVIDER_REGISTRY = FabricRegistryBuilder.createSimple(Heracles.TEAM_PROVIDER_REGISTRY_KEY).buildAndRegister();

    public static void init() {
        Heracles.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, context, env) ->
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
                .then(Commands.literal("pin").then(Commands.argument("quest", StringArgumentType.string()).executes(context1 -> {
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
            )
        );

        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((level, killer, entity) -> {
            if (killer instanceof ServerPlayer player) {
                QuestProgressHandler.getProgress(level.getServer(), killer.getUUID())
                    .testAndProgressTaskType(player, entity, KillEntityQuestTask.TYPE);
            }
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResultHolder.pass(ItemStack.EMPTY);

            ItemStack stack = player.getItemInHand(hand);

            QuestProgressHandler.getProgress(serverPlayer.server, serverPlayer.getUUID())
                .testAndProgressTaskType(serverPlayer, stack, ItemInteractionTask.TYPE);

            return InteractionResultHolder.pass(stack);
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

            QuestProgressHandler.getProgress(serverPlayer.server, serverPlayer.getUUID())
                .testAndProgressTaskType(serverPlayer, new BlockSourceImpl(serverPlayer.getLevel(), hitResult.getBlockPos()), BlockInteractionTask.TYPE);

            return InteractionResult.PASS;
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> Heracles.setRegistryAccess(server::registryAccess));
    }
}
