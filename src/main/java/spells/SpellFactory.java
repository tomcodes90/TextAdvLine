package spells;

import java.util.EnumMap;
import java.util.Map;

public class SpellFactory {
    private static final Map<SpellType, Spell> template = new EnumMap<>(SpellType.class);

    static {
        // Non-elemental
        template.put(SpellType.ENERGYBLAST, new Spell(SpellType.ENERGYBLAST, ElementalType.NONE, 10, 2));
        template.put(SpellType.FLARE, new Spell(SpellType.FLARE, ElementalType.NONE, 6, 3));

        // Fire
        template.put(SpellType.FIREBALL, new Spell(SpellType.FIREBALL, ElementalType.FIRE, 12, 2));
        template.put(SpellType.INFERNO, new Spell(SpellType.INFERNO, ElementalType.FIRE, 20, 3));

        // Ice
        template.put(SpellType.FROSTBITE, new Spell(SpellType.FROSTBITE, ElementalType.ICE, 8, 2));
        template.put(SpellType.GLACIALSPIKE, new Spell(SpellType.GLACIALSPIKE, ElementalType.ICE, 16, 3));

        // Nature
        template.put(SpellType.VINEWHIP, new Spell(SpellType.VINEWHIP, ElementalType.NATURE, 7, 1));
        template.put(SpellType.THORNSURGE, new Spell(SpellType.THORNSURGE, ElementalType.NATURE, 15, 2));

        // Healing / Utility
        template.put(SpellType.HEAL, new Spell(SpellType.HEAL, ElementalType.NONE, 0, 2));
        template.put(SpellType.CURESTATUS, new Spell(SpellType.CURESTATUS, ElementalType.NONE, 0, 3));
    }

    /**
     * Returns a *new* Spell instance (no shared state).
     */
    public static Spell create(SpellType type) {
        Spell base = template.get(type);
        if (base == null)
            throw new IllegalArgumentException("Unknown spell type: " + type);
        return Spell.copyOf(base);              // fresh copy
    }

}
