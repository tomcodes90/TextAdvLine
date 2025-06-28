package items.equip;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import items.Item;

/**
 * Item: Armor
 * <p>
 * Represents a piece of armor that grants defensive power when equipped.
 * It extends the base Item class and adds defense stats.
 */
public class Armor extends Item {

    /**
     * The amount of defense this armor provides when equipped.
     */
    @JsonProperty("defensePoints")
    private final int defense;

    /**
     * Constructs an Armor item.
     *
     * @param id          Unique item ID
     * @param name        Name displayed to the player
     * @param price       Value used in shop systems
     * @param description Description tooltip
     * @param defense     Defense stat granted when equipped
     */
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

    /**
     * @return The defense value of the armor
     */
    public int getDefensePoints() {
        return defense;
    }
}
