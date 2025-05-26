package br.martim.dev.command;

import br.martim.dev.Eternal;
import br.martim.dev.api.config.ConfigAPI;
import br.martim.dev.api.user.User;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static br.martim.dev.api.config.ConfigAPI.getMessage;

public class LivesCommand {

    public void handle(JavaPlugin plugin) {

        new CommandAPICommand("lives")
                .withPermission(CommandPermission.OP)
                .withPermission("eternallives.admin")
                .withSubcommand(
                        new CommandAPICommand("reload")
                                .executes((sender, args) -> {

                                    ConfigAPI.reload();

                                    sender.sendMessage("§aThe plugin was successfully restarted.");
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("set")
                                .withArguments(
                                        new PlayerArgument("player"),
                                        new IntegerArgument("amount", 0)
                                )
                                .executes((sender, args) -> {
                                    Player target = Objects.requireNonNull((Player) args.get(0), "Player is null");

                                    Integer amount = Objects.requireNonNull((Integer) args.get(1), "Amount is null");

                                    if (amount >= ConfigAPI.getMaxLife()) {
                                        sender.sendMessage(
                                                getMessage("max-life-reached")
                                                        .replace("{player}", target.getName())
                                        );
                                        return;
                                    }

                                    User user = Eternal.getUserController().load(target.getUniqueId());

                                    user.setLives(amount);
                                    user.save();

                                    target.sendMessage(
                                            getMessage("lives-command-set-in-target")
                                                    .replace("{life}", amount + ConfigAPI.getHeartSymbol())
                                    );

                                    sender.sendMessage(
                                            getMessage("lives-command-set-sender")
                                                    .replace("{player}", target.getName())
                                                    .replace("{life}", amount + ConfigAPI.getHeartSymbol())
                                    );
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("add")
                                .withArguments(
                                        new PlayerArgument("player"),
                                        new IntegerArgument("amount", 1)
                                )
                                .executes((sender, args) -> {
                                    Player target = Objects.requireNonNull((Player) args.get(0), "Player is null");

                                    int amount = Objects.requireNonNull((Integer) args.get(1), "Amount is null");

                                    User user = Eternal.getUserController().load(target.getUniqueId());

                                    int newLives = Math.min(ConfigAPI.getMaxLife(), user.getLives() + amount);

                                    if (user.getLives() >= ConfigAPI.getMaxLife()) {
                                        sender.sendMessage(
                                                getMessage("max-life-reached")
                                                        .replace("{player}", target.getName())
                                        );
                                        return;
                                    }

                                    user.setLives(newLives);
                                    user.save();

                                    amount = Math.min(amount, ConfigAPI.getMaxLife());

                                    target.sendMessage(
                                            getMessage("lives-command-add-in-target")
                                                    .replace("{life}", amount + ConfigAPI.getHeartSymbol())
                                    );

                                    sender.sendMessage(
                                            getMessage("lives-command-add-sender")
                                                    .replace("{player}", target.getName())
                                                    .replace("{life}", amount + ConfigAPI.getHeartSymbol())
                                    );
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("remove")
                                .withArguments(
                                        new PlayerArgument("player"),
                                        new IntegerArgument("amount", 1)
                                )
                                .executes((sender, args) -> {
                                    Player target = Objects.requireNonNull((Player) args.get(0), "Player is null");

                                    Integer amount = Objects.requireNonNull((Integer) args.get(1), "Amount is null");

                                    User user = Eternal.getUserController().load(target.getUniqueId());

                                    int newLives = Math.max(user.getLives() - amount, 0);

                                    user.setLives(newLives);
                                    user.save();

                                    target.sendMessage(
                                            getMessage("lives-command-remove-in-target")
                                                    .replace("{life}", amount + ConfigAPI.getHeartSymbol())
                                    );

                                    sender.sendMessage(
                                            getMessage("lives-command-remove-sender")
                                                    .replace("{player}", target.getName())
                                                    .replace("{life}", amount + ConfigAPI.getHeartSymbol())
                                    );
                                })
                )
                .executes((sender, args) -> {
                    sender.sendMessage("""
                            §eCorrect usage:
                            /lives set <player> <amount>
                            /lives add <player> <amount>
                            /lives remove <player> <amount>
                            """);
                })
                .register(plugin);
    }
}
