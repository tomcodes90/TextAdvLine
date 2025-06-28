// File: scenes/missions/Mission8.java
package scenes.missions;

import battle.actions.BattleResult;
import characters.EnemyFactory;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import dialogues.*;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.menu.MainMenu;
import scenes.ui.Battle;
import scenes.ui.DialogueUI;
import scenes.worldhub.WorldHub;
import state.GameState;
import util.DeveloperLogger;

import java.util.List;

/**
 * Mission8 – The Lasagna Finale
 * <p>
 * In this epic final mission, the player presents their completed lasagna to the rival food clans.
 * When diplomacy fails, battles ensue – one against each leader. Victory ends the War of Taste.
 */
public class Mission8 implements Scene {
    private final MultiWindowTextGUI gui;              // Lanterna GUI controller
    private final DialogueService dialogueService;     // Centralized dialogue manager
    private final String name = GameState.get().getPlayer().getName();     // Player's name
    private final int playerLevel = GameState.get().getPlayer().getLevel(); // For enemy scaling

    public Mission8(MultiWindowTextGUI gui) {
        this.gui = gui;
        this.dialogueService = DialogueService.getInstance();
    }

    // ──────────────────────────────────────────────────────
    // Scene Entry
    // ──────────────────────────────────────────────────────

    @Override
    public void enter() {
        // Attach Lanterna-compatible dialogue UI to the service
        dialogueService.setUI(new DialogueUI(gui));
        introDialogue();
    }

    @Override
    public void exit() {
        // No exit handling required
    }

    // ──────────────────────────────────────────────────────
    // Step 0 – Lasagna Assembly Cinematic
    // ──────────────────────────────────────────────────────

    private void introDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "With pork, beef, tomatoes, basil, garlic, and parmesan gathered, the kitchen becomes a battlefield of aroma."),
                new Dialogue("Nonna", "Stand back. It must be layered with vision – and the stamina of a Roman legion."),
                new Dialogue("Hero", "That tray is bigger than my future."),
                new Dialogue("Nonna", "Dream bigger, cut wider. Pass me the rolling pin of destiny."),
                new Dialogue("Narrator", "Sheets of pasta stack like sandstone cliffs; sauce flows like molten lava; cheese snows upon the summit."),
                new Dialogue("Nonna", "Into the oven – and may the gods of gluten smile.")
        ), this::arrivalDialogue);
    }

    // ──────────────────────────────────────────────────────
    // Step 1 – Arriving at Forchetta Council
    // ──────────────────────────────────────────────────────

    private void arrivalDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "City of Forchetta – neutral ground where Ricottelli, Parmesani, and Linguini leaders meet."),
                new Dialogue("Hero", "Nonna, everyone’s staring."),
                new Dialogue("Nonna", "Let them. They stare because they’re hungry for truth – and lunch."),
                new Dialogue("Narrator", "The silver lid lifts. Steam rolls across marble floors. The crowd gasps – then the bosses scoff."),
                new Dialogue("Ricottelli Boss", "A peasant casserole? Laughable."),
                new Dialogue("Parmesani Don", "You dare season diplomacy with dairy?"),
                new Dialogue("Linguini Matriarch", "If that’s your weapon, perhaps we should taste your resolve first.")
        ), this::firstBoss);
    }

    // ──────────────────────────────────────────────────────
    // Step 2 – Boss Fight 1: Ricottelli Patriarch
    // ──────────────────────────────────────────────────────

    private void firstBoss() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createRicottelliPatriarch(playerLevel));
        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                DeveloperLogger.log("Boss 1 down");
                dialogueService.runDialogues(List.of(
                        new Dialogue("Nonna", "One family down. Two more courses to go."),
                        new Dialogue("Hero", "The crowd is getting restless."),
                        new Dialogue("Nonna", "Good. Appetite sharpens judgement.")
                ), this::secondBoss);
            } else {
                failAndKick(r);
            }
        });
        SceneManager.get().switchTo(battle);
    }

    // ──────────────────────────────────────────────────────
    // Step 3 – Boss Fight 2: Parmesani Don
    // ──────────────────────────────────────────────────────

    private void secondBoss() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createParmesaniDon(playerLevel));
        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                DeveloperLogger.log("Boss 2 down");
                dialogueService.runDialogues(List.of(
                        new Dialogue("Nonna", "Cheese crumbles under pressure. One plate left warm."),
                        new Dialogue("Linguini Matriarch", "Impressive… but pasta reigns supreme!")
                ), this::thirdBoss);
            } else {
                failAndKick(r);
            }
        });
        SceneManager.get().switchTo(battle);
    }

    // ──────────────────────────────────────────────────────
    // Step 4 – Final Boss: Linguini Matriarch
    // ──────────────────────────────────────────────────────

    private void thirdBoss() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createLinguiniMatriarch(playerLevel));
        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                DeveloperLogger.log("Boss 3 down");
                finaleDialogue();
            } else {
                failAndKick(r);
            }
        });
        SceneManager.get().switchTo(battle);
    }

    // ──────────────────────────────────────────────────────
    // Step 5 – Epilogue: Peace Through Pasta
    // ──────────────────────────────────────────────────────

    private void finaleDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "Three titans have fallen. Silence simmers."),
                new Dialogue("Hero", "Will you finally taste?"),
                new Dialogue("Crowd", "Taste! Taste! Taste!"),
                new Dialogue("Nonna", "Mangiate! Taste the freedom of flavor! This is Lasagna!!!"),
                new Dialogue("Narrator", "Forks dive. Eyes widen. Centuries of rivalry melt into mozzarella-like peace."),
                new Dialogue("Narrator", "Food laws crumble as quickly as the lasagna disappears."),
                new Dialogue("Nonna", "Remember this layer of history – built on courage… and carbs."),
                new Dialogue("Narrator", "Thus ends the War of Taste. And thus begins an era where every table is free.")
        ), () -> {
            GameState.get().setMissionFlag(MissionType.MISSION_8);
            SceneManager.get().switchTo(new WorldHub(gui, GameState.get().getPlayer()));
        });
    }

    // ──────────────────────────────────────────────────────
    // Failure Route – Back to Main Menu
    // ──────────────────────────────────────────────────────

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", r == BattleResult.DEFEAT ?
                        "Crushed beneath ego and eggplant parm, your dream burns." :
                        "You flee, pizza cold, hearts colder.")
        ), () -> SceneManager.get().switchTo(new MainMenu(gui)));
    }
}
