package earth.terrarium.heracles.fabric;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.BlockInteractTask;
import earth.terrarium.heracles.api.tasks.defaults.EntityInteractTask;
import earth.terrarium.heracles.api.tasks.defaults.ItemInteractTask;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import earth.terrarium.heracles.common.commands.ModCommands;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

public class HeraclesFabric {
    public static void init() {
        Heracles.setConfigPath(FabricLoader.getInstance().getConfigDir());
        Heracles.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, context, env) -> ModCommands.init(dispatcher));

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
                .testAndProgressTaskType(serverPlayer, stack, ItemInteractTask.TYPE);

            return InteractionResultHolder.pass(stack);
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

            QuestProgressHandler.getProgress(serverPlayer.server, serverPlayer.getUUID())
                .testAndProgressTaskType(serverPlayer, new BlockSourceImpl(serverPlayer.serverLevel(), hitResult.getBlockPos()), BlockInteractTask.TYPE);

            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

            QuestProgressHandler.getProgress(serverPlayer.server, serverPlayer.getUUID())
                .testAndProgressTaskType(serverPlayer, entity, EntityInteractTask.TYPE);

            return InteractionResult.PASS;
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Heracles.setRegistryAccess(server::registryAccess);
            QuestProgressHandler.setupChanger();
        });
    }
}
