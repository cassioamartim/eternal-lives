package br.martim.dev.listener;

import br.martim.dev.api.menu.Menu;
import br.martim.dev.api.menu.item.Item;
import br.martim.dev.api.menu.item.objects.ItemClick;
import br.martim.dev.controller.MenuController;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MenuListener implements Listener {

    private void removeMenu(UUID uuid) {
        MenuController.remove(uuid);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeMenu(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        removeMenu(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerClickItem(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {

            Menu menu = MenuController.get(player.getUniqueId());

            if (menu != null) {

                if (event.getClickedInventory() == null || event.getCurrentItem() == null) {
                    event.setCancelled(true);
                    return;
                }

                Inventory inventory = event.getClickedInventory();

                if (inventory.equals(player.getInventory())) {
                    event.setCancelled(true);
                    return;
                }

                ClickType clickType = event.getClick();

                ItemStack currentItem = event.getCurrentItem(), cursor = event.getCursor();

                if ((!menu.isAllowClickItemWithQuantity() && clickType.equals(ClickType.RIGHT) && currentItem.getAmount() > 1)
                    || clickType.equals(ClickType.NUMBER_KEY) || clickType.name().contains("DROP")) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getRawSlot() > inventory.getSize()) {
                    event.setCancelled(false);
                    return;
                }

                if (menu.isProtectedSlot(event.getSlot())) {
                    event.setCancelled(true);
                    return;
                }

                if (clickType.name().startsWith("SHIFT") && !menu.isAllowShift()) {
                    event.setCancelled(true);
                    return;
                }

                if (cursor != null && !cursor.getType().equals(Material.AIR) && (menu.isProtectedSlot(event.getSlot()) || menu.isProtectedContent(currentItem))) {
                    event.setCancelled(true);
                    return;
                }

                Item item = menu.getContents().get(event.getSlot());

                if (item == null)
                    return;

                event.setCancelled(menu.isProtectedContent(event.getCurrentItem()) || !menu.isAllowClick());

                if (!menu.isAllowClick())
                    player.setItemOnCursor(null);

                /* Rodando o clickType */
                ItemClick itemClick = item.getClick();

                if (itemClick != null) itemClick.handle(event);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        int rawSlotLimit = event.getView().getTopInventory().getSize() + event.getView().getBottomInventory().getSize();

        for (int slot : event.getRawSlots()) {
            if (slot >= rawSlotLimit) {
                event.setCancelled(true);
                return;
            }
        }

        Menu menu = MenuController.get(player.getUniqueId());

        if (menu != null)
            event.setCancelled(!menu.isAllowDrag());
    }

    @EventHandler
    public void onInteractInventory(InventoryCloseEvent event) {
        InventoryView view = event.getView();

        removeMenu(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        if (event.hasItem()) {
            ItemStack stack = event.getItem();

            if (stack == null || stack.getType().equals(Material.AIR) || !stack.hasItemMeta()) return;
            if (!Item.exists(stack)) return;

            Item item = Item.convertItem(stack);

            if (event.getAction().name().startsWith("RIGHT") && item.getInteract() != null)
                item.getInteract().handle(event);
        }
    }
}