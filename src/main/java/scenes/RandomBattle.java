package scenes;

import battle.TurnManager;
import characters.EnemyFactory;
import characters.Player;

public class RandomBattle implements Scene {
    private final Player player;

    public RandomBattle(Player player) {
        this.player = player;
    }

    @Override
    public void enter() {
        // 1️⃣ Generate a random enemy
        var enemy = EnemyFactory.createRandomEnemy();

        // 2️⃣ Start the battle
        TurnManager tm = new TurnManager(player, enemy);
        tm.startBattle(player, enemy, () -> {
            // 3️⃣ On battle end, return to world hub
            SceneManager.get().switchTo(new WorldHub(?? needs gui));
        });
    }

    @Override
    public void handleInput() {
        // no manual input needed here
    }

    @Override
    public void exit() {
        // Optional: clean up if needed
    }
}
}
