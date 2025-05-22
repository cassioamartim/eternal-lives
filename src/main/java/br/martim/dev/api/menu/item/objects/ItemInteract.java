package br.martim.dev.api.menu.item.objects;

import org.bukkit.event.player.PlayerInteractEvent;

public interface ItemInteract {
    void handle(PlayerInteractEvent event);
}
