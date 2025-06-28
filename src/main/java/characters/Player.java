package characters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import items.Item;
import items.consumables.Consumable;
import items.equip.Armor;
import items.equip.Weapon;
import lombok.Getter;
import lombok.Setter;
import spells.ElementalType;
import spells.SpellType;
import util.ItemRegistry;

import java.util.HashMap;
import java.util.Map;

import static characters.StatsType.*;

/**
 * Class: Player
 * <p>
 * Purpose:
 * Represents the player's character in the game.
 * Inherits from `Entity` and adds inventory, experience, gold, and progression logic.
 * Handles persistence via Jackson and runtime setup of spells, consumables, and items.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Player extends Entity {

    /* ── Runtime only ─────────────────────────── */

    /**
     * The stat that receives extra growth when leveling up
     */
    private final StatsType statBoost;

    /**
     * Experience points (XP) currently held
     */
    private int exp;

    /**
     * XP required for next level-up
     */
    @Setter
    private int expToLevelUp;

    /**
     * Gold carried by the player
     */
    @Setter
    private int gold;

    /**
     * Runtime inventory (ignored during JSON serialization).
     * Key: Item instance, Value: quantity.
     */
    @JsonIgnore
    private final Map<Item, Integer> inventory = new HashMap<>();

    /**
     * Persisted version of inventory.
     * Key: item ID, Value: quantity.
     */
    @JsonProperty("inventoryIds")
    private Map<String, Integer> inventoryIds = new HashMap<>();

    /* ── Constructors ─────────────────────────── */

    /**
     * Constructor for new games.
     *
     * @param name      Player name
     * @param statBoost Stat that receives extra points on level up
     * @param weakness  Elemental weakness (e.g., fire, ice)
     */
    public Player(String name, StatsType statBoost, ElementalType weakness) {
        super(name);
        this.statBoost = statBoost;
        this.elementalWeakness = weakness;
        this.gold = 100;
        this.expToLevelUp = 100;

        // Apply stat bonus
        if (statBoost == STRENGTH) modifyStat(STRENGTH, 100);
        if (statBoost == INTELLIGENCE) modifyStat(INTELLIGENCE, 5);

        // Equip starter gear
        setWeapon((Weapon) ItemRegistry.getItemById("iron_sword"));
        setArmor((Armor) ItemRegistry.getItemById("leather_armor"));
    }

    /**
     * Constructor used for deserialization (Jackson).
     */
    @JsonCreator
    public Player(@JsonProperty("name") String name,
                  @JsonProperty("statBoost") StatsType statBoost,
                  @JsonProperty("elementalWeakness") ElementalType weakness,
                  @JsonProperty("level") int level,
                  @JsonProperty("gold") int gold,
                  @JsonProperty("exp") int exp,
                  @JsonProperty("expToLevelUp") int expToLevelUp,
                  @JsonProperty("equippedSpellTypes") SpellType[] spellIds,
                  @JsonProperty("equippedConsumableIds") String[] consIds,
                  @JsonProperty("inventoryIds") Map<String, Integer> ids) {
        super(name);
        this.statBoost = statBoost;
        this.elementalWeakness = weakness;
        this.level = level;
        this.gold = gold;
        this.exp = exp;
        this.expToLevelUp = expToLevelUp;

        // Restore inventory from ID map
        if (ids != null) {
            this.inventoryIds = ids;
            ids.forEach((id, qty) -> {
                Item it = ItemRegistry.getItemById(id);
                if (it != null) inventory.put(it, qty);
            });
        }

        // Restore spell and consumable identifiers into the superclass arrays.
        // These IDs will be used by rebuildSpellsFromIds() and rebuildConsumablesFromIds()
        // to reconstruct the actual Spell and Consumable objects from the registry.
        if (spellIds != null) System.arraycopy(spellIds, 0, super.equippedSpellTypes, 0, Math.min(spellIds.length, 3));
        if (consIds != null) System.arraycopy(consIds, 0, super.equippedConsumableIds, 0, Math.min(consIds.length, 3));
        rebuildSpellsFromIds();
        rebuildConsumablesFromIds();
    }

    /* ── Inventory helpers ────────────────────── */

    /**
     * Adds one of the given item to the inventory.
     */
    public void addItemToInventory(Item item) {
        inventory.merge(item, 1, Integer::sum);
        inventoryIds.merge(item.getId(), 1, Integer::sum);
    }

    /**
     * Removes one of the given item, or deletes entry if quantity reaches zero.
     */
    public void removeItemFromInventory(Item item) {
        inventory.computeIfPresent(item, (k, v) -> v > 1 ? v - 1 : null);
        inventoryIds.computeIfPresent(item.getId(), (k, v) -> v > 1 ? v - 1 : null);
    }

    /* ── Consumable slot override ─────────────── */

    @Override
    public void assignConsumableToSlot(Consumable c, int slot) {
        equipConsumable(slot, c);
    }

    /* ── Level / EXP logic ─────────────────────── */

    /**
     * Collects XP and triggers level-up(s) if enough XP is earned.
     */
    public void collectExp(int xp) {
        exp += xp;
        while (exp >= expToLevelUp) {
            exp -= expToLevelUp;
            levelUp();
        }
    }

    /**
     * Increases level, doubles required XP, and boosts all stats.
     * The selected statBoost gets a slightly higher bonus.
     */
    private void levelUp() {
        level++;
        expToLevelUp *= 2;
        getStats().forEach((t, v) -> {
            int inc = (statBoost == t) ? 5 : 4;
            getStats().put(t, v + inc);
        });
    }

    /* ── Gold management ───────────────────────── */

    public void collectGold(int g) {
        gold += g;
    }

    public void decreaseGold(int amt) {
        gold -= amt;
    }
}
