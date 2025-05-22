package br.martim.dev.command;

import br.martim.dev.menu.ShopMenu;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.plugin.java.JavaPlugin;

public class LifeShopCommand {

    public void handle(JavaPlugin plugin) {

        new CommandAPICommand("lifeshop")
                .executesPlayer((player, args) -> {
                    new ShopMenu(player, 1).build();
                })
                .register(plugin);
    }
}
