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

public class QuestManager extends CodecResourceReloadListener<Quest> {
    public static final QuestManager INSTANCE = new QuestManager();

    public QuestManager() {
        super(Quest.CODEC, "quests/quest");
    }

    public BiMap<ResourceLocation, Quest> getQuests() {
        return getValues();
    }
}
