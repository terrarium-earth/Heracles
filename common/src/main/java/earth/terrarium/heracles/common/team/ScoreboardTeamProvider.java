package earth.terrarium.heracles.common.team;

import com.mojang.authlib.GameProfile;
import net.minecraft.Optionull;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class ScoreboardTeamProvider implements TeamProvider {
    public static final String KEY = "scoreboard";

    @Override
    public Stream<List<UUID>> getTeams(ServerPlayer player) {
        return Optionull.mapOrDefault(
                player.getTeam(),
                team -> Stream.of(team.getPlayers()
                    .stream()
                    .map(player.server.getProfileCache()::get)
                    .flatMap(Optional::stream)
                    .map(GameProfile::getId)
                    .toList()),
                Stream.of(List.of(player.getUUID()))
        );
    }
}
