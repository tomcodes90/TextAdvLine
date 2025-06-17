package characters;


import items.Armor;
import items.Consumable;
import items.Weapon;
import lombok.*;
import spells.Spell;
import spells.SpellType;

import java.util.EnumMap;

import static characters.StatsType.*;

@Getter
public abstract class Entity {

    protected final String name;

    @Setter protected int level;

    @Getter(AccessLevel.PROTECTED)
    protected final EnumMap<StatsType, Integer> stats = new EnumMap<>(StatsType.class);

    @Getter(AccessLevel.PROTECTED)
    protected final EnumMap<SpellType, Spell> spells = new EnumMap<>(SpellType.class);

    @Setter protected Weapon weapon;
    @Setter protected Armor armor;

    protected final Consumable[] consumables = new Consumable[3];

    @Setter protected boolean isAlive;
    @Setter protected ElementalType elementalWeakness;

    protected Entity(String name) {
        this.name = name;
        stats.put(MAX_HP, 100);
        stats.put(HP, 100);
        stats.put(STRENGTH, 10);
        stats.put(INTELLIGENCE, 10);
        stats.put(DEFENSE, 10);
        stats.put(SPEED, 10);
    }

    public abstract void assignConsumableToSlot(Consumable consumable, int index);

    public void attack(Entity target) {
        int damage = weapon.getEffectiveDamage(stats) - target.getEffectiveDefense();
        target.modifyStat(HP, -damage);
        if (target.getStat(HP) <= 0) {
            target.setAlive(false);
        }
    }

    public void castSpell(Spell spell, Entity target) {
        int baseDamage = spell.getDamage() + getStat(INTELLIGENCE);
        boolean isWeak = spell.getElement() == target.getElementalWeakness();
        int totalDamage = isWeak ? (int)(baseDamage * 1.25) : baseDamage;

        target.modifyStat(HP, -totalDamage);
        if (target.getStat(HP) <= 0) {
            target.setAlive(false);
        }
    }

    public int getEffectiveDefense() {
        return getStat(DEFENSE) + armor.getDefensePoints();
    }

    public int getStat(StatsType type) {
        return stats.getOrDefault(type, 0);
    }

    public void setStat(StatsType type, int value) {
        stats.put(type, value);
    }

    public void modifyStat(StatsType type, int delta) {
        stats.put(type, getStat(type) + delta);
    }
}
