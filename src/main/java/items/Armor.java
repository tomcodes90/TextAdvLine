package items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Armor extends Item {
    private final int defense;

    @JsonCreator
    public Armor(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("defense") int defense) {
        super(id, name, description);
        this.defense = defense;
    }

    public int getDefensePoints() {
        return defense;
    }
}
