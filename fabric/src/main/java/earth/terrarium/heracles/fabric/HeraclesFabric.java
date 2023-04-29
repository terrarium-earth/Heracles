package earth.terrarium.heracles.fabric;

import com.mojang.serialization.Codec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.condition.QuestCondition;
import earth.terrarium.heracles.resource.CriteriaManager;
import earth.terrarium.heracles.resource.QuestManager;
import earth.terrarium.heracles.reward.QuestReward;
import earth.terrarium.heracles.team.TeamProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.PredicateManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class HeraclesFabric implements ModInitializer {
    public static final Registry<Function<DeserializationContext, Codec<? extends QuestCondition>>> CONDITION_REGISTRY = FabricRegistryBuilder.createSimple(Heracles.QUEST_CONDITION_TYPE_REGISTRY_KEY).buildAndRegister();
    public static final Registry<Codec<? extends QuestReward>> REWARD_REGISTRY = FabricRegistryBuilder.createSimple(Heracles.QUEST_REWARD_TYPE_REGISTRY_KEY).buildAndRegister();
    public static final Registry<TeamProvider> TEAM_PROVIDER_REGISTRY = FabricRegistryBuilder.createSimple(Heracles.TEAM_PROVIDER_REGISTRY_KEY).buildAndRegister();

    private static PredicateManager predicateManager;

    private static PredicateManager latestPredicateManager() {
        return predicateManager;
    }

    public static void updatePredicateManager(PredicateManager predicateManager) {
        HeraclesFabric.predicateManager = predicateManager;
    }

    @Override
    public void onInitialize() {
        Heracles.init();

        ResourceManagerHelper resourceHelper = ResourceManagerHelper.get(PackType.SERVER_DATA);
        resourceHelper.registerReloadListener(wrapListener(new ResourceLocation(Heracles.MOD_ID, "criteria_manager"), new CriteriaManager(HeraclesFabric::latestPredicateManager)));
        resourceHelper.registerReloadListener(wrapListener(new ResourceLocation(Heracles.MOD_ID, "quest_manager"), new QuestManager(HeraclesFabric::latestPredicateManager)));
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
