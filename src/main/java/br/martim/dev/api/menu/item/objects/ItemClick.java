package br.martim.dev.api.menu.item.objects;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface ItemClick {
    void handle(InventoryClickEvent event);
}
