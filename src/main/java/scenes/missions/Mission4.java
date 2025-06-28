// File: scenes/missions/Mission4.java
package scenes.missions;

import battle.actions.BattleResult;
import characters.EnemyFactory;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import dialogues.*;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.ui.Battle;
import scenes.ui.DialogueUI;
import state.GameState;
import util.DeveloperLogger;
import util.ItemRegistry;

import java.util.List;

/**
 * Mission4 – San Marzano Groves
 * <p>
 * This scene represents the fourth mission of the game.
 * The player makes a branching path decision (stealthy tunnel or frontal assault),
 * fights enemies depending on the choice, and finally confronts a boss (Ricottelli Chef).
 * <p>
 * The class uses Lanterna's `MultiWindowTextGUI` to safely show battle and dialogue screens.
 * ⚠️ Lanterna UI rules: Windows must be added/removed from the same GUI thread, and transitions
 * must happen using SceneManager to prevent UI freezing or race conditions.
 */
public class Mission4 implements Scene {
    private final MultiWindowTextGUI gui;             // Lanterna GUI controller
    private final DialogueService dialogueService;    // Manages running dialogues
    private int step = 0;                             // Current step in mission flow
    private Path path;                                // Chosen path by player
    private final int playerLevel = GameState.get().getPlayer().getLevel();
    private final String name = GameState.get().getPlayer().getName();

    // Internal enum to track player's chosen route
    private enum Path {SMUGGLER_ROUTE, FRONT_GATE}

    public Mission4(MultiWindowTextGUI gui) {
        this.gui = gui;
        this.dialogueService = DialogueService.getInstance();
    }

    // ──────────────────────────────────────────────────────────────
    // Scene lifecycle methods
    // ──────────────────────────────────────────────────────────────

    @Override
    public void enter() {
        // Attach GUI to dialogue UI before starting
        dialogueService.setUI(new DialogueUI(gui));
        nextStep();
    }

    @Override
    public void exit() {
        // No cleanup necessary
    }

    // ──────────────────────────────────────────────────────────────
    // Step-by-step mission flow controller
    // ──────────────────────────────────────────────────────────────

