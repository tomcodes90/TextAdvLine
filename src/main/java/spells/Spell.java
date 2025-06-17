package spells;

import characters.ElementalType;

public class Spell {
    static public int spellsCreated;
    private final SpellType name;
    private final ElementalType element;
    private final int damage;
    private final int cooldown;

    Spell(SpellType name, ElementalType element, int damage, int cooldown) {
        this.name = name;
        this.element = element;
        this.damage = damage;
        this.cooldown = cooldown;
        spellsCreated++;
    }

public void printSpellDetails() {
    System.out.println("Name: " + name + "\nElement: " + element + "\nDamage: " + damage + "Cooldown: " + cooldown);
}
    public SpellType getName() {
        return name;
    }

    public ElementalType getElement() {
        return element;
    }

    public int getDamage() {
        return damage;
    }

    public int getCooldown() {
        return cooldown;
    }
}
