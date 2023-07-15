package earth.terrarium.heracles.client.compat.rei;

import me.shedaniel.rei.api.client.favorites.FavoriteEntryType;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import net.minecraft.network.chat.Component;

@SuppressWarnings("UnstableApiUsage")
public class HeraclesReiClientPlugin implements REIClientPlugin {

    @Override
    public void registerFavorites(FavoriteEntryType.Registry registry) {
        registry.register(HearclesFavoriteEntry.ID, HearclesFavoriteEntry.Type.INSTANCE);
        registry.getOrCrateSection(Component.translatable("rei.sections.odyssey"))
            .add(new HearclesFavoriteEntry());
    }
}
