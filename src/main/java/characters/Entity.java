package characters;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * Base class for Player & Enemy
 */
@Getter
public abstract class Entity {

    /* ── Core fields ───────────────────────────────────────────── */
    private final String name;
    private final EnumMap<StatsType, Integer> stats = new EnumMap<>(StatsType.class);

    /* Runtime spell & consumable objects (Jackson IGNORE) */
    @JsonIgnore
    protected final Spell[] spellsEquipped = new Spell[3];
    @JsonIgnore
    protected final Consumable[] consumablesEquipped = new Consumable[3];

    /* IDs persisted in JSON */
    @JsonProperty("equippedSpellTypes")
    protected final SpellType[] equippedSpellTypes = new SpellType[3];
    @JsonProperty("equippedConsumableIds")
    protected final String[] equippedConsumableIds = new String[3];

    private final List<TemporaryStatBoost> tempBoosts = new ArrayList<>();

    @Setter
    protected int level = 1;
    @Setter
    protected Weapon weapon;
    @Setter
    protected Armor armor;
    @Setter
    protected boolean isAlive = true;
    @Setter
    protected ElementalType elementalWeakness = ElementalType.FIRE;

    /* ── Constructors ─────────────────────────────────────────── */
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
    public void equipSpell(int slot, SpellType type) {
        if (slot < 0 || slot >= spellsEquipped.length) return;
        equippedSpellTypes[slot] = type;
        spellsEquipped[slot] = (type != null) ? SpellFactory.create(type) : null;
    }

    public void rebuildSpellsFromIds() {
        for (int i = 0; i < equippedSpellTypes.length; i++) {
            SpellType t = equippedSpellTypes[i];
            spellsEquipped[i] = (t != null) ? SpellFactory.create(t) : null;
        }
    }

    /* ── Consumable helpers ───────────────────────────────────── */
    public void equipConsumable(int slot, Consumable c) {
        if (slot < 0 || slot >= consumablesEquipped.length) return;
        consumablesEquipped[slot] = c;
        equippedConsumableIds[slot] = (c != null) ? c.getId() : null;
    }

    public void rebuildConsumablesFromIds() {
        for (int i = 0; i < equippedConsumableIds.length; i++) {
            String id = equippedConsumableIds[i];
            consumablesEquipped[i] = (id != null) ? (Consumable) ItemRegistry.getItemById(id) : null;
        }
    }

    public abstract void assignConsumableToSlot(Consumable consumable, int slot);

    /* ── Quick selectors ─────────────────────────────────────── */
    public Spell getEquippedSpell(SpellType type) {
        return Arrays.stream(spellsEquipped)
                .filter(s -> s != null && s.getName() == type)
                .findFirst().orElse(null);
    }

    public Spell getEquippedSpell(ElementalType elem) {
        return Arrays.stream(spellsEquipped)
                .filter(s -> s != null && s.getElement() == elem)
                .findFirst().orElse(null);
    }

    /* ── Stats & boosts ───────────────────────────────────────── */
    public void addTemporaryBoost(TemporaryStatBoost b) {
        tempBoosts.add(b);
    }

    public void removeTemporaryBoost(TemporaryStatBoost b) {
        tempBoosts.remove(b);
    }

    public void tickStatusEffects() {
        new ArrayList<>(tempBoosts).forEach(TemporaryStatBoost::tick);
    }

    public int getStat(StatsType t) {
        return stats.getOrDefault(t, 0);
    }

    public void setStat(StatsType t, int v) {
        stats.put(t, v);
    }

    public void modifyStat(StatsType t, int d) {
        stats.put(t, getStat(t) + d);
    }

    @JsonIgnore
    public int getEffectiveDefense() {
        return getStat(DEFENSE) + armor.getDefensePoints();
    }


    public boolean isAlive() {
        return getStat(HP) > 0;
    }
}
