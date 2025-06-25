package characters;

import items.*;
import items.equip.Armor;
import items.equip.Weapon;
import spells.SpellFactory;
import spells.SpellType;
import util.ItemRegistry;

import java.util.ArrayList;
import java.util.Random;

public class EnemyFactory {

    /* ------------ presets ------------ */
    public static Enemy createBandit() {
        Enemy bandit = new Enemy(
                "Limoniris",
                StatsType.INTELLIGENCE,
                0,
                new ArrayList<>(),
                40,
                40,
                AIRole.BERSERKER
        );

        bandit.getLootReward().add(ItemRegistry.getByName("Healing Potion"));
        bandit.getLootReward().add(ItemRegistry.getByName("Book of Energyblast"));
        bandit.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        bandit.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));

        return bandit;
    }

    public static Enemy createDarkMage() {
        Enemy mage = new Enemy(
                "Rigatonius",
                StatsType.INTELLIGENCE,
                1,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        mage.getLootReward().add(item);
        mage.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        mage.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        mage.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        mage.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return mage;
    }

    /* ------------ random helper ------------ */

    private static final Random RNG = new Random();

    public static Enemy createRandomEnemy() {
        return switch (RNG.nextInt(2)) {
            case 1 -> createBandit();
            default -> createDarkMage();
        };
    }
}
