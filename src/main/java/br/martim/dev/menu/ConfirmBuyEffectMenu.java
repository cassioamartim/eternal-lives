package br.martim.dev.menu;

import br.martim.dev.Eternal;
import br.martim.dev.api.config.ConfigAPI;
import br.martim.dev.api.menu.Menu;
import br.martim.dev.api.menu.item.Item;
import br.martim.dev.api.menu.sound.MenuSound;
import br.martim.dev.api.shop.effect.EffectData;
import br.martim.dev.api.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static br.martim.dev.api.config.ConfigAPI.getMessage;

public class ConfirmBuyEffectMenu extends Menu {

    private final User user;
    private final EffectData effect;

    public ConfirmBuyEffectMenu(Player player, EffectData effect, Menu last) {
        super(player, "§aConfirm Buy Effect", last, 3);

        this.user = Eternal.getUserController().load(player.getUniqueId());
        this.effect = effect;
    }

    @Override
    public void build() {
        clear();

        add(12, Item.of(Material.RED_DYE, "§cCancel")
                .click(event -> {
                    sound(MenuSound.NO);
                    getLast().build();
                }));

        add(14, Item.of(Material.LIME_DYE, "§aConfirm")
                .click(event -> {
                    close();
                    sound(MenuSound.ACTION_CONFIRMED);

                    user.setLives(user.getLives() - effect.getPrice());
                    user.save();

                    getPlayer().sendMessage(
                            getMessage("bought-effect")
                                    .replace("{effect}", effect.getName())
                                    .replace("{lives}", effect.getPrice() + ConfigAPI.getHeartSymbol())
                    );

                    effect.apply(getPlayer());
                }));

        show();
    }
}
