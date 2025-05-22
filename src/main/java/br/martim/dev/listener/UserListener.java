package br.martim.dev.listener;

import br.martim.dev.Eternal;
import br.martim.dev.api.config.ConfigAPI;
import br.martim.dev.api.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

            user.save();

            String[] messageUponBanishment = ConfigAPI.getMessageUponBanishment();

            if (messageUponBanishment.length > 0)
                player.sendMessage(messageUponBanishment);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Eternal.getUserController().remove(event.getPlayer().getUniqueId());
    }
}
