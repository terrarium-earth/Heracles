package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.handlers.ClientStructureDisplays;
import earth.terrarium.heracles.client.widgets.boxes.AutocompleteEditBox;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A setting that handles structure input as plain strings while providing autocomplete functionality.
 * This setting stores and returns plain strings, but uses the same autocomplete data as RegistryValueSetting
 * from both registry access and ClientStructureDisplays.
 */
public record StructureStringSetting() implements Setting<String, AutocompleteEditBox<String>> {

    @Override
    public AutocompleteEditBox<String> createWidget(int width, String value) {
        AutocompleteEditBox<String> box = new AutocompleteEditBox<>(Minecraft.getInstance().font, 0, 0, width, 11,
            (text, item) -> item.contains(text) && !item.equals(text), Function.identity(), s -> {});
        box.setMaxLength(Short.MAX_VALUE);
        List<String> suggestions = new ArrayList<>();
        
        var registry = Heracles.getRegistryAccess().registry(Registries.STRUCTURE).orElse(null);
        if (registry != null) {
            // Use registry data when available
            registry.getTagNames().map(tag -> "#" + tag.location()).forEach(suggestions::add);
            registry.keySet().stream().map(ResourceLocation::toString).forEach(suggestions::add);
        } else if (ClientStructureDisplays.hasStructureData()) {
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
        box.setValue(Optionull.mapOrDefault(value, Function.identity(), ""));
        return box;
    }

    @Override
    public String getValue(AutocompleteEditBox<String> widget) {
        String value = widget.getValue();
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        
        // Validate the input format but return the string as-is
        if (value.startsWith("#")) {
            // Tag format: #namespace:tag_name
            ResourceLocation id = ResourceLocation.tryParse(value.substring(1));
            if (id == null) {
                return ""; // Invalid tag format
            }
        } else {
            // Structure format: namespace:structure_name
            ResourceLocation id = ResourceLocation.tryParse(value);
            if (id == null) {
                return ""; // Invalid structure format
            }
        }
        
        // Return the validated string as-is
        return value.trim();
    }
}