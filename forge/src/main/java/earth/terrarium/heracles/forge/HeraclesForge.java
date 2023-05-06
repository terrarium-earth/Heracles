package earth.terrarium.heracles.forge;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.team.ScoreboardTeamProvider;
import earth.terrarium.heracles.common.team.TeamProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

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
    }

    private static void onResourcesLoad(AddReloadListenerEvent event) {
        QuestHandler.load(FMLPaths.CONFIGDIR.get());
    }
}
