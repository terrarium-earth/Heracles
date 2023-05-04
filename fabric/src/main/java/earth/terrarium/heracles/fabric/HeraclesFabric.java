package earth.terrarium.heracles.fabric;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.menus.BasicContentMenuProvider;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.menus.quest.QuestMenu;
import earth.terrarium.heracles.common.resource.QuestManager;
import earth.terrarium.heracles.common.team.TeamProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class HeraclesFabric {
    public static final Registry<TeamProvider> TEAM_PROVIDER_REGISTRY = FabricRegistryBuilder.createSimple(Heracles.TEAM_PROVIDER_REGISTRY_KEY).buildAndRegister();

    public static void init() {
        Heracles.init();

        ResourceManagerHelper resourceHelper = ResourceManagerHelper.get(PackType.SERVER_DATA);
        resourceHelper.registerReloadListener(wrapListener(new ResourceLocation(Heracles.MOD_ID, "quest_manager"), QuestManager.INSTANCE));

        CommandRegistrationCallback.EVENT.register((dispatcher, context, env) -> {
            dispatcher.register(Commands.literal(Heracles.MOD_ID)
                    .then(Commands.literal("test")
                            .executes(context1 -> {
                                BasicContentMenuProvider.open(
                                    new QuestContent(new ResourceLocation("test"), true),
                                    Component.literal("Test"),
                                    QuestMenu::new,
                                    context1.getSource().getPlayerOrException()
                                );
                                return 1;
                            })
                    ));
        });
    }

    private static IdentifiableResourceReloadListener wrapListener(ResourceLocation id, PreparableReloadListener listener) {
        return new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return id;
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return listener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }
        };
    }
}
