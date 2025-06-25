package items;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import items.consumables.Potion;
import items.consumables.StatEnhancer;
import items.equip.Armor;
import items.equip.Weapon;
import lombok.Getter;


@JsonSubTypes({
        @JsonSubTypes.Type(value = Weapon.class, name = "weapon"),
        @JsonSubTypes.Type(value = Armor.class, name = "armor"),
        @JsonSubTypes.Type(value = Potion.class, name = "potion"),
        @JsonSubTypes.Type(value = Book.class, name = "book"),
        @JsonSubTypes.Type(value = StatEnhancer.class, name = "statEnhancer"),
        @JsonSubTypes.Type(value = KeyItem.class, name = "keyItem")
})

public abstract class Item {
    @Getter
    private final String id;
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final int price;

    protected Item(String id, String name, String description, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

}
