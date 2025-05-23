package br.martim.dev.menu;

import br.martim.dev.Eternal;
import br.martim.dev.api.config.ConfigAPI;
import br.martim.dev.api.menu.Menu;
import br.martim.dev.api.menu.item.Item;
import br.martim.dev.api.menu.sound.MenuSound;
import br.martim.dev.api.shop.Shop;
import br.martim.dev.api.shop.effect.EffectData;
import br.martim.dev.api.user.User;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

import static br.martim.dev.api.config.ConfigAPI.getMessage;

public class ShopMenu extends Menu {

    private final User user;

    private int id;

    public ShopMenu(Player player, int id) {
        super(player, "§9§lLifeShop (Page " + id + ")", 6, 9);

        this.user = Eternal.getUserController().load(player.getUniqueId());

        this.id = id;
    }

    @Override
    public void build() {
        clear();

        setTitle("§9§lLifeShop (Page " + id + ")");

        boolean canBuy = id == 1
                ? user.getLives() >= 7
                : user.getLives() >= 15;

        Shop shop = Eternal.getShopController().find(id);

        for (int i = 0; i < getTotalSlots(); i++)
            add(i, Item.of(Material.BLACK_STAINED_GLASS_PANE, "§r"));

        List<EffectData> effects = shop.getEffects();

        if (effects.isEmpty())
            addErrorButton(22, "§cThere are no added effects.");
        else
            pageCentered(effects, (effect, slot) -> {

                boolean canBuyEffect = user.getLives() >= effect.getPrice();

                List<String> lore = new ArrayList<>(List.of(
                        ""
                ));

                if (!effect.getDescription().isEmpty()) {
                    lore.addAll(effect.getDescription());
                    lore.add("");
                }

                lore.addAll(
                        List.of(
                                "§7Lives to buy: §c" + effect.getPrice() + ConfigAPI.getHeartSymbol(),
                                "",
                                !canBuy ? "§cYou cannot buy effects."
                                        : !canBuyEffect ? "§cYou don't have enough lives."
                                        : "§eClick to buy!"
                        )
                );

                Item icon = Item.of(effect.getIcon(), "§a" + effect.getName(), lore);

                if (effect.isEnchanted())
                    icon.enchantment(Enchantment.PROTECTION, 1);

                add(slot, icon.flags(ItemFlag.values())
                        .click(event -> {

                            if (!canBuy) {
                                sound(MenuSound.ERROR);

                                getPlayer().sendMessage(getMessage("you-cant-buy-effects"));
                                return;
                            }

                            if (!canBuyEffect) {
                                sound(MenuSound.ERROR);

                                getPlayer().sendMessage(getMessage("you-cant-buy-effect-data"));
                                return;
                            }

                            new ConfirmBuyEffectMenu(getPlayer(), effect, this).build();
                        }));
            });

        if (user.getLives() <= 15)
            add(getTotalSlots() - 5, Item.of(Material.BARRIER, "§cYou still can't see the next store."));
        else {
            if (id == 1)
                add(getTotalSlots() - 5, Item.of(Material.CROSSBOW, "§aStore 2",
                                "§eClick to see.")
                        .click(event -> {
                            this.id = 2;

                            sound(MenuSound.MENU_CHANGE);
                            build();
                        }));
            else
                add(getTotalSlots() - 5, Item.of(Material.ARROW, "§cBack to Store 1",
                                "§eClick to see.")
                        .click(event -> {
                            this.id = 1;

                            sound(MenuSound.MENU_CHANGE);
                            build();
                        }));
        }

        show();
    }
}
