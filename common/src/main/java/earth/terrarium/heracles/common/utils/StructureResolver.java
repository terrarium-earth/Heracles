package earth.terrarium.heracles.common.utils;

import com.mojang.datafixers.util.Either;
import earth.terrarium.heracles.Heracles;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * Utility class for resolving string structure names to registry entries in a context-aware manner.
 * Only performs registry resolution when needed (server-side or single-player scenarios).
 */
public class StructureResolver {

    /**
     * Resolves a structure string to a RegistryValue<Structure> when registry access is available.
     * Handles both direct structure names and tag references (prefixed with #).
     * 
     * @param structureString The structure string to resolve (e.g., "minecraft:village" or "#minecraft:village")
     * @param access The registry access to use for resolution
     * @return A RegistryValue<Structure> if resolution is successful, null otherwise
     */
    public static RegistryValue<Structure> resolveStructureString(String structureString, RegistryAccess access) {
        // Check if we have registry access (server-side or single-player)
        if (access == null) {
            return null; // Client-side fallback
        }
        
        if (structureString == null || structureString.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = structureString.trim();
        Registry<Structure> registry = access.registry(Registries.STRUCTURE).orElse(null);
        
        if (registry == null) {
            return null; // Registry not available
        }
        
        if (trimmed.startsWith("#")) {
            // Tag format: #namespace:tag_name
            ResourceLocation id = ResourceLocation.tryParse(trimmed.substring(1));
            if (id != null) {
                return new RegistryValue<>(Either.right(TagKey.create(Registries.STRUCTURE, id)));
            }
        } else {
            // Structure format: namespace:structure_name
            ResourceLocation id = ResourceLocation.tryParse(trimmed);
            if (id != null) {
                ResourceKey<Structure> resourceKey = ResourceKey.create(Registries.STRUCTURE, id);
                return registry.getHolder(resourceKey)
                    .map(RegistryValue::new)
                    .orElse(null);
            }
        }
        
        return null;
    }
    
    /**
     * Determines if we're in a context that requires registry validation.
     * This checks if we're on the server-side or in single-player mode where registry access is available.
     * 
     * @return true if registry resolution should be performed, false for client-only scenarios
     */
    public static boolean needsRegistryResolution() {
        // Determine if we're in a context that requires registry validation
        // (server-side or single-player)
        return Heracles.getRegistryAccess() != null;
    }
    
    /**
     * Convenience method that uses the current registry access from Heracles.
     * 
     * @param structureString The structure string to resolve
     * @return A RegistryValue<Structure> if resolution is successful, null otherwise
     */
    public static RegistryValue<Structure> resolveStructureString(String structureString) {
        return resolveStructureString(structureString, Heracles.getRegistryAccess());
    }
}