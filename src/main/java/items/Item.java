package items;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import items.consumables.Potion;
import items.consumables.StatEnhancer;
import items.equip.Armor;
import items.equip.Weapon;
import lombok.Getter;

/**
 * Abstract Class: Item
 * <p>
 * Purpose:
 * Serves as the base class for all in-game items.
 * Items can be weapons, armor, consumables, key items, etc.
 * This class defines shared properties and ensures consistent behavior across item types.
 * <p>
 * Fields:
 * - id: Unique identifier used for persistence and registry lookup.
 * - name: Display name of the item.
 * - description: Short explanation shown in the UI.
 * - price: Shop value or base worth of the item.
 * <p>
 * Serialization:
 * - Annotated with @JsonSubTypes to support proper deserialization of concrete item types.
 * - Allows saving/loading inventory with Jackson.
 */

// is essential if you’re saving a List<Item> — otherwise Jackson won’t know what subclass to instantiate.
@JsonSubTypes({
        @JsonSubTypes.Type(value = Weapon.class, name = "weapon"),
        @JsonSubTypes.Type(value = Armor.class, name = "armor"),
        @JsonSubTypes.Type(value = Potion.class, name = "potion"),
        @JsonSubTypes.Type(value = Book.class, name = "book"),
        @JsonSubTypes.Type(value = StatEnhancer.class, name = "statEnhancer")
})
public abstract class Item {

    /**
     * Unique item identifier (used in saves and item registry lookup)
     */
    @Getter
    private final String id;

    /**
     * Display name of the item shown to the player
     */
    @Getter
    private final String name;

    /**
     * Description used in inventory tooltips or item details
     */
    @Getter
    private final String description;

    /**
     * Price used for shops, rewards, or display (0 = unsellable)
     */
    @Getter
    private final int price;

    /**
     * Protected constructor used by subclasses.
     *
     * @param id          Unique ID string
     * @param name        Name shown to the player
     * @param description Description of the item
     * @param price       Value of the item
     */
    protected Item(String id, String name, String description, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
