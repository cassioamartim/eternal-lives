package br.martim.dev.listener;

import br.martim.dev.Eternal;
import br.martim.dev.api.config.ConfigAPI;
import br.martim.dev.api.life.LifeAPI;
import br.martim.dev.api.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.TimeUnit;

import static br.martim.dev.api.config.ConfigAPI.getMessage;

public class UserListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        User user = Eternal.getUserController().load(player.getUniqueId());

        if (user == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cAn error occurred when carrying your account on the eternal.");
            return;
        }

        if (user.isBanned())
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ConfigAPI.getBannedKickMessage(user.getReturnsAt()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        User user = Eternal.getUserController().load(player.getUniqueId());

        if (user.hasAlreadyBanned() && user.isDead()) {
            user.setLives(ConfigAPI.getLifeUponBanishment());
            user.setReturnsAt(-1);

            user.save();

            String[] messageUponBanishment = ConfigAPI.getMessageUponBanishment();

            if (messageUponBanishment.length > 0)
                player.sendMessage(messageUponBanishment);
        } else
            LifeAPI.update(player, user.getLives());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Eternal.getUserController().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        User user = Eternal.getUserController().load(player.getUniqueId());

        if (user != null) {
            user.setLives(user.getLives() - ConfigAPI.getDiedLife());

            if (user.getLives() <= 0) {

                user.setReturnsAt(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(ConfigAPI.getBannedDays()));

                user.save();

                player.kickPlayer(ConfigAPI.getBannedKickMessage(user.getReturnsAt()));
            } else
                user.save();
        }

        Player killer = player.getKiller();

        if (killer != null) {
            User killerUser = Eternal.getUserController().load(killer.getUniqueId());

            if (killerUser != null) {
                killerUser.setLives(killerUser.getLives() + ConfigAPI.getKillLife());
                killerUser.save();

                String message = getMessage("player-receiving-life")
                        .replace("{life}", ConfigAPI.getKillLife() + ConfigAPI.getHeartSymbol())
                        .replace("{player}", player.getName());

                if (!message.isEmpty())
                    killer.sendMessage(message);
            }
        }
    }
}
