package items.equip;

import characters.StatsType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import items.Item;

import java.util.EnumMap;

public class Weapon extends Item {
    private final int damage;
    private final StatsType damageMultiplier;

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
