package characters;

import items.Consumable;
import items.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

import static spells.ElementalType.*;
import static characters.StatsType.*;

@Getter
public class Player extends Entity {
    private final StatsType statBoost;
    private int exp;
    @Setter
    private int expToLevelUp;
    private final HashMap<Item, Integer> inventory = new HashMap<>();

    public Player(String name, StatsType statBoost) {
        super(name);
        this.statBoost = statBoost;
        this.level = 1;
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
            case DEFENSE -> {
                modifyStat(DEFENSE, 5);
                setElementalWeakness(FIRE);
            }
        }
    }

    @Override
    public void assignConsumableToSlot(Consumable consumable, int index) {
        if (getConsumablesEquipped()[index] != null) {
            addItemToInventory(getConsumablesEquipped()[index]);
        }
        getConsumablesEquipped()[index] = consumable;
    }

    public void levelUp() {
        this.level++;
        this.expToLevelUp *= 2;

        getStats().forEach((statType, value) -> {
            int increase = (statBoost == statType) ? 5 : 2;
            getStats().put(statType, +increase);
        });
    }

    public void collectExp(int expCollected) {
        this.exp += expCollected;
        if (this.exp >= this.expToLevelUp) {
            levelUp();
        }
    }

    public void addItemToInventory(Item item) {
        inventory.put(item, +inventory.get(item));
    }

    public void removeItemFromInventory(Item item) {
        inventory.put(item, -inventory.get(item));
        if (inventory.get(item) == 0) {
            inventory.remove(item);
        }
    }
}
