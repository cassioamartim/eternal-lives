package br.martim.dev;

import br.martim.dev.command.LivesCommand;
import br.martim.dev.controller.ShopController;
import br.martim.dev.controller.UserController;
import br.martim.dev.listener.MenuListener;
import br.martim.dev.listener.UserListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Eternal extends JavaPlugin {

    public static final String HEART_SYMBOL = "❤";

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
    }
}
