package characters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Getter
public class Player extends Entity {

    /* ── Runtime only ─────────────────────────── */
    private final StatsType statBoost;
    private int exp;
    @Setter
    private int gold;
    @Setter
    private int expToLevelUp;

    /**
     * Inventory at runtime (ignored in JSON)
     */
    @JsonIgnore
    private final Map<Item, Integer> inventory = new HashMap<>();

    /**
     * Persisted view: itemId → qty
     */
    @JsonProperty("inventoryIds")
    private Map<String, Integer> inventoryIds = new HashMap<>();

    /* ── Constructors ─────────────────────────── */

    /**
     * New-game constructor
     */
    public Player(String name, StatsType statBoost, ElementalType weakness) {
        super(name);
        this.statBoost = statBoost;
        this.elementalWeakness = weakness;
        this.gold = 100;
        this.expToLevelUp = 100;

        if (statBoost == STRENGTH) modifyStat(STRENGTH, 100);
        if (statBoost == INTELLIGENCE) modifyStat(INTELLIGENCE, 5);

        setWeapon((Weapon) ItemRegistry.getItemById("iron_sword"));
        setArmor((Armor) ItemRegistry.getItemById("leather_armor"));
    }

    /**
     * Jackson constructor
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

        /* restore inventory */
        if (ids != null) {
            this.inventoryIds = ids;
            ids.forEach((id, qty) -> {
                Item it = ItemRegistry.getItemById(id);
                if (it != null) inventory.put(it, qty);
            });
        }
        /* restore spells & consumables */
        if (spellIds != null) System.arraycopy(spellIds, 0, super.equippedSpellTypes, 0, Math.min(spellIds.length, 3));
        if (consIds != null) System.arraycopy(consIds, 0, super.equippedConsumableIds, 0, Math.min(consIds.length, 3));
        rebuildSpellsFromIds();
        rebuildConsumablesFromIds();
    }

    @JsonProperty("inventoryIds")
    public Map<String, Integer> getInventoryIds() {
        // Always rebuild from current runtime inventory
        Map<String, Integer> synced = new HashMap<>();
        inventory.forEach((item, qty) -> synced.put(item.getId(), qty));
        this.inventoryIds = synced;
        return synced;
    }

    /* ── Inventory helpers ────────────────────── */
    public void addItemToInventory(Item item) {
        inventory.merge(item, 1, Integer::sum);
        inventoryIds.merge(item.getId(), 1, Integer::sum);
    }

    public void removeItemFromInventory(Item item) {
        inventory.computeIfPresent(item, (k, v) -> v > 1 ? v - 1 : null);
        inventoryIds.computeIfPresent(item.getId(), (k, v) -> v > 1 ? v - 1 : null);
    }

    public boolean hasItem(String itemName) {
        Item it = ItemRegistry.getByName(itemName);
        return it != null && inventory.containsKey(it);
    }

    /* ── Consumable equip slot ─────────────────── */
    @Override
    public void assignConsumableToSlot(Consumable c, int slot) {
        equipConsumable(slot, c);
    }

    /* ── Level / EXP logic ─────────────────────── */
    public void collectExp(int xp) {
        exp += xp;
        while (exp >= expToLevelUp) {
            exp -= expToLevelUp;
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        expToLevelUp *= 2;
        getStats().forEach((t, v) -> {
            int inc = (statBoost == t) ? 5 : 4;
            getStats().put(t, v + inc);
        });
    }

    public void collectGold(int g) {
        gold += g;
    }

    public void decreaseGold(int amt) {
        gold -= amt;
    }
}
