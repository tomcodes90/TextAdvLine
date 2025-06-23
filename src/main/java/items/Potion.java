package items;

import characters.Entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static characters.StatsType.HP;
import static characters.StatsType.MAX_HP;

public class Potion extends Consumable {

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

    @Override
    public void use(Entity entity) {
        int currentHp = entity.getStat(HP);
        int maxHp = entity.getStat(MAX_HP);
        int healedHp = Math.min(currentHp + pointsToApply, maxHp);
        entity.setStat(HP, healedHp);
    }
}
