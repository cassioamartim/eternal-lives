package br.martim.dev.command;

import br.martim.dev.Eternal;
import br.martim.dev.api.user.User;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ReviveCommand {

    public void handle(JavaPlugin plugin) {

        new CommandAPICommand("revive")
                .withPermission(CommandPermission.OP)
                .withPermission("eternallivees.admin")
                .withArguments(new OfflinePlayerArgument("player"))
                .executes((sender, args) -> {

                    OfflinePlayer player = (OfflinePlayer) args.get(0);

                    if (player == null) {
                        sender.sendMessage("§cTarget not found.");
                        return;
                    }

                    User user = Eternal.getUserController().load(player.getUniqueId());

                    if (user.getLives() > 0) {
                        sender.sendMessage("§cThe player already has lives.");
                        return;
                    }

                    user.setReturnsAt(-1);
                    user.setLives(3);

                    user.save();

                    Player online = Bukkit.getPlayer(player.getUniqueId());

                    if (online != null) {
                        if (online.isDead())
                            online.spigot().respawn();

                        online.sendMessage("§aYou were revived by an admin.");
                    }

                    sender.sendMessage("§aYou revived the player §e%s§a.".formatted(player.getName()));
                })
                .register(plugin);
    }
}
