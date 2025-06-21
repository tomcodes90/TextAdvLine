package items;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import util.DeveloperLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private static final Map<String, Item> items = new HashMap<>();

    public static void loadAllItems() {
        ObjectMapper mapper = new ObjectMapper();

        try (
                InputStream keyStream = ItemRegistry.class.getResourceAsStream("/items/key_items.json");
                InputStream equipStream = ItemRegistry.class.getResourceAsStream("/items/equipment.json");
                InputStream consumableStream = ItemRegistry.class.getResourceAsStream("/items/consumables.json")
        ) {
            // 🔑 KeyItems
            KeyItem[] keyArr = mapper.readValue(keyStream, KeyItem[].class);
            for (KeyItem k : keyArr) items.put(k.getName(), k);

            // 🛡️⚔️ Weapons & Armors
            JsonNode equipArray = mapper.readTree(equipStream);
            for (JsonNode node : equipArray) {
                String type = node.get("type").asText();
                Item item = switch (type) {
                    case "weapon" -> mapper.treeToValue(node, Weapon.class);
                    case "armor" -> mapper.treeToValue(node, Armor.class);
                    default -> throw new IllegalArgumentException("Unknown equipment type: " + type);
                };
                items.put(item.getName(), item);
            }

            // 💊 Potions & StatEnhancers
            JsonNode consArray = mapper.readTree(consumableStream);
            for (JsonNode node : consArray) {
                String type = node.get("type").asText();
                Item item = switch (type) {
                    case "potion" -> mapper.treeToValue(node, Potion.class);
                    case "statEnhancer" -> mapper.treeToValue(node, StatEnhancer.class);
                    default -> throw new IllegalArgumentException("Unknown consumable type: " + type);
                };
                items.put(item.getName(), item);
            }

            DeveloperLogger.log("✅ Loaded " + items.size() + " items from JSON.");
        } catch (IOException e) {
            DeveloperLogger.log("❌ Failed to load item JSON files: " + e.getMessage());
            throw new RuntimeException("Failed to load item JSON files", e);
        }
    }

    public static Item getByName(String name) {
        return items.get(name);
    }
}
