package missions;

import characters.*;
import items.Armor;
import items.Weapon;
import spells.SpellFactory;
import spells.SpellType;

import static characters.AIRole.BERSERKER;
import static characters.StatsType.*;

public final class TestScenario {

    private TestScenario() {
    }          // utility class – no instances

    /* -----------------------------------------------------------
       Factory helpers used by MainMenuUI or BattleUI
       ----------------------------------------------------------- */

    public static Player createPlayer() {
        Player player = new Player("Maicon", INTELLIGENCE);

        player.setWeapon(
                new Weapon("IRON_SWORD", "Iron Sword",
                        "A trusty iron sword", 20, STRENGTH)
        );
        player.setArmor(
                new Armor("IRON_SWORD", "Iron Sword",
                        "A trusty iron sword", 20)
        );
        player.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);

        return player;
    }

    public static Enemy createEnemy() {
        Enemy enemy = new Enemy("Frank", STRENGTH, 2, null, 10, BERSERKER);
        enemy.setWeapon(
                new Weapon("IRON_SWORD", "Iron Sword",
                        "A trusty iron sword", 20, STRENGTH)
        );
        enemy.setArmor(
                new Armor("IRON_SWORD", "Iron Sword",
                        "A trusty iron sword", 20)
        );
        return enemy;
    }
}
