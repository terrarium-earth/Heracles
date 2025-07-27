package earth.terrarium.heracles.api.client.settings.base;

import com.mojang.datafixers.util.Either;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.handlers.ClientStructureDisplays;
import earth.terrarium.heracles.client.widgets.boxes.AutocompleteEditBox;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record RegistryValueSetting<T>(
    ResourceKey<? extends Registry<T>> key
) implements Setting<RegistryValue<T>, AutocompleteEditBox<String>> {

    public static final RegistryValueSetting<Item> ITEM = new RegistryValueSetting<>(Registries.ITEM);
    public static final RegistryValueSetting<Block> BLOCK = new RegistryValueSetting<>(Registries.BLOCK);
    public static final RegistryValueSetting<Structure> STRUCTURE = new RegistryValueSetting<>(Registries.STRUCTURE);
    public static final RegistryValueSetting<Biome> BIOME = new RegistryValueSetting<>(Registries.BIOME);
    public static final RegistryValueSetting<EntityType<?>> ENTITY = new RegistryValueSetting<>(Registries.ENTITY_TYPE);

    @Override
    public AutocompleteEditBox<String> createWidget(int width, RegistryValue<T> value) {
        AutocompleteEditBox<String> box = new AutocompleteEditBox<>(Minecraft.getInstance().font, 0, 0, width, 11,
            (text, item) -> item.contains(text) && !item.equals(text), Function.identity(), s -> {});
        box.setMaxLength(Short.MAX_VALUE);
        List<String> suggestions = new ArrayList<>();
        
        var registry = Heracles.getRegistryAccess().registry(key).orElse(null);
        if (registry != null) {
            // Use registry data when available
            registry.getTagNames().map(tag -> "#" + tag.location()).forEach(suggestions::add);
            registry.keySet().stream().map(ResourceLocation::toString).forEach(suggestions::add);
        } else if (key.location().equals(Registries.STRUCTURE.location()) && ClientStructureDisplays.hasStructureData()) {
            // Fallback to cached structure data for structures
            ClientStructureDisplays.getStructureTags().stream()
                .map(tag -> "#" + tag.toString())
                .forEach(suggestions::add);
            ClientStructureDisplays.getStructures().stream()
                .map(ResourceLocation::toString)
                .forEach(suggestions::add);
        } 
        
        // Sort suggestions alphabetically for better user experience
        suggestions.sort(String.CASE_INSENSITIVE_ORDER);
        
        box.setSuggestions(suggestions);
        box.setValue(Optionull.mapOrDefault(value, RegistryValue::toRegistryString, ""));
        return box;
    }

    @Override
    public RegistryValue<T> getValue(AutocompleteEditBox<String> widget) {
        if (widget.getValue().startsWith("#")) {
            ResourceLocation id = ResourceLocation.tryParse(widget.getValue().substring(1));
            return id == null ? null : new RegistryValue<>(Either.right(TagKey.create(key, id)));
        }
        
        ResourceLocation id = ResourceLocation.tryParse(widget.getValue());
        if (id == null) {
            return null;
        }
        
        var registry = Heracles.getRegistryAccess().registry(key).orElse(null);
        if (registry != null) {
            return registry.getHolder(ResourceKey.create(key, id))
                .map(RegistryValue::new)
                .orElse(null);
        } else if (key.location().equals(Registries.STRUCTURE.location()) && ClientStructureDisplays.hasStructureData()) {
            // Fallback for structures when registry is unavailable
            if (ClientStructureDisplays.getStructures().contains(id)) {
                // Try to create a standalone holder, but handle exceptions gracefully
                try {
                    ResourceKey<T> resourceKey = ResourceKey.create(key, id);
                    @SuppressWarnings("unchecked")
                    net.minecraft.core.Holder<T> holder = (net.minecraft.core.Holder<T>) 
                        net.minecraft.core.Holder.Reference.createStandAlone(
                            Heracles.getRegistryAccess().lookupOrThrow(key), resourceKey);
                    return new RegistryValue<>(Either.left(holder));
                } catch (Exception e) {
                    // If holder creation fails, fall through to return null
                    // This is still better than crashing
                }
            }
        }
        // If we reach here, the registry is unavailable and we couldn't create a proper holder
        // Return null which will cause fallback to default, but this is better than crashing
        return null;
    }
}
