package earth.terrarium.heracles.common.team;

import earth.terrarium.heracles.Heracles;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface TeamProvider {
    /**
     * Get the teams this provider includes
     * @param player The player to get the teams of
     * @return A stream of the teams represented as a list of players
     */
    Stream<List<UUID>> getTeams(ServerPlayer player);

    /**
     * Get all the teams associated with the player
     * @param player The player to get the teams of
     * @return A stream of the teams represented as a list of players
     */
    static Stream<List<UUID>> getAllTeams(ServerPlayer player) {
        return StreamSupport.stream(Heracles.getTeamProviders().spliterator(), false)
                .flatMap(provider -> provider.getTeams(player));
    }
}
