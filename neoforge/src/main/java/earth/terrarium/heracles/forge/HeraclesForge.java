package earth.terrarium.heracles.forge;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.*;
import earth.terrarium.heracles.common.blocks.BlockSource;
import earth.terrarium.heracles.common.commands.ModCommands;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.utils.PlatformSettings;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;

@Mod(Heracles.MOD_ID)
public class HeraclesForge {

    public HeraclesForge() {
        Heracles.setConfigPath(FMLPaths.CONFIGDIR.get());
        Heracles.init(new PlatformSettings(false));

        NeoForge.EVENT_BUS.addListener(HeraclesForge::onServerStarting);
        NeoForge.EVENT_BUS.addListener(HeraclesForge::onAdvancementEarn);
        NeoForge.EVENT_BUS.addListener(HeraclesForge::onTick);
        NeoForge.EVENT_BUS.addListener(HeraclesForge::onItemUse);
        NeoForge.EVENT_BUS.addListener(HeraclesForge::onItemInteract);
        NeoForge.EVENT_BUS.addListener(HeraclesForge::onBlockInteract);
        NeoForge.EVENT_BUS.addListener(HeraclesForge::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(HeraclesForge::onEntityDeath);
        NeoForge.EVENT_BUS.addListener(HeraclesForge::onRegisterCommands);

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

    private static void onTick(PlayerTickEvent event) {
        if (event.getEntity().tickCount % 20 != 0) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
        Map<Structure, LongSet> structures = player.serverLevel().structureManager().getAllStructuresAt(player.getOnPos());

        progress.testAndProgressTaskType(player, player.serverLevel().getBiome(player.getOnPos()), BiomeTask.TYPE);
        progress.testAndProgressTaskType(player, player, LocationTask.TYPE);

        if (!structures.isEmpty()) {
            progress.testAndProgressTaskType(player, structures.keySet(), StructureTask.TYPE);
        }
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
            .testAndProgressTaskType(player, new BlockSource(player.serverLevel(), event.getPos()), BlockInteractTask.TYPE);
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
