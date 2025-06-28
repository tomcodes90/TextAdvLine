package scenes.missions;

import characters.Enemy;
import characters.EnemyFactory;
import characters.Player;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import dialogues.Dialogue;
import dialogues.DialogueService;

import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.ui.Battle;
import scenes.ui.DialogueUI;

import java.util.List;

/**
 * 🌲 Exploration: A short scene where the player encounters random enemies.
 * <p>
 * 🧭 Purpose:
 * - Starts with a brief narrated sequence
 * - Then initiates a random battle based on the player's level
 * <p>
 * 🔁 Flow:
 * 1. Show narration using DialogueService.
 * 2. Spawn a random enemy.
 * 3. Launch a Battle scene.
 * <p>
 * 🧱 Dependencies:
 * - DialogueService singleton for scripted narration
 * - EnemyFactory for creating scalable threats
 * - SceneManager to transition between scenes
 */
public class Exploration implements Scene {
    private final MultiWindowTextGUI gui;
    private final Player player;
    private final DialogueService dialogueService;

    /**
     * 🎬 Constructor
     *
     * @param gui    The main Lanterna GUI controller
     * @param player The current player
     */
    public Exploration(MultiWindowTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
        this.dialogueService = DialogueService.getInstance(); // singleton pattern
    }

    /**
     * 🚪 Called when the scene is entered.
     * Sets up the Dialogue UI and starts the narration.
     */
    @Override
    public void enter() {
        dialogueService.setUI(new DialogueUI(gui));
        showExplorationStart();
    }

    /**
     * 🗺️ Initial exploration text sequence.
     * Narrates a short intro before launching the battle.
     */
    private void showExplorationStart() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "You head out into the wilderness..."),
                new Dialogue("Narrator", "The wind howls... You sense danger nearby."),
                new Dialogue("Narrator", "Suddenly, an enemy leaps out! Mamma Mia!")
        ), this::startBattle);
    }

    /**
     * ⚔️ Starts a random battle after the dialogues.
     * Uses player's level to scale difficulty.
     */
    private void startBattle() {
        Enemy enemy = EnemyFactory.createRandomEnemy(player.getLevel()); // define this method in your factory
        Battle battle = new Battle(gui, player, enemy);
        SceneManager.get().switchTo(battle); // delegate transition to scene manager
    }

    /**
     * 🔚 Exit logic (unused for now).
     */
    @Override
    public void exit() {
        // nothing for now
    }
}
