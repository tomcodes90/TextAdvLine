package items.equip;

import characters.StatsType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import items.Item;

import java.util.EnumMap;

/**
 * Item: Weapon
 * <p>
 * Represents an equippable weapon that deals physical or magical damage.
 * A weapon has a base damage and a stat multiplier (e.g., Strength or Intelligence)
 * that adds bonus damage based on the wielder's stats.
 */
public class Weapon extends Item {

    /**
     * Flat base damage of the weapon before stat scaling.
     */
    private final int damage;

    /**
     * The stat type that contributes to damage scaling (e.g., STRENGTH, INTELLIGENCE).
     */
    private final StatsType damageMultiplier;

    /**
     * Constructs a weapon with base damage and a scaling stat.
     *
     * @param id               Unique identifier for the weapon
     * @param name             Display name
     * @param description      Short tooltip or lore text
     * @param price            Shop value or gold worth
     * @param damage           Flat base damage
     * @param damageMultiplier Stat used to calculate bonus damage
     */
    @JsonCreator
    public Weapon(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("gold") int price,
            @JsonProperty("damage") int damage,
            @JsonProperty("damageMultiplier") StatsType damageMultiplier
    ) {
        super(id, name, description, price);
        this.damage = damage;
        this.damageMultiplier = damageMultiplier;
    }

    /**
     * Calculates effective damage using base + scaled bonus.
     *
     * @param stats The user's current stat map
     * @return Effective total damage
     */
    public int getEffectiveDamage(EnumMap<StatsType, Integer> stats) {
        return damage + stats.getOrDefault(damageMultiplier, 0);
    }

    public int getDamage() {
        return damage;
    }

    public StatsType getDamageMultiplier() {
        return damageMultiplier;
    }
}
