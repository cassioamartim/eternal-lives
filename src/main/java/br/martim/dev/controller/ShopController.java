package br.martim.dev.controller;

import br.martim.dev.api.json.JsonAPI;
import br.martim.dev.api.shop.Shop;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ShopController {

    private final JavaPlugin plugin;

    private final Map<Integer, Shop> shopMap = new HashMap<>();

    public void handle() {

        JsonArray shops = this.getShopsJson();

        if (shops != null && shops.isJsonArray()) {

            shopMap.clear();

            for (JsonElement element : shops) {

                if (!element.isJsonObject()) continue;

                Shop shop = new Shop(element.getAsJsonObject());

                shopMap.put(shop.getId(), shop);
            }
        }
    }

    public void disable() {
        shopMap.clear();
    }

    public JsonArray getShopsJson() {
        return JsonAPI.getArrayFromResource(plugin, "shops.json");
    }

    public Shop find(int id) {
        return shopMap.get(id);
    }

    public List<Shop> find() {
        return shopMap.values().stream().toList();
    }
}
