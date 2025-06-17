package items;

import characters.StatsType;

import java.util.EnumMap;

public class Weapon extends Item {
    private final int damage;
    private final StatsType damageMultiplier;

   public Weapon(String id, String name, String description, int damage, StatsType damageMultiplier) {
        super(id, name, description);
       this.damage = damage;
       this.damageMultiplier = damageMultiplier;
   }

   public int getEffectiveDamage( EnumMap<StatsType, Integer> stats) {
       return damage + stats.get(damageMultiplier);
   }

    public int getDamage() {
        return damage;
    }

    public StatsType getDamageMultiplier() {
        return damageMultiplier;
    }
}
