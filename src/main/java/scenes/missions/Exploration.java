package scenes.missions;

import characters.Enemy;
import characters.EnemyFactory;
import characters.Player;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import dialogues.Dialogue;
import dialogues.DialogueService;

import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.menu.MainMenu;
import scenes.ui.Battle;
import scenes.ui.DialogueUI;
import scenes.worldhub.WorldHub;

import java.util.List;

public class Exploration implements Scene {
    private final MultiWindowTextGUI gui;
    private final Player player;
    private final DialogueService dialogueService;

    public Exploration(MultiWindowTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
        this.dialogueService = DialogueService.getInstance();
    }

    @Override
    public void enter() {
        dialogueService.setUI(new DialogueUI(gui));
        showExplorationStart();
    }

    private void showExplorationStart() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "You head out into the wilderness...", "🧭"),
                new Dialogue("Narrator", "The wind howls... You sense danger nearby.", "🌫️"),
                new Dialogue("Narrator", "Suddenly, an enemy leaps out!", "⚔️")
        ), this::startBattle);
    }

    private void startBattle() {
        Enemy enemy = EnemyFactory.createRandomEnemy(); // you can define this method
        Battle battle = new Battle(gui, player, enemy);
        SceneManager.get().switchTo(battle);
    }

    @Override
    public void handleInput() {}

    @Override
    public void exit() {}
}
