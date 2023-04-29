package earth.terrarium.heracles.resource;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import earth.terrarium.heracles.Quest;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.PredicateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.function.Supplier;

public class QuestManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String KEY = "quests/quest";
    private static QuestManager instance;

    private final BiMap<ResourceLocation, Quest> quests = HashBiMap.create();
    private final Supplier<PredicateManager> predicateManager;

    public QuestManager(Supplier<PredicateManager> predicateManager) {
        super(GSON, KEY);

        this.predicateManager = predicateManager;

        instance = this;
    }

    public static QuestManager getInstance() {
        return instance;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        quests.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation id = new ResourceLocation(entry.getKey().getNamespace(), KEY + '/' + entry.getKey().getPath());
            DeserializationContext context = new DeserializationContext(id, predicateManager.get());
            DataResult<Quest> result = Quest.codec(context).parse(JsonOps.INSTANCE, entry.getValue());

            var partialResult = result.get().right();
            if (partialResult.isPresent()) {
                LOGGER.error("Failed to load object " + id + " from codec", new JsonSyntaxException(partialResult.get().message()));
            } else {
                quests.put(entry.getKey(), result.get().left().orElseThrow());
            }
        }
    }

    public BiMap<ResourceLocation, Quest> getQuests() {
        return quests;
    }
}
