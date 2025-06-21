package characters;

import items.*;
import spells.Spell;
import spells.SpellFactory;
import spells.SpellType;

import java.util.List;
import java.util.Random;

public class EnemyFactory {

    /* ------------ presets ------------ */

    public static Enemy createGoblin() {
        Enemy goblin = new Enemy(
                "Goblin",
                StatsType.STRENGTH,
                1,
                List.of(ItemRegistry.getByName("potion_small")), // loot
                25,
                AIRole.BERSERKER
        );

        // Equip gear (cast from Item to specific class)
        goblin.setWeapon((Weapon) ItemRegistry.getByName("iron_sword"));
        goblin.setArmor((Armor) ItemRegistry.getByName("leather_armor"));

        // Equip spells

        return goblin;
    }

    public static Enemy createDarkMage() {
        Enemy mage = new Enemy(
                "Dark Mage",
                StatsType.INTELLIGENCE,
                2,
                List.of(ItemRegistry.getByName("mana_potion")),
                40,
                AIRole.MAGE
        );

        mage.setWeapon((Weapon) ItemRegistry.getByName("wooden_staff"));
        mage.setArmor((Armor) ItemRegistry.getByName("cloth_robe"));

        mage.getSpellsEquipped()[0] = SpellFactory.create(SpellType.ENERGYBLAST);
        mage.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);
        mage.getSpellsEquipped()[2] = SpellFactory.create(SpellType.CURESTATUS);

        return mage;
    }

    /* ------------ random helper ------------ */

    private static final Random RNG = new Random();

    public static Enemy createRandomEnemy() {
        return switch (RNG.nextInt(2)) {
            case 0 -> createGoblin();
            default -> createDarkMage();
        };
    }
}
