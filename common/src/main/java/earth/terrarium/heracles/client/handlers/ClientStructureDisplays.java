package earth.terrarium.heracles.client.handlers;

import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class ClientStructureDisplays {

    private static final Set<ResourceLocation> STRUCTURES = new HashSet<>();
    private static final Set<ResourceLocation> STRUCTURE_TAGS = new HashSet<>();

    public static void update(Set<ResourceLocation> structures, Set<ResourceLocation> structureTags) {
        STRUCTURES.clear();
        STRUCTURES.addAll(structures);
        STRUCTURE_TAGS.clear();
        STRUCTURE_TAGS.addAll(structureTags);
    }

    public static Set<ResourceLocation> getStructures() {
        return new HashSet<>(STRUCTURES);
    }

    public static Set<ResourceLocation> getStructureTags() {
        return new HashSet<>(STRUCTURE_TAGS);
    }

    public static boolean hasStructureData() {
        return !STRUCTURES.isEmpty() || !STRUCTURE_TAGS.isEmpty();
    }

    public static void clear() {
        STRUCTURES.clear();
        STRUCTURE_TAGS.clear();
    }
}