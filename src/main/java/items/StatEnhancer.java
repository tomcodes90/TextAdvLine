package items;

import characters.Entity;
import characters.StatsType;
import characters.TemporaryStatBoost;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class StatEnhancer extends Consumable {
    private final StatsType statToBoost;
    private final int length;

    @JsonCreator
    public StatEnhancer(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("pointsToApply") int pointsToApply,
            @JsonProperty("statToBoost") StatsType statToBoost,
            @JsonProperty("length") int length
    ) {
        super(id, name, description, pointsToApply);
        this.statToBoost = statToBoost;
        this.length = length;
    }

    @Override
    public void use(Entity entity) {
        TemporaryStatBoost boost = new TemporaryStatBoost(entity, statToBoost, pointsToApply, length);
        entity.addTemporaryBoost(boost);
    }
}
