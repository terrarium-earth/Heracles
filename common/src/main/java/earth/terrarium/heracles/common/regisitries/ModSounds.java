package earth.terrarium.heracles.common.regisitries;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.heracles.Heracles;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

    public static final ResourcefulRegistry<SoundEvent> SOUNDS = ResourcefulRegistries.create(BuiltInRegistries.SOUND_EVENT, Heracles.MOD_ID);

    public static final RegistryEntry<SoundEvent> QUEST_COMPLETE = SOUNDS.register("quest_complete", () -> SoundEvent.createVariableRangeEvent(Heracles.id("quest_complete")));
}