    private void nextStep() {
        switch (step++) {
            case 0 -> introDialogue();
            case 1 -> pathChoice();
            case 2 -> preBattleDialogue();
            case 3 -> encounterBattle();
            case 4 -> preBossDialogue();
            case 5 -> bossBattle();
            case 6 -> outroDialogue();
            case 7 -> SceneManager.get().switchTo(new scenes.worldhub.WorldHub(gui, GameState.get().getPlayer()));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Step 0: Mission intro
    // ──────────────────────────────────────────────────────────────

    private void introDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "San Marzano – where the ground bubbles with sauce and tomatoes ripen in volcanic ash."),
                new Dialogue("Narrator", "Nonna says the secret to perfection lies in these lands."),
                new Dialogue("Nonna", String.format("%s, these tomatoes aren't your every day garbage. They're blessed by fire and forged in flavor.", name)),
                new Dialogue("Nonna", "If you ruin this, don't bother coming home. Even the basil will judge you."),
                new Dialogue("Hero", "Got it. No pressure, right?"),
                new Dialogue("Nonna", "Pressure? Please. This is a tomato, not your sad little ego.")
        ), this::nextStep);
    }

    // ──────────────────────────────────────────────────────────────
    // Step 1: Branching path choice (affects enemy + dialog)
    // ──────────────────────────────────────────────────────────────

    private void pathChoice() {
        DialogueWithInput choice = new DialogueWithInput(
                "Narrator",
                "You arrive at the edge of the San Marzano groves. How do you proceed?",
                List.of(
                        new ChoiceOption("Take the hidden smuggler tunnels", () -> {
                            path = Path.SMUGGLER_ROUTE;
                            nextStep();
                        }),
                        new ChoiceOption("March through the Ricottelli front gate", () -> {
                            path = Path.FRONT_GATE;
                            nextStep();
                        })
                ),
                DialogueInputType.CHOICE_ONLY
        );
        dialogueService.runDialogueWithInput(choice);
    }

    // ──────────────────────────────────────────────────────────────
    // Step 2: Path-based flavor dialogue before battle
    // ──────────────────────────────────────────────────────────────

    private void preBattleDialogue() {
        if (path == Path.SMUGGLER_ROUTE) {
            dialogueService.runDialogues(List.of(
                    new Dialogue("Narrator", "The tunnels smell like garlic and danger."),
                    new Dialogue("Hero", "Hope this shortcut doesn't lead to a shortcut to the grave."),
                    new Dialogue("Narrator", "A bandit lunges from the shadows!")
            ), this::nextStep);
        } else {
            dialogueService.runDialogues(List.of(
                    new Dialogue("Narrator", "You stride through the gate like you own the place."),
                    new Dialogue("Hero", "Confidence is seasoning. Let's cook."),
                    new Dialogue("Narrator", "A Ricottelli scout blocks your way, fork drawn.")
            ), this::nextStep);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Step 3: First enemy battle (depends on chosen path)
    // ──────────────────────────────────────────────────────────────

    private void encounterBattle() {
        Battle battle = switch (path) {
            case SMUGGLER_ROUTE -> new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createBandit(playerLevel));
            case FRONT_GATE ->
                    new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createRicottelliScout(playerLevel));
        };

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                DeveloperLogger.log("First encounter cleared via path: " + path);
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });

        SceneManager.get().switchTo(battle);
    }

    // ──────────────────────────────────────────────────────────────
    // Step 4: Flavor dialog before boss fight
    // ──────────────────────────────────────────────────────────────

    private void preBossDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "You reach the heart of the grove: a bubbling sauce forge guarded by..."),
                new Dialogue("Narrator", "A Ricottelli Chef – muscles glazed with oil, wielding a spoon like a mace."),
                new Dialogue("Hero", "Let's turn up the heat."),
                new Dialogue("Nonna", String.format("Make me proud, %s. Show that impostore what real sauce tastes like!", name))
        ), this::nextStep);
    }

    // ──────────────────────────────────────────────────────────────
    // Step 5: Boss fight vs Ricottelli Chef
    // ──────────────────────────────────────────────────────────────

    private void bossBattle() {
        Battle battle = new Battle(gui,
                GameState.get().getPlayer(),
                EnemyFactory.createRicottelliChef(playerLevel));

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                GameState.get().setMissionFlag(MissionType.MISSION_4);
                MessageDialog.showMessageDialog(gui, "Item Found", "You found the InfernoGrana book!");
                GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_inferno"));
                DeveloperLogger.log("Defeated Ricottelli Chef");
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });

        SceneManager.get().switchTo(battle);
    }

    // ──────────────────────────────────────────────────────────────
    // Step 6: Closing dialogue and transition to hub
    // ──────────────────────────────────────────────────────────────

    private void outroDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "You bottle the sacred San Marzano sauce. The air is thick with spice and triumph."),
                new Dialogue("Hero", "Nonna, I’ve got it. Still hot. Still pure."),
                new Dialogue("Nonna", String.format("Don’t spill a drop, %s, or I’ll make you lick it off the lava rocks.", name)),
                new Dialogue("Hero", "Understood."),
                new Dialogue("Nonna", "Good. You're finally starting to cook with passion. Not talent – we’re not there yet."),
                new Dialogue("Narrator", "The dish nears completion. But the greatest challenge lies ahead...")
        ), this::nextStep);
    }

    // ──────────────────────────────────────────────────────────────
    // Utility: Handle loss or fleeing
    // ──────────────────────────────────────────────────────────────

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", r == BattleResult.DEFEAT ?
                        "You collapse into a pool of bubbling sauce. Your journey ends medium-rare." :
                        "You flee, leaving the sacred tomatoes behind. Nonna is... disappointed.")
        ), () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }
}
