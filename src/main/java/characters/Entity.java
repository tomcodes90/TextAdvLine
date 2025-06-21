package characters;


import items.Armor;
import items.Consumable;
import items.Item;
import items.Weapon;
import lombok.*;
import spells.ElementalType;
import spells.Spell;
import spells.SpellType;

import java.util.*;

import static characters.StatsType.*;

@Getter
public abstract class Entity {
    private final String name;
    private final EnumMap<StatsType, Integer> stats = new EnumMap<>(StatsType.class);
    private final HashMap<Item, Integer> inventory = new HashMap<>();
    private final Consumable[] consumablesEquipped = new Consumable[3];
    private final Spell[] spellsEquipped = new Spell[3];
    private final List<TemporaryStatBoost> tempBoosts = new ArrayList<>();

    @Setter
    int level;
    @Setter
    Weapon weapon;
    @Setter
    Armor armor;
    @Setter
    boolean isAlive;
    @Setter
    ElementalType elementalWeakness;

    Entity(String name) {
        this.name = name;
        stats.put(MAX_HP, 100);
        stats.put(HP, 100);
        stats.put(STRENGTH, 10);
        stats.put(INTELLIGENCE, 10);
        stats.put(DEFENSE, 10);
        stats.put(SPEED, 10);
        this.isAlive = true;

    }

    public void addTemporaryBoost(TemporaryStatBoost boost) {
        tempBoosts.add(boost);
    }

    public void removeTemporaryBoost(TemporaryStatBoost boost) {
        tempBoosts.remove(boost);
    }

    public void tickStatusEffects() {
        new ArrayList<>(tempBoosts).forEach(TemporaryStatBoost::tick);
    }

    public abstract void assignConsumableToSlot(Consumable consumable, int index);

    // Spell selector for Player
    public Spell getEquippedSpell(SpellType type) {
        return Arrays.stream(spellsEquipped)
                .filter(s -> s != null && s.getName() == type)
                .findFirst()
                .orElse(null);
    }

    // Spell selector for Enemy
    public Spell getEquippedSpell(ElementalType element) {
        return Arrays.stream(spellsEquipped)
                .filter(s -> s != null && s.getElement() == element)
                .findFirst()
                .orElse(null);
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

    public boolean isAlive() {
        return getStat(StatsType.HP) > 0;
    }
}
