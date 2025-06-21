package items;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import util.DeveloperLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class ItemRegistry {

    private static final Map<String, Item> ITEMS = new HashMap<>();

    public static void loadAllItems() {
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream keyStream = ItemRegistry.class.getResourceAsStream("/items/key_items.json");
             InputStream equipStream = ItemRegistry.class.getResourceAsStream("/items/equipment.json");
             InputStream consStream = ItemRegistry.class.getResourceAsStream("/items/consumables.json")) {

            /* ---------- 🔑  Key items (no "type" field) ---------------- */
            for (KeyItem k : mapper.readValue(keyStream, KeyItem[].class)) {
                ITEMS.put(k.getName(), k);
            }

            /* ---------- 🛡️ / ⚔️  Equipment --------------------------- */
            for (JsonNode raw : mapper.readTree(equipStream)) {
                ObjectNode node = (ObjectNode) raw;            // make it mutable
                String type = node.remove("type").asText();    // ⚠️ strip "type" key
                Item item = switch (type) {
                    case "weapon" -> mapper.treeToValue(node, Weapon.class);
                    case "armor" -> mapper.treeToValue(node, Armor.class);
                    default -> throw new IllegalArgumentException("Unknown equipment type: " + type);
                };
                ITEMS.put(item.getName(), item);
            }

            /* ---------- 💊  Consumables ------------------------------ */
            for (JsonNode raw : mapper.readTree(consStream)) {
                ObjectNode node = (ObjectNode) raw;
                String type = node.remove("type").asText();    // ⚠️ strip "type" key
                Item item = switch (type) {
                    case "potion" -> mapper.treeToValue(node, Potion.class);
                    case "statEnhancer" -> mapper.treeToValue(node, StatEnhancer.class);
                    default -> throw new IllegalArgumentException("Unknown consumable type: " + type);
                };
                ITEMS.put(item.getName(), item);
            }

            DeveloperLogger.log("✅ Loaded " + ITEMS.size() + " items from JSON.");

        } catch (IOException e) {
            DeveloperLogger.log("❌ Failed to load item JSON files: " + e.getMessage());
            throw new RuntimeException("Failed to load item JSON files", e);
        }
    }

    public static Item getByName(String name) {
        return ITEMS.get(name);
    }

    private ItemRegistry() { /* utility class – no instances */ }
}
