package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ==========================================
 * PortraitRegistry
 * ==========================================
 * <p>
 * This utility class loads and manages ASCII art portraits for characters.
 * The portraits are defined in a JSON file located at `/resources/images/portraits.json`.
 * <p>
 * Portraits are stored as string arrays and can be retrieved by their ID.
 * Example usage:
 * String[] portrait = PortraitRegistry.get("nonna");
 * if (portrait != null) display(portrait);
 * <p>
 * Expected JSON structure:
 * {
 * "hero": [
 * "       ",
 * " ( -_- )",
 * "  /| |\\"
 * ],
 * "nonna": [
 * "    ",
 * " (^_^)",
 * "  \\_/ "
 * ]
 * }
 */
public final class PortraitRegistry {

    // Global map of all portraits, accessible via lowercase ID
    @Getter
    private static final Map<String, String[]> portraits = new HashMap<>();

    /**
     * Loads all portraits from the JSON file in the resources' folder.
     * This should be called at game startup.
     */
    public static void loadAllPortraits() {
        ObjectMapper mapper = new ObjectMapper(); // JSON parser

        // Open the JSON file from the classpath (/resources/images/portraits.json)
        try (InputStream stream = PortraitRegistry.class.getResourceAsStream("/images/portraits.json")) {
            if (stream == null) {
                DeveloperLogger.log("portraits.json not found in resources!"); // Log if file is missing
                return;
            }

            // Parse the JSON structure
            JsonNode root = mapper.readTree(stream);

            // Iterate through each field (e.g., "hero", "nonna")
            for (Iterator<Map.Entry<String, JsonNode>> it = root.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                String id = entry.getKey().toLowerCase(); // Normalize ID to lowercase
                JsonNode lines = entry.getValue(); // Get array of portrait lines

                // If the value is a list of strings, parse it into a String[]
                if (lines.isArray()) {
                    String[] portrait = new String[lines.size()];
                    for (int i = 0; i < lines.size(); i++) {
                        portrait[i] = lines.get(i).asText(); // Read each line as string
                    }
                    portraits.put(id, portrait); // Store in the global map
                }
            }

            DeveloperLogger.log("Loaded " + portraits.size() + " portraits."); // Confirm load success

        } catch (IOException e) {
            // Handle parse or IO errors
            DeveloperLogger.log("Failed to load portraits.json: " + e.getMessage());
            throw new RuntimeException("Failed to load portraits", e);
        }
    }

    /**
     * Returns a portrait array for the given character ID.
     * If not found, returns null.
     *
     * @param id Character name (case-insensitive)
     */
    public static String[] get(String id) {
        return portraits.getOrDefault(id.toLowerCase(), null);
    }

    // Prevent instantiation – this is a static utility class
    private PortraitRegistry() {
    }
}
