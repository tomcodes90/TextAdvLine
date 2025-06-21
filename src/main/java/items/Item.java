package items;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Weapon.class, name = "weapon"),
        @JsonSubTypes.Type(value = Armor.class, name = "armor"),
        @JsonSubTypes.Type(value = Potion.class, name = "potion"),
        @JsonSubTypes.Type(value = Book.class, name = "book"),
        @JsonSubTypes.Type(value = StatEnhancer.class, name = "statEnhancer"),
        @JsonSubTypes.Type(value = KeyItem.class, name = "keyItem")
})

public abstract class Item {
    private final String id;
    private final String name;
    private final String description;

    Item(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
