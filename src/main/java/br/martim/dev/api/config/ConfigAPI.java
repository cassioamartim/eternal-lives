package br.martim.dev.api.config;

import br.martim.dev.Eternal;
import br.martim.dev.util.Util;
import br.martim.dev.util.list.DateUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.stream.Collectors;

public class ConfigAPI {

    private static FileConfiguration config() {
        return Eternal.getPlugin(Eternal.class).getConfig();
    }

    public static void reload() {
        Eternal.getPlugin(Eternal.class).reloadConfig();

        Eternal.getShopController().handle();

    }

    public static int getBannedDays() {
        return config().getInt("banned-days", 10);
    }

    public static String getBannedKickMessage(long returnsAt) {
        if (!config().contains("banned-kick-message")) return "";

        return config().getStringList("banned-kick-message")
                .stream()
                .map(msg -> msg.replace("{date}", DateUtil.getDate(returnsAt)))
                .map(Util::color)
                .collect(Collectors.joining("\n"));
    }

    public static int getLifeUponBanishment() {
        return config().getInt("life-upon-banishment", 3);
    }

    public static String[] getMessageUponBanishment() {
        if (!config().contains("message-upon-banishment")) return new String[]{};

        return config().getStringList("message-upon-banishment")
                .stream()
                .map(msg -> msg.replace("{lives}", String.valueOf(getLifeUponBanishment())))
                .map(Util::color)
                .toArray(String[]::new);
    }

    public static int getInitialLife() {
        return config().getInt("initial-life", 10);
    }

    public static int getKillLife() {
        return config().getInt("kill-life", 1);
    }

    public static int getDiedLife() {
        return config().getInt("died-life", 1);
    }

    public static String getHeartSymbol() {
        return config().getString("heart-symbol", "❤");
    }

    public static String getTabSuffix() {
        return Util.color(config().getString("tab-suffix", "&c{life}&4{heart}"));
    }

    public static String getMessage(String key) {
        if (!config().contains("messages." + key.toLowerCase())) return "";

        return Util.color(config().getString("messages." + key.toLowerCase()));
    }
}
