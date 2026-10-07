package earth.terrarium.heracles.api.client.settings.tasks;

import com.mojang.datafixers.util.Either;
import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.RegistryValueSetting;
import earth.terrarium.heracles.api.client.settings.base.StructureStringSetting;
import earth.terrarium.heracles.api.tasks.defaults.StructureTask;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;

public class StructureTaskSettings implements SettingInitializer<StructureTask>, CustomizableQuestElementSettings<StructureTask> {

    public static final StructureTaskSettings INSTANCE = new StructureTaskSettings();

    @Override
    public CreationData create(@Nullable StructureTask object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("structure", new StructureStringSetting(), getDefaultStructureString(object));
        return settings;
    }

    @Override
    public StructureTask create(String id, @Nullable StructureTask object, Data data) {
        return create(object, data, (title, icon) -> new StructureTask(
            id,
            title,
            icon,
            null, // Keep structures as null for new string-based approach
            data.get("structure", new StructureStringSetting()).orElse(getDefaultStructureString(object))
        ));
    }

    private static String getDefaultStructureString(StructureTask object) {
        if (object != null) {
            // Convert existing RegistryValue to string if available
            if (object.structureString() != null) {
                return object.structureString();
            } else if (object.structures() != null) {
                return object.structures().toRegistryString();
            }
        }
        return "#minecraft:village"; // Default fallback
    }

    private static RegistryValue<Structure> getDefaultStructure(StructureTask object) {
        return Optionull.mapOrDefault(object, StructureTask::structures, 
            new RegistryValue<>(Either.right(TagKey.create(Registries.STRUCTURE, new ResourceLocation("minecraft", "village")))));
    }
}
