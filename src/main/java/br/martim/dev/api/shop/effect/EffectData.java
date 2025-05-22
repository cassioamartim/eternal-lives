package br.martim.dev.api.shop.effect;

import br.martim.dev.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

import static br.martim.dev.api.config.ConfigAPI.getMessage;

@Getter
public class EffectData {

    private final String name;

    private final PotionEffectType type;
    private final Material icon;

    private final int amplifier, duration, price;

    private final List<String> description;

    public EffectData(JsonObject data) {
        this.name = data.get("name").getAsString();

        this.type = PotionEffectType.getByName(data.get("type").getAsString());

        Material material = Material.matchMaterial(data.get("icon").getAsString());

        if (material == null)
            material = Material.BARRIER;

        this.icon = material;

        this.amplifier = data.has("amplifier") ? data.get("amplifier").getAsInt() : 0;
        this.duration = data.has("duration") ? data.get("duration").getAsInt() : Integer.MAX_VALUE;

        this.price = data.has("necessary-life") ? data.get("necessary-life").getAsInt() : 3;

        this.description = new ArrayList<>();

        // Taking data from the description, if there is
        if (data.has("description")) {
            JsonArray array = data.get("description").getAsJsonArray();

            if (array != null && array.isJsonArray())
                array.forEach(element -> this.description.add(Util.color(element.getAsString())));
        }
    }

    public void apply(Player player) {

        PotionEffect effect = new PotionEffect(type, duration, amplifier);

        player.addPotionEffect(effect);

        player.sendMessage(
                getMessage("applied-effect-target")
                        .replace("{effect}", name)
        );
    }
}
