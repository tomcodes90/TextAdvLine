package characters;

import items.*;
import spells.Spell;
import spells.SpellFactory;
import spells.SpellType;
import util.DeveloperLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyFactory {

    /* ------------ presets ------------ */

    public static Enemy createDarkMage() {
        Enemy mage = new Enemy(
                "Dark Mage",
                StatsType.INTELLIGENCE,
                2,
                new ArrayList<>(),
                40,
                AIRole.MAGE
        );
        ItemRegistry.loadAllItems();
        Item item = ItemRegistry.getByName("Healing Potion");
        mage.getLootReward().add(item);
        mage.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        mage.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        mage.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        mage.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);
        mage.getSpellsEquipped()[2] = SpellFactory.create(SpellType.THORNSURGE);

        return mage;
    }

    /* ------------ random helper ------------ */

    private static final Random RNG = new Random();

    public static Enemy createRandomEnemy() {
        return switch (RNG.nextInt(2)) {
            default -> createDarkMage();
        };
    }
}
