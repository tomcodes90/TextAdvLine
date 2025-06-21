package missions;

import characters.*;
import items.Armor;
import items.Book;
import items.ItemRegistry;
import items.Weapon;
import spells.SpellFactory;
import spells.SpellType;

import java.util.ArrayList;

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
        player.levelUp();
        player.levelUp();
        player.levelUp();
        player.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        player.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        player.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);

        return player;
    }

    public static Enemy createEnemy() {
        return EnemyFactory.createDarkMage();
    }
}
