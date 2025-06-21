package items;

import characters.Entity;
import characters.StatsType;

import static characters.StatsType.HP;
import static characters.StatsType.MAX_HP;

public class Potion extends Consumable {
    public Potion(String id, String name, String description, int pointsToApply) {
        super(id, name, description, pointsToApply);
    }

    @Override
    public void use(Entity entity) {
        entity.setStat(HP, ++pointsToApply);
        if (entity.getStat(HP) <= entity.getStat(StatsType.MAX_HP)) {
            entity.setStat(HP, entity.getStat(MAX_HP));
        }
    }
}
