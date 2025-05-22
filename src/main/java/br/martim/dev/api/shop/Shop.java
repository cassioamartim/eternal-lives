package br.martim.dev.api.shop;

import br.martim.dev.api.shop.effect.EffectData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Shop {

    private final int id;

    private final List<EffectData> effects;

    public Shop(JsonObject data) {
        this.id = data.get("id").getAsInt();

        this.effects = new ArrayList<>();

        if (data.has("effects")) {
            JsonArray array = data.getAsJsonArray("effects");

            if (array != null && array.isJsonArray())
                array.forEach(element -> {
                    if (element.isJsonObject())
                        this.effects.add(new EffectData(element.getAsJsonObject()));
                });
        }
    }
}
