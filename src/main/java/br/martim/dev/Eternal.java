package br.martim.dev;

import br.martim.dev.api.config.ConfigAPI;
import br.martim.dev.api.user.User;
import br.martim.dev.command.*;
import br.martim.dev.controller.*;
import br.martim.dev.listener.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Eternal extends JavaPlugin {

    @Getter
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    private static ShopController shopController;

    @Getter
    private static UserController userController;

    @Override
    public void onLoad() {
        saveDefaultConfig();

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
                .silentLogs(true)
                .verboseOutput(false)
        );

        shopController = new ShopController(this);
        userController = new UserController(this);
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        shopController.handle();

        this.handleCommands();
        this.handlePlaceholder();

        getServer().getPluginManager().registerEvents(new UserListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        getServer().getConsoleSender().sendMessage("§aPlugin started successfully. Developed by Cássio Martim.");
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();

        shopController.disable();

        getServer().getConsoleSender().sendMessage("§cPlugin hit successfully. Developed by Cássio Martim.");
    }

    protected void handleCommands() {
        new LivesCommand().handle(this);
        new LifeShopCommand().handle(this);
        new ReviveCommand().handle(this);
    }

    protected void handlePlaceholder() {
        new PlaceholderExpansion() {

            @Override
            public @NotNull String getIdentifier() {
                return "eternal";
            }

            @Override
            public @NotNull String getAuthor() {
                return "Cássio Martim";
            }

            @Override
            public @NotNull String getVersion() {
                return "1.0.0";
            }

            @Override
            public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

                if (player == null)
                    return "";

                User user = userController.load(player.getUniqueId());

                if (params.equalsIgnoreCase("total_life"))
                    return String.valueOf(user.getLives());

                if (params.equalsIgnoreCase("heart_suffix"))
                    return ConfigAPI.getTabSuffix(user.getLives());

                return "";

            }
        }.register();
    }
}
