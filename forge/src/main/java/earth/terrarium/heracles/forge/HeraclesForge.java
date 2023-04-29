package earth.terrarium.heracles.forge;

import com.mojang.serialization.Codec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.condition.AllOfQuestCondition;
import earth.terrarium.heracles.condition.AnyOfQuestCondition;
import earth.terrarium.heracles.condition.PlayerAcquiredCriteria;
import earth.terrarium.heracles.condition.QuestCondition;
import earth.terrarium.heracles.resource.CriteriaManager;
import earth.terrarium.heracles.resource.QuestManager;
import earth.terrarium.heracles.reward.FunctionQuestReward;
import earth.terrarium.heracles.reward.LootQuestReward;
import earth.terrarium.heracles.reward.QuestReward;
import earth.terrarium.heracles.reward.RecipesQuestReward;
import earth.terrarium.heracles.team.ScoreboardTeamProvider;
import earth.terrarium.heracles.team.TeamProvider;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Function;
import java.util.function.Supplier;

@Mod(Heracles.MOD_ID)
public class HeraclesForge {
    public static final CapabilityToken<PlayerAcquiredCriteria> ACQUIRED_CRITERIA_CAPABILITY_TOKEN = new CapabilityToken<>() {};

    private static final DeferredRegister<Function<DeserializationContext, Codec<? extends QuestCondition>>> CONDITION_TYPE_REGISTRAR = DeferredRegister.create(Heracles.QUEST_CONDITION_TYPE_REGISTRY_KEY, Heracles.MOD_ID);
    private static final DeferredRegister<Codec<? extends QuestReward>> REWARD_TYPE_REGISTRAR = DeferredRegister.create(Heracles.QUEST_REWARD_TYPE_REGISTRY_KEY, Heracles.MOD_ID);
    private static final DeferredRegister<TeamProvider> TEAM_PROVIDER_REGISTRAR = DeferredRegister.create(Heracles.TEAM_PROVIDER_REGISTRY_KEY, Heracles.MOD_ID);

    public static final Supplier<IForgeRegistry<Function<DeserializationContext, Codec<? extends QuestCondition>>>> CONDITION_REGISTRY = CONDITION_TYPE_REGISTRAR.makeRegistry(() ->
            new RegistryBuilder<Function<DeserializationContext, Codec<? extends QuestCondition>>>()
                    .setName(Heracles.QUEST_CONDITION_TYPE_REGISTRY_KEY.location())
    );

    public static final Supplier<IForgeRegistry<Codec<? extends QuestReward>>> REWARD_REGISTRY = REWARD_TYPE_REGISTRAR.makeRegistry(() ->
            new RegistryBuilder<Codec<? extends QuestReward>>()
                    .setName(Heracles.QUEST_REWARD_TYPE_REGISTRY_KEY.location())
    );

    public static final Supplier<IForgeRegistry<TeamProvider>> TEAM_PROVIDER_REGISTRY = TEAM_PROVIDER_REGISTRAR.makeRegistry(() ->
            new RegistryBuilder<TeamProvider>()
                    .setName(Heracles.TEAM_PROVIDER_REGISTRY_KEY.location())
    );

    public HeraclesForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Heracles.init();

        CONDITION_TYPE_REGISTRAR.register(AllOfQuestCondition.KEY, () -> AllOfQuestCondition::simpleCodec);
        CONDITION_TYPE_REGISTRAR.register(AnyOfQuestCondition.KEY, () -> AnyOfQuestCondition::simpleCodec);

        REWARD_TYPE_REGISTRAR.register(LootQuestReward.KEY, LootQuestReward.MAP_CODEC::codec);
        REWARD_TYPE_REGISTRAR.register(RecipesQuestReward.KEY, RecipesQuestReward.MAP_CODEC::codec);
        REWARD_TYPE_REGISTRAR.register(FunctionQuestReward.KEY, () -> FunctionQuestReward.CODEC);

        TEAM_PROVIDER_REGISTRAR.register(ScoreboardTeamProvider.KEY, ScoreboardTeamProvider::new);

        modEventBus.addListener(HeraclesForge::registerCapabilities);
        modEventBus.addListener(HeraclesForge::attachCapabilities);

        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::addResourceReloaders);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerAcquiredCriteria.class);
    }

    private static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) return;

        event.addCapability(PlayerAcquiredCriteria.KEY, new PlayerAcquiredCriteriaCapability());
    }

    private static void addResourceReloaders(AddReloadListenerEvent event) {
        ReloadableServerResources resources = event.getServerResources();

        event.addListener(new CriteriaManager(resources::getPredicateManager));
        event.addListener(new QuestManager(resources::getPredicateManager));
    }
}
