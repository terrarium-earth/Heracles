package earth.terrarium.heracles.forge;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.*;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.team.ScoreboardTeamProvider;
import earth.terrarium.heracles.common.team.TeamProvider;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.Map;
import java.util.function.Supplier;

@Mod(Heracles.MOD_ID)
public class HeraclesForge {
    private static final DeferredRegister<TeamProvider> TEAM_PROVIDER_REGISTRAR = DeferredRegister.create(Heracles.TEAM_PROVIDER_REGISTRY_KEY, Heracles.MOD_ID);

    public static final Supplier<IForgeRegistry<TeamProvider>> TEAM_PROVIDER_REGISTRY = TEAM_PROVIDER_REGISTRAR.makeRegistry(() ->
        new RegistryBuilder<TeamProvider>()
            .setName(Heracles.TEAM_PROVIDER_REGISTRY_KEY.location())
    );

    public HeraclesForge() {
        Heracles.init();

        TEAM_PROVIDER_REGISTRAR.register(ScoreboardTeamProvider.KEY, ScoreboardTeamProvider::new);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onResourcesLoad);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onAdvancementEarn);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onTravelToDimension);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onTick);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onItemUse);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onItemInteract);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onBlockInteract);
    }

    private static void onResourcesLoad(AddReloadListenerEvent event) {
        QuestHandler.load(Heracles.getRegistryAccess(), FMLPaths.CONFIGDIR.get());
    }

    private static void onServerStarting(ServerAboutToStartEvent event) {
        Heracles.setRegistryAccess(event.getServer()::registryAccess);
    }

    private static void onAdvancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, event.getAdvancement(), AdvancementTask.TYPE);
    }

    private static void onTravelToDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, player.getLevel(), EnterDimensionTask.TYPE);
    }

    private static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.player.tickCount % 20 != 0) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
        Map<Structure, LongSet> structures = player.getLevel().structureManager().getAllStructuresAt(player.getOnPos());

        progress.testAndProgressTaskType(player, player.level.getBiome(player.getOnPos()), FindBiomeTask.TYPE);

        if (!structures.isEmpty()) {
            progress.testAndProgressTaskType(player, structures.keySet(), FindStructureTask.TYPE);
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
            .testAndProgressTaskType(player, event.getItemStack(), ItemInteractionTask.TYPE);
    }

    private static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, Pair.of(player.getLevel(), event.getPos()), BlockInteractionTask.TYPE);
    }
}
