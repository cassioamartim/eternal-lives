package br.martim.dev.api.life;

import br.martim.dev.Eternal;
import br.martim.dev.api.config.ConfigAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class LifeAPI {

    public static void update(Player player, int lives) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        String teamName = "lives_" + player.getName();

        Team oldTeam = scoreboard.getTeam(teamName);
        if (oldTeam != null) oldTeam.unregister();

        Team team = scoreboard.registerNewTeam(teamName);

        team.addEntry(player.getName());

        String suffix = ConfigAPI.getTabSuffix()
                        .replace("{life}", String.valueOf(lives))
                                .replace("{heart}", ConfigAPI.getHeartSymbol());

        team.setSuffix(" " + suffix);

        player.setScoreboard(scoreboard);
    }
}
