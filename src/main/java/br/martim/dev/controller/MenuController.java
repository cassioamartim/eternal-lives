package br.martim.dev.controller;

import br.martim.dev.api.menu.Menu;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuController {

    @Getter
    private static final Map<UUID, Menu> menuCache = Collections.synchronizedMap(new HashMap<>());

    public static void save(Menu menu) {
        menuCache.put(menu.getPlayer().getUniqueId(), menu);
    }

    public static void remove(UUID playerId) {
        menuCache.remove(playerId);
    }

    public static Menu get(UUID playerId) {
        return menuCache.get(playerId);
    }
}
