package items;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Lombok;
import util.DeveloperLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ItemRegistry {
   @Getter
    private static final Map<String, Item> ITEMS = new HashMap<>();

    public static void loadAllItems() {
        ObjectMapper mapper = new ObjectMapper();

        try (
                InputStream keyStream = ItemRegistry.class.getResourceAsStream("/items/key_items.json");
                InputStream equipStream = ItemRegistry.class.getResourceAsStream("/items/equipment.json");
                InputStream consStream = ItemRegistry.class.getResourceAsStream("/items/consumables.json");
                InputStream booksStream = ItemRegistry.class.getResourceAsStream("/items/books.json")  // 🆕
        ) {
            /* ---------- 🔑 Key items ------------------------------ */
            for (KeyItem k : mapper.readValue(keyStream, KeyItem[].class)) {
                ITEMS.put(k.getName(), k);
            }

            /* ---------- 📚 Spell books ---------------------------- */
            for (Book book : mapper.readValue(booksStream, Book[].class)) {
                ITEMS.put(book.getName(), book);
            }

            /* ---------- 🛡️ / ⚔️ Equipment ------------------------ */
            for (JsonNode raw : mapper.readTree(equipStream)) {
                ObjectNode node = (ObjectNode) raw;
                String type = node.remove("type").asText();
                Item item = switch (type) {
                    case "weapon" -> mapper.treeToValue(node, Weapon.class);
                    case "armor" -> mapper.treeToValue(node, Armor.class);
                    default -> throw new IllegalArgumentException("Unknown equipment type: " + type);
                };
                ITEMS.put(item.getName(), item);
            }

            /* ---------- 💊 Consumables --------------------------- */
            for (JsonNode raw : mapper.readTree(consStream)) {
                ObjectNode node = (ObjectNode) raw;
                String type = node.remove("type").asText();
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

    public static Collection<Item> getAllItems() {
        return ITEMS.values(); // Returns all registered items
    }


    private ItemRegistry() { /* utility class – no instances */ }
}
