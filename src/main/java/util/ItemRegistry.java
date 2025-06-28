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

/**
 * ==========================================
 * ItemRegistry
 * ==========================================
 * <p>
 * Loads all game items (weapons, armor, potions, stat boosters, and spell books)
 * from JSON files and stores them in static maps for fast lookup.
 * <p>
 * Usage examples:
 * - Get item by ID for loading: `ItemRegistry.getItemById("steel_sword")`
 * - Get item by name for UI: `ItemRegistry.getByName("Steel Sword")`
 */
public final class ItemRegistry {

    // Used for UI display or dropdowns (name shown to players)
    @Getter
    private static final Map<String, Item> ITEMS_BY_NAME = new HashMap<>();

    // Used internally and for serialization/deserialization
    @Getter
    private static final Map<String, Item> ITEMS_BY_ID = new HashMap<>();

    /**
     * Loads all items from JSON files in the resources folder.
     * This should be called once on game startup.
     * <p>
     * Expected resource files:
     * - /items/equipment.json
     * - /items/consumables.json
     * - /items/books.json
     */
    public static void loadAllItems() {
        ObjectMapper mapper = new ObjectMapper(); // JSON parser

        try (
                InputStream equipStream = ItemRegistry.class.getResourceAsStream("/items/equipment.json");
                InputStream consStream = ItemRegistry.class.getResourceAsStream("/items/consumables.json");
                InputStream booksStream = ItemRegistry.class.getResourceAsStream("/items/books.json")
        ) {

            // Local helper to store an item in both maps
            java.util.function.Consumer<Item> store = it -> {
                ITEMS_BY_NAME.put(it.getName(), it); // for UI
                ITEMS_BY_ID.put(it.getId(), it);     // for save/load
            };

            // === 📚 Load all spell books ===
            for (Book b : mapper.readValue(booksStream, Book[].class)) {
                store.accept(b); // deserialize and store each book
            }

            // === 🛡⚔ Load all equipment ===
            for (JsonNode raw : mapper.readTree(equipStream)) {
                ObjectNode node = (ObjectNode) raw;
                String type = node.remove("type").asText(); // read and remove "type" field

                Item item = switch (type) {
                    case "weapon" -> mapper.treeToValue(node, Weapon.class);
                    case "armor" -> mapper.treeToValue(node, Armor.class);
                    default -> throw new IllegalArgumentException("Unknown equipment type: " + type);
                };

                store.accept(item); // add to registry
            }

            // === 💊 Load all consumables ===
            for (JsonNode raw : mapper.readTree(consStream)) {
                ObjectNode node = (ObjectNode) raw;
                String type = node.remove("type").asText(); // read and remove "type" field

                Item item = switch (type) {
                    case "potion" -> mapper.treeToValue(node, Potion.class);
                    case "statEnhancer" -> mapper.treeToValue(node, StatEnhancer.class);
                    default -> throw new IllegalArgumentException("Unknown consumable type: " + type);
                };

                store.accept(item); // add to registry
            }

            // Log success
            DeveloperLogger.log("Loaded " + ITEMS_BY_ID.size() + " items.");

        } catch (IOException e) {
            // If any file is missing or invalid JSON, crash with error message
            DeveloperLogger.log("Failed to load item JSON files: " + e.getMessage());
            throw new RuntimeException("Failed to load item JSON files", e);
        }
    }

    // === Lookup Methods ===

    /**
     * Retrieve item by name (used for UI or manual selection)
     */
    public static Item getByName(String name) {
        return ITEMS_BY_NAME.get(name);
    }

    /**
     * Retrieve item by ID (used in save files and inventory maps)
     */
    public static Item getItemById(String id) {
        return ITEMS_BY_ID.get(id);
    }

    /**
     * Get all loaded items (used for shop menus, debug lists, etc.)
     */
    public static Collection<Item> getAllItems() {
        return ITEMS_BY_ID.values();
    }

    // Prevent instantiation – this is a static utility class
    private ItemRegistry() {
    }
}
