package br.martim.dev.controller;

import br.martim.dev.Eternal;
import br.martim.dev.api.config.ConfigAPI;
import br.martim.dev.api.json.JsonAPI;
import br.martim.dev.api.user.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class UserController {

    private final JavaPlugin plugin;

    private final Map<UUID, User> userMap = Collections.synchronizedMap(new HashMap<>());

    public JsonArray getUserJson() {
        return JsonAPI.getArrayFromResource(plugin, "users.json");
    }

    public User load(UUID id) {
        User user = userMap.get(id);
        if (user != null) return user;

        user = new User(id);

        JsonArray users = getUserJson();
        boolean found = false;

        for (JsonElement element : users) {
            if (!element.isJsonObject()) continue;

            JsonObject data = element.getAsJsonObject();
            if (!data.has("id")) continue;

            UUID uuid = UUID.fromString(data.get("id").getAsString());
            if (!uuid.equals(id)) continue;

            int lives = data.get("lives").getAsInt();

            long createdAt = data.get("createdAt").getAsLong();
            long returnsAt = data.get("returnsAt").getAsLong();

            user.setLives(lives);
            user.setCreatedAt(createdAt);
            user.setReturnsAt(returnsAt);

            found = true;
            break;
        }

        if (!found) {
            user.setLives(ConfigAPI.getInitialLife());

            user.setCreatedAt(System.currentTimeMillis());
            user.setReturnsAt(-1L);

            users.add(Eternal.getGson().toJsonTree(user).getAsJsonObject());

            JsonAPI.updateJsonArrayFile(plugin, "users.json", users);
        }

        userMap.put(id, user);
        return user;
    }

    public void save(User user) {

        JsonArray users = getUserJson();

        if (users == null || !users.isJsonArray()) return;

        JsonObject data = Eternal.getGson().toJsonTree(user).getAsJsonObject();

        int index = 0;
        for (JsonElement element : users) {

            if (element.isJsonObject()) {

                JsonObject json = element.getAsJsonObject();

                if (json.has("id") && json.get("id").getAsString().equalsIgnoreCase(user.getId().toString())) {
                    users.set(index, data);
                    break;
                }
            }

            index++;
        }

        JsonAPI.updateJsonArrayFile(plugin, "users.json", users);
    }

    public void remove(UUID id) {
        userMap.remove(id);
    }

}
