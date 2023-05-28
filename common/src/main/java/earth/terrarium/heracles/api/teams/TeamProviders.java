package earth.terrarium.heracles.api.teams;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public final class TeamProviders {

    private static final Map<ResourceLocation, TeamProvider> providers = new ConcurrentHashMap<>();

    public static void register(ResourceLocation id, TeamProvider provider) {
        providers.put(id, provider);
    }

    public static List<UUID> getMembers(ServerPlayer player) {
        return providers.values().stream()
            .flatMap(provider -> provider.getTeams(player))
            .flatMap(List::stream)
            .distinct()
            .toList();
    }

    public static List<UUID> getMembers(ServerLevel level, UUID player) {
        return providers.values().stream()
            .flatMap(provider -> provider.getTeams(level, player))
            .flatMap(List::stream)
            .distinct()
            .toList();
    }

    public static void init(BiConsumer<ServerLevel, UUID> changer) {
        providers.values().forEach(provider -> provider.setupTeamChanger(changer));
    }
}
