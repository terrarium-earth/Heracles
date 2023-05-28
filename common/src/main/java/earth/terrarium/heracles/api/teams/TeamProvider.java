package earth.terrarium.heracles.api.teams;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public interface TeamProvider {
    /**
     * Get the teams this provider includes for the given player not including the player itself
     *
     * @param player The player to get the teams of
     * @return A stream of the teams represented as a list of players
     */
    Stream<List<UUID>> getTeams(ServerPlayer player);

    /**
     * Get the teams this provider includes
     *
     * @param player The player to get the teams of
     * @return A stream of the teams represented as a list of players
     */
    Stream<List<UUID>> getTeams(ServerLevel level, UUID player);

    /**
     * Set the team changer for this provider
     * This is used to notify the system when a player changes teams.
     * @param teamChanger The team changer to set
     */
    void setupTeamChanger(BiConsumer<ServerLevel, UUID> teamChanger);
}
