package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import items.*;
import items.consumables.Potion;
import items.consumables.StatEnhancer;
import items.equip.Armor;
import items.equip.Weapon;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ItemRegistry {

    /* name  -> item (for UI) */
    @Getter
    private static final Map<String, Item> ITEMS_BY_NAME = new HashMap<>();

    /* id -> item (for save/load) */
    @Getter
    private static final Map<String, Item> ITEMS_BY_ID = new HashMap<>();

    public static void loadAllItems() {
        ObjectMapper mapper = new ObjectMapper();

        try (
                InputStream equipStream = ItemRegistry.class.getResourceAsStream("/items/equipment.json");
                InputStream consStream = ItemRegistry.class.getResourceAsStream("/items/consumables.json");
                InputStream booksStream = ItemRegistry.class.getResourceAsStream("/items/books.json")) {

            /* helper that stores an item in both maps */
            java.util.function.Consumer<Item> store = it -> {
                ITEMS_BY_NAME.put(it.getName(), it);
                ITEMS_BY_ID.put(it.getId(), it);
            };


            /* 📚 books */
            for (Book b : mapper.readValue(booksStream, Book[].class)) store.accept(b);

            /* 🛡⚔ equipment */
            for (JsonNode raw : mapper.readTree(equipStream)) {
                ObjectNode node = (ObjectNode) raw;
                String type = node.remove("type").asText();
                Item item = switch (type) {
                    case "weapon" -> mapper.treeToValue(node, Weapon.class);
                    case "armor" -> mapper.treeToValue(node, Armor.class);
                    default -> throw new IllegalArgumentException("Unknown equipment type: " + type);
                };
                store.accept(item);
            }

            /* 💊 consumables */
            for (JsonNode raw : mapper.readTree(consStream)) {
                ObjectNode node = (ObjectNode) raw;
                String type = node.remove("type").asText();
                Item item = switch (type) {
                    case "potion" -> mapper.treeToValue(node, Potion.class);
                    case "statEnhancer" -> mapper.treeToValue(node, StatEnhancer.class);
                    default -> throw new IllegalArgumentException("Unknown consumable type: " + type);
                };
                store.accept(item);
            }

            DeveloperLogger.log("✅ Loaded " + ITEMS_BY_ID.size() + " items.");

        } catch (IOException e) {
            DeveloperLogger.log("❌ Failed to load item JSON files: " + e.getMessage());
            throw new RuntimeException("Failed to load item JSON files", e);
        }
    }

    /* ---------- look-ups ---------- */

    public static Item getByName(String name) {
        return ITEMS_BY_NAME.get(name);
    }

    public static Item getItemById(String id) {
        return ITEMS_BY_ID.get(id);
    }

    public static Collection<Item> getAllItems() {
        return ITEMS_BY_ID.values();
    }

    private ItemRegistry() { /* utility */ }
}


