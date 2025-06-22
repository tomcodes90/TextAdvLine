package game;

import characters.Enemy;
import characters.Player;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import missions.TestScenario;
import scenes.MainMenu;
import scenes.ui.battle.Battle;

public class GameLoopManager {
    private final MultiWindowTextGUI gui;

    public GameLoopManager(MultiWindowTextGUI gui) {
        this.gui = gui;
    }

    public void showMainMenu() {
        Runnable startBattle = () -> {
            Player player = TestScenario.createPlayer();
            Enemy enemy = TestScenario.createEnemy();

            Battle battle = new Battle(gui, player, enemy);
            battle.setOnBattleEnd(this::showMainMenu);
            battle.start();
        };

        MainMenu menu = new MainMenu(gui, startBattle);
        gui.addWindowAndWait(menu.build());
    }
}
