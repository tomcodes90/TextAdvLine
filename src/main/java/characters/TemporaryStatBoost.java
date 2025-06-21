package characters;

import characters.Entity;
import characters.StatsType;

public class TemporaryStatBoost {
    private final StatsType stat;
    private final int boostAmount;
    private int turnsLeft;
    private final Entity entity;

    public TemporaryStatBoost(Entity entity, StatsType stat, int boostAmount, int duration) {
        this.entity = entity;
        this.stat = stat;
        this.boostAmount = boostAmount;
        this.turnsLeft = duration;
        applyBoost();
    }

    private void applyBoost() {
        entity.modifyStat(stat, boostAmount);
    }

    public void tick() {
        turnsLeft--;
        if (turnsLeft <= 0) {
            entity.modifyStat(stat, -boostAmount); // Revert boost
            entity.removeTemporaryBoost(this);     // Remove from list
        }
    }
}
