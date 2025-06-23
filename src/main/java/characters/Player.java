package characters;

import items.*;
import lombok.Getter;
import lombok.Setter;
import spells.SpellFactory;
import spells.SpellType;
import util.DeveloperLogger;

import java.util.HashMap;

import static spells.ElementalType.*;
import static characters.StatsType.*;

@Getter
public class Player extends Entity {
    private final StatsType statBoost;
    private int exp;
    @Setter
    private int gold;
    @Setter
    private int expToLevelUp;
    private final HashMap<Item, Integer> inventory = new HashMap<>();

    public Player(String name, StatsType statBoost) {
        super(name);
        this.statBoost = statBoost;
        this.level = 1;
        this.gold = 100;
        this.exp = 0;
        this.expToLevelUp = 100;

        switch (statBoost) {
            case STRENGTH -> {
                modifyStat(STRENGTH, 5);
                setElementalWeakness(NATURE);
            }
            case INTELLIGENCE -> {
                modifyStat(INTELLIGENCE, 5);
                setElementalWeakness(ICE);
            }
        }

        setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
    }

    @Override
    public void assignConsumableToSlot(Consumable consumable, int index) {
        if (getConsumablesEquipped()[index] != null) {
            addItemToInventory(getConsumablesEquipped()[index]);
        }
        getConsumablesEquipped()[index] = consumable;
    }

    public void levelUp() {
        level++;
        expToLevelUp *= 2;

        getStats().forEach((type, val) -> {
            int inc = (statBoost == type) ? 5 : 3;
            getStats().put(type, val + inc);   // ← ADD, not replace
        });
    }

    public void collectExp(int xp) {
        exp += xp;
        while (exp >= expToLevelUp) {
            exp -= expToLevelUp;
            levelUp();
        }
    }

    public void addItemToInventory(Item item) {
        inventory.merge(item, 1, Integer::sum); // adds 1 or initializes to 1
    }


    public void removeItemFromInventory(Item item) {
        int current = inventory.getOrDefault(item, 0);
        if (current <= 1) {
            inventory.remove(item);
        } else {
            inventory.put(item, current - 1);
        }
    }

}
