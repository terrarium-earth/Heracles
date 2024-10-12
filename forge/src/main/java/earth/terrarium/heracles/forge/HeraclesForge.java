package earth.terrarium.heracles.forge;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.*;
import earth.terrarium.heracles.common.commands.ModCommands;
import earth.terrarium.heracles.common.handlers.TaskManager;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.utils.PlatformSettings;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(Heracles.MOD_ID)
public class HeraclesForge {

    public HeraclesForge() {
        Heracles.setConfigPath(FMLPaths.CONFIGDIR.get());
        Heracles.init(new PlatformSettings(false));

        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onAdvancementEarn);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onTick);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onItemUse);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onItemInteract);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onBlockInteract);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onEntityDeath);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onRegisterCommands);

        if (FMLEnvironment.dist.isClient()) {
            HeraclesForgeClient.init();
        }
    }

    private static void onEntityDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, event.getEntity(), KillEntityQuestTask.TYPE);
    }

    private static void onServerStarting(ServerAboutToStartEvent event) {
        Heracles.setRegistryAccess(event.getServer()::registryAccess);
        QuestProgressHandler.setupChanger();
    }

    private static void onAdvancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, event.getAdvancement(), AdvancementTask.TYPE);
    }

    private static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.player.tickCount % 20 != 0) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        TaskManager.onPlayerTick(player);
    }

    private static void onItemUse(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, event.getItem(), ItemUseTask.TYPE);
    }

    private static void onItemInteract(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, event.getItemStack(), ItemInteractTask.TYPE);
    }

    private static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, new BlockSourceImpl(player.serverLevel(), event.getPos()), BlockInteractTask.TYPE);
    }

    private static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, event.getTarget(), EntityInteractTask.TYPE);
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.init(event.getDispatcher());
    }
}
