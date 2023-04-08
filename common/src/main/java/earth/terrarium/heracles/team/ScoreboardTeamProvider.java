package earth.terrarium.heracles.team;

import net.minecraft.Optionull;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.stream.Stream;

public class ScoreboardTeamProvider implements TeamProvider {
    public static final String KEY = "scoreboard";

    @Override
    public Stream<List<ServerPlayer>> getTeams(ServerPlayer player) {
        return Optionull.mapOrDefault(
                player.getTeam(),
                team -> Stream.of(team.getPlayers().stream().map(player.server.getPlayerList()::getPlayerByName).toList()),
                Stream.empty()
        );
    }
}
