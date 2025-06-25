package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class PortraitRegistry {
    @Getter
    private static final Map<String, String[]> PORTRAITS = new HashMap<>();

    public static void
    loadAllPortraits() {
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream stream = PortraitRegistry.class.getResourceAsStream("/images/portraits.json")) {
            if (stream == null) {
                DeveloperLogger.log("❌ portraits.json not found in resources!");
                return;
            }

            JsonNode root = mapper.readTree(stream);
            for (Iterator<Map.Entry<String, JsonNode>> it = root.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                String id = entry.getKey().toLowerCase();
                JsonNode lines = entry.getValue();

                if (lines.isArray()) {
                    String[] portrait = new String[lines.size()];
                    for (int i = 0; i < lines.size(); i++) {
                        portrait[i] = lines.get(i).asText();
                    }
                    PORTRAITS.put(id, portrait);
                }
            }

            DeveloperLogger.log("✅ Loaded " + PORTRAITS.size() + " portraits.");
        } catch (IOException e) {
            DeveloperLogger.log("❌ Failed to load portraits.json: " + e.getMessage());
            throw new RuntimeException("Failed to load portraits", e);
        }
    }

    public static String[] get(String id) {
        return PORTRAITS.getOrDefault(id.toLowerCase(), new String[]{"(portrait missing)"});
    }

    public static boolean exists(String id) {
        return PORTRAITS.containsKey(id.toLowerCase());
    }

    private PortraitRegistry() { /* Utility class */ }
}
