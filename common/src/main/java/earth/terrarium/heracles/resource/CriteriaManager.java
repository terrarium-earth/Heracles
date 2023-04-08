package earth.terrarium.heracles.resource;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.PredicateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

public class CriteriaManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String KEY = "quests/criterion";

    private static CriteriaManager instance;
    private final BiMap<ResourceLocation, Criterion> criteria = HashBiMap.create();
    private final PredicateManager predicateManager;

    public CriteriaManager(PredicateManager predicateManager) {
        super(GSON, KEY);
        this.predicateManager = predicateManager;

        instance = this;
    }

    public static CriteriaManager getInstance() {
        return instance;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        criteria.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            try {
                var deserializationContext = new DeserializationContext(new ResourceLocation(entry.getKey().getNamespace(), KEY + "/" + entry.getKey().getPath()), predicateManager);

                Criterion criterion = Criterion.criterionFromJson(GsonHelper.convertToJsonObject(entry.getValue(), "root"), deserializationContext);

                criteria.put(entry.getKey(), criterion);
            } catch (RuntimeException exception) {
                LOGGER.error("Failed to load criterion " + entry.getKey(), exception);
            }
        }
    }

    public BiMap<ResourceLocation, Criterion> getCriteria() {
        return criteria;
    }
}
