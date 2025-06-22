package ui;

import characters.Enemy;
import characters.Player;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import missions.TestScenario;
import scenes.MainMenu;
import ui.battle.BattleUI;

public class GameLoopManager {
    private final MultiWindowTextGUI gui;

    public GameLoopManager(MultiWindowTextGUI gui) {
        this.gui = gui;
    }

    public void showMainMenu() {
        Runnable startBattle = () -> {
            Player player = TestScenario.createPlayer();
            Enemy enemy = TestScenario.createEnemy();

            BattleUI battleUI = new BattleUI(gui, player, enemy);
            battleUI.setOnBattleEnd(this::showMainMenu);
            battleUI.start();
        };

        MainMenu menu = new MainMenu(gui, startBattle);
        gui.addWindowAndWait(menu.build());
    }
}
