package characters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import items.consumables.Consumable;
import items.equip.Armor;
import items.equip.Weapon;
import lombok.Getter;
import lombok.Setter;
import spells.ElementalType;
import spells.Spell;
import spells.SpellFactory;
import spells.SpellType;
import util.ItemRegistry;

import java.util.*;

import static characters.StatsType.*;

/**
 * Abstract class: Entity
 * <p>
 * Purpose:
 * Base class for both Player and Enemy characters in the game.
 * Stores core attributes like stats, equipment, spells, consumables, and handles
 * common logic such as stat manipulation, spell reconstruction, and boost management.
 * <p>
 * Serialized via Jackson for save/load functionality.
 */
@Getter
public abstract class Entity {
    @JsonIgnoreProperties(ignoreUnknown = true)
    /* ── Core fields ───────────────────────────────────────────── */

    /**
     * The name of the entity (Player or Enemy)
     */
    private final String name;

    /**
     * Core stats stored in an EnumMap for quick lookup
     */
    private final EnumMap<StatsType, Integer> stats = new EnumMap<>(StatsType.class);

    /**
     * Equipped spells (runtime only, not serialized)
     */
    @JsonIgnore
    protected final Spell[] spellsEquipped = new Spell[3];

    /**
     * Equipped consumables (runtime only, not serialized)
     */
    @JsonIgnore
    protected final Consumable[] consumablesEquipped = new Consumable[3];

    /**
     * Serialized spell types to rebuild spells after loading
     */
    @JsonProperty("equippedSpellTypes")
    protected final SpellType[] equippedSpellTypes = new SpellType[3];

    /**
     * Serialized consumable IDs to rebuild after loading
     */
    @JsonProperty("equippedConsumableIds")
    protected final String[] equippedConsumableIds = new String[3];

    /**
     * List of active temporary stat boosts (e.g., buffs)
     */
    private final List<TemporaryStatBoost> tempBoosts = new ArrayList<>();

    /**
     * Entity's current level
     */
    @Setter
    protected int level = 1;

    /**
     * Equipped weapon and armor
     */
    @Setter
    protected Weapon weapon;
    @Setter
    protected Armor armor;

    @Setter
    protected ElementalType elementalWeakness = ElementalType.FIRE;

    /* ── Constructors ─────────────────────────────────────────── */

    /**
     * Initializes an entity with default stats and name.
     * Default stats are placeholder values for HP, Strength, etc.
     */
    protected Entity(String name) {
        this.name = name;
        stats.put(MAX_HP, 100);
        stats.put(HP, 100);
        stats.put(STRENGTH, 10);
        stats.put(INTELLIGENCE, 10);
        stats.put(DEFENSE, 10);
        stats.put(SPEED, 10);
    }

    /* ── Spell helpers ────────────────────────────────────────── */

    /**
     * Equips a spell in a specific slot using a SpellType.
     * Also stores the spell type for persistence.
     */
    public void equipSpell(int slot, SpellType type) {
        if (slot < 0 || slot >= spellsEquipped.length) return;
        equippedSpellTypes[slot] = type;
        spellsEquipped[slot] = (type != null) ? SpellFactory.create(type) : null;
    }

    /**
     * Rebuilds spell objects from stored spell types (used after loading).
     */
    public void rebuildSpellsFromIds() {
        for (int i = 0; i < equippedSpellTypes.length; i++) {
            SpellType t = equippedSpellTypes[i];
            spellsEquipped[i] = (t != null) ? SpellFactory.create(t) : null;
        }
    }

    /* ── Consumable helpers ───────────────────────────────────── */

    /**
     * Equips a consumable in a specific slot and stores its ID.
     */
    public void equipConsumable(int slot, Consumable c) {
        if (slot < 0 || slot >= consumablesEquipped.length) return;
        consumablesEquipped[slot] = c;
        equippedConsumableIds[slot] = (c != null) ? c.getId() : null;
    }

    /**
     * Rebuilds consumables from stored IDs (used after loading).
     */
    public void rebuildConsumablesFromIds() {
        for (int i = 0; i < equippedConsumableIds.length; i++) {
            String id = equippedConsumableIds[i];
            consumablesEquipped[i] = (id != null) ? (Consumable) ItemRegistry.getItemById(id) : null;
        }
    }

    /**
     * Abstract method to allow subclasses to assign a consumable to a specific slot.
     * (Used for inventory menu logic.)
     */
    public abstract void assignConsumableToSlot(Consumable consumable, int slot);

    /* ── Quick selectors ─────────────────────────────────────── */

    /**
     * Returns the first equipped spell of the given SpellType, or null if not found.
     */
    public Spell getEquippedSpell(SpellType type) {
        return Arrays.stream(spellsEquipped)
                .filter(s -> s != null && s.getName() == type)
                .findFirst().orElse(null);
    }

    /**
     * Returns the first equipped spell matching the given element, or null if not found.
     */
    public Spell getEquippedSpell(ElementalType elem) {
        return Arrays.stream(spellsEquipped)
                .filter(s -> s != null && s.getElement() == elem)
                .findFirst().orElse(null);
    }

    /* ── Stats & boosts ───────────────────────────────────────── */

    /**
     * Adds a temporary stat boost (e.g., from potion or buff)
     */
    public void addTemporaryBoost(TemporaryStatBoost b) {
        tempBoosts.add(b);
    }

    /**
     * Removes a temporary boost (e.g., when expired)
     */
    public void removeTemporaryBoost(TemporaryStatBoost b) {
        tempBoosts.remove(b);
    }

    /**
     * Ticks down all temporary boosts by 1 turn (used after each round).
     */
    public void tickStatusEffects() {
        new ArrayList<>(tempBoosts).forEach(TemporaryStatBoost::tick);
    }

    /**
     * Returns the current value of the requested stat
     */
    public int getStat(StatsType t) {
        return stats.getOrDefault(t, 0);
    }

    /**
     * Sets a stat to a specific value
     */
    public void setStat(StatsType t, int v) {
        stats.put(t, v);
    }

    /**
     * Modifies a stat by a delta (+/-)
     */
    public void modifyStat(StatsType t, int d) {
        stats.put(t, getStat(t) + d);
    }

    /**
     * Returns the entity's defense including armor bonus.
     */
    @JsonIgnore
    public int getEffectiveDefense() {
        return getStat(DEFENSE) + armor.getDefensePoints();
    }

    /**
     * Runtime check if entity is alive (HP > 0).
     * Also used by the AI and combat system.
     */
    public boolean isAlive() {
        return getStat(HP) > 0;
    }
}
