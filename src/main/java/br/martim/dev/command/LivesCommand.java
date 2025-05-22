package br.martim.dev.command;

import br.martim.dev.Eternal;
import br.martim.dev.api.user.User;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class LivesCommand {

    public void handle(JavaPlugin plugin) {

        new CommandAPICommand("lives")
                .withoutPermission("lives.admin")
                .withSubcommand(
                        new CommandAPICommand("set")
                                .withArguments(
                                        new PlayerArgument("player"),
                                        new IntegerArgument("amount", 0)
                                )
                                .executes((sender, args) -> {
                                    Player target = Objects.requireNonNull((Player) args.get(0), "Player is null");

                                    Integer amount = Objects.requireNonNull((Integer) args.get(1), "Amount is null");

                                    User user = Eternal.getUserController().load(target.getUniqueId());

                                    user.setLives(amount);
                                    user.save();

                                    target.sendMessage(
                                            "§aYour lives have been set to §c%s".formatted(amount + Eternal.HEART_SYMBOL)
                                    );

                                    sender.sendMessage(
                                            "§aYou changed the life of the player %s to §c%s"
                                                    .formatted(target.getName(), amount + Eternal.HEART_SYMBOL)
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

                                    Integer amount = Objects.requireNonNull((Integer) args.get(1), "Amount is null");

                                    User user = Eternal.getUserController().load(target.getUniqueId());

                                    user.setLives(user.getLives() + amount);
                                    user.save();

                                    target.sendMessage(
                                            "§aYour life has increased in §c+%s".formatted(amount + Eternal.HEART_SYMBOL)
                                    );

                                    sender.sendMessage("§aYou gave §c+%s§a lives to §e%s".formatted(amount + Eternal.HEART_SYMBOL, target.getName()));
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
                                            "§aYour life diminished in §c-%s".formatted(newLives + Eternal.HEART_SYMBOL)
                                    );

                                    sender.sendMessage(
                                            "§aYou decreased the player's life %s to §c%s"
                                                    .formatted(target.getName(), newLives + Eternal.HEART_SYMBOL)
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
