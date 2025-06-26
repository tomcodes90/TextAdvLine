package items.equip;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import items.Item;

public class Armor extends Item {
    @JsonProperty("defensePoints")
    private final int defense;


    @JsonCreator
    public Armor(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("price") int price,
            @JsonProperty("description") String description,
            @JsonProperty("defensePoints") int defense) {
        super(id, name, description, price);
        this.defense = defense;
    }

    public int getDefensePoints() {
        return defense;
    }
}
