package br.martim.dev.api.shop.effect;

import br.martim.dev.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EffectData {

    private final String name;

    private final PotionEffectType type;
    private final Material icon;

    private final int duration, price;

    private final List<String> description;

    public EffectData(JsonObject data) {
        this.name = data.get("name").getAsString();

        this.type = PotionEffectType.getByName(data.get("type").getAsString());
        this.icon = data.has("icon") ? Material.matchMaterial(data.get("icon").getAsString()) : Material.BARRIER;

        this.duration = data.has("duration") ? data.get("duration").getAsInt() : Integer.MAX_VALUE;
        this.price = data.has("price") ? data.get("price").getAsInt() : 3;

        this.description = new ArrayList<>();

        // Taking data from the description, if there is
        if (data.has("description")) {
            JsonArray array = data.get("description").getAsJsonArray();

            if (array != null && array.isJsonArray())
                array.forEach(element -> this.description.add(Util.color(element.getAsString())));
        }
    }
}
