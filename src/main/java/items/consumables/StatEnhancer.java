package items.consumables;

import characters.Entity;
import characters.StatsType;
import characters.TemporaryStatBoost;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Class: StatEnhancer
 * <p>
 * Purpose:
 * Represents a consumable that temporarily boosts one of the target's stats.
 * The boost has a limited duration (number of turns).
 * Internally applies a {@link TemporaryStatBoost} to the target entity.
 */
@Getter
public class StatEnhancer extends Consumable {

    /**
     * The stat to temporarily enhance (e.g., STRENGTH, DEFENSE)
     */
    private final StatsType statToBoost;

    /**
     * Number of turns the effect lasts
     */
    private final int length;

    /**
     * Constructor for JSON deserialization or manual creation.
     *
     * @param id            Unique identifier
     * @param name          Display name
     * @param description   Description shown in the UI
     * @param price         Gold value of the item
     * @param pointsToApply Amount of stat increase
     * @param statToBoost   The specific stat that will be boosted
     * @param length        Number of turns the boost lasts
     */
    @JsonCreator
    public StatEnhancer(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("gold") int price,
            @JsonProperty("pointsToApply") int pointsToApply,
            @JsonProperty("statToBoost") StatsType statToBoost,
            @JsonProperty("length") int length
    ) {
        super(id, name, description, pointsToApply, price);
        this.statToBoost = statToBoost;
        this.length = length;
    }

    /**
     * Applies a temporary stat boost to the given entity.
     * The effect is added to the entity's boost tracker and will expire automatically.
     *
     * @param entity Target to receive the boost
     */
    @Override
    public void use(Entity entity) {
        TemporaryStatBoost boost = new TemporaryStatBoost(entity, statToBoost, pointsToApply, length);
        entity.addTemporaryBoost(boost);
    }
}
