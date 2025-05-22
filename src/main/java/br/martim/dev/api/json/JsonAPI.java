package br.martim.dev.api.json;

import br.martim.dev.Eternal;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class JsonAPI {

    public static JsonArray getArrayFromResource(String resourcePath) {
        try (InputStream input = Eternal.class.getResourceAsStream(resourcePath)) {

            if (input == null) return null;

            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(input));

            return jsonElement.isJsonArray() ? jsonElement.getAsJsonArray() : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonObject getObjectFromResource(String resourcePath) {
        try (InputStream input = Eternal.class.getResourceAsStream(resourcePath)) {

            if (input == null)
                return null;

            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(input));

            return jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonArray getArrayFromResource(JavaPlugin plugin, String resourcePath) {
        File file = new File(plugin.getDataFolder(), resourcePath);

        if (!file.exists()) {
            try (InputStream resourceStream = plugin.getResource(resourcePath)) {
                file.getParentFile().mkdirs();

                if (resourceStream != null) {
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = resourceStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                } else {
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write("[]");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        try (Reader reader = new FileReader(file)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            return jsonElement.isJsonArray() ? jsonElement.getAsJsonArray() : new JsonArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean updateJsonArrayFile(JavaPlugin plugin, String resourcePath, JsonArray data) {
        File file = new File(plugin.getDataFolder(), resourcePath);

        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {

            writer.write(Eternal.getGson().toJson(data));
            writer.flush();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateJsonArrayObject(JavaPlugin plugin, String resourcePath, int index, JsonObject newData) {
        File file = new File(plugin.getDataFolder(), resourcePath);

        file.getParentFile().mkdirs();

        if (!file.exists()) return false;

        try (Reader reader = new FileReader(file)) {
            JsonElement element = JsonParser.parseReader(reader);

            if (!element.isJsonArray()) return false;

            JsonArray array = element.getAsJsonArray();

            if (index < 0 || index >= array.size()) return false;

            array.set(index, newData);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(Eternal.getGson().toJson(array));
                writer.flush();
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeObjectFromArray(JavaPlugin plugin, String resourcePath, int index) {
        File file = new File(plugin.getDataFolder(), resourcePath);

        file.getParentFile().mkdirs();

        if (!file.exists()) return false;

        try (Reader reader = new FileReader(file)) {
            JsonElement element = JsonParser.parseReader(reader);

            if (!element.isJsonArray()) return false;

            JsonArray array = element.getAsJsonArray();

            if (index < 0 || index >= array.size()) return false;

            array.remove(index);

            try (FileWriter writer = new FileWriter(file)) {

                writer.write(Eternal.getGson().toJson(array));
                writer.flush();

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static JsonObject getObjectFromResource(JavaPlugin plugin, String resourcePath) {
        File file = new File(plugin.getDataFolder(), resourcePath);

        if (!file.exists()) {
            try (InputStream resourceStream = plugin.getResource(resourcePath)) {
                file.getParentFile().mkdirs();

                if (resourceStream != null) {

                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = resourceStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                } else {

                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write("{}");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        try (Reader reader = new FileReader(file)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            return jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : new JsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean updateJsonObjectFile(JavaPlugin plugin, String resourcePath, JsonObject data) {
        File file = new File(plugin.getDataFolder(), resourcePath);

        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(Eternal.getGson().toJson(data));
            writer.flush();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}