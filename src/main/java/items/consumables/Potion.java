package items.consumables;

import characters.Entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static characters.StatsType.HP;
import static characters.StatsType.MAX_HP;

/**
 * Class: Potion
 * <p>
 * Purpose:
 * Represents a healing item that restores HP to the target entity.
 * Inherits from {@link Consumable} and implements the `use` method to apply healing.
 * <p>
 * Jackson Annotations:
 * - @JsonCreator and @JsonProperty allow deserialization from JSON.
 */
public class Potion extends Consumable {

    /**
     * Constructor for JSON deserialization and manual creation.
     *
     * @param id            Unique item ID
     * @param name          Display name
     * @param description   Short tooltip or inventory description
     * @param price         Value in gold
     * @param pointsToApply HP points restored when used
     */
    @JsonCreator
    public Potion(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("gold") int price,
            @JsonProperty("pointsToApply") int pointsToApply
    ) {
        super(id, name, description, pointsToApply, price);
    }

    /**
     * Applies the healing effect to the target entity.
     * Will not overheal beyond MAX_HP.
     *
     * @param entity The target to heal
     */
    @Override
    public void use(Entity entity) {
        int currentHp = entity.getStat(HP);
        int maxHp = entity.getStat(MAX_HP);
        int healedHp = Math.min(currentHp + pointsToApply, maxHp); // Clamp to max HP
        entity.setStat(HP, healedHp);
    }
}
