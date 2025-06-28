// File: scenes/missions/Mission5.java
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
 * Mission5 – Infiltrating the Linguini Feast
 * <p>
 * The player crashes the Linguini family’s secretive banquet in Al Dente to steal
 * the legendary “Menu of Dominion.” There are two approach choices (sneak or attack),
 * each influencing the first battle. Afterward, a boss battle determines the mission outcome.
 * Uses Lanterna for all UI transitions and display.
 */
public class Mission5 implements Scene {
    private final MultiWindowTextGUI gui;             // Lanterna UI manager
    private final DialogueService dialogueService;    // Dialogue handler
    private int step = 0;                             // Mission progress tracker
    private int playerLevel;                          // Cached player level
    private int forkChoice = 0;                       // Player’s infiltration method

    public Mission5(MultiWindowTextGUI gui) {
        this.gui = gui;
        this.dialogueService = DialogueService.getInstance();
    }

    // ──────────────────────────────────────────────────────
    // Scene lifecycle
    // ──────────────────────────────────────────────────────

    @Override
    public void enter() {
        dialogueService.setUI(new DialogueUI(gui));                // Attach the Lanterna-based dialogue UI
        playerLevel = GameState.get().getPlayer().getLevel();      // Cache level for enemy scaling
        nextStep();                                                // Begin step sequence
    }

    @Override
    public void exit() {
        // No cleanup needed
    }

    // ──────────────────────────────────────────────────────
    // Step management logic
    // ──────────────────────────────────────────────────────

    private void nextStep() {
        switch (step++) {
            case 0 -> introDialogue();
            case 1 -> infiltrationChoice();
            case 2 -> firstBattle();
            case 3 -> secondBattle();
            case 4 -> outroDialogue();
            case 5 -> SceneManager.get().switchTo(new scenes.worldhub.WorldHub(gui, GameState.get().getPlayer()));
        }
    }

    // ──────────────────────────────────────────────────────
    // Step 0 – Nonna sends you to steal the menu
    // ──────────────────────────────────────────────────────

    private void introDialogue() {
        String name = GameState.get().getPlayer().getName();
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "The Linguini family has declared a Grand Feast in their capital, Al Dente."),
                new Dialogue("Narrator", "All major food families gather — but this is no banquet. It's a trap."),
                new Dialogue("Nonna", "The Linguini think they're the sauce on top, but their noodles are hollow."),
                new Dialogue("Nonna", name + ", I need you to crash their feast and steal the Menu of Dominion. Yes, it's real."),
                new Dialogue("Hero", "A menu? Seriously?"),
                new Dialogue("Nonna", "That menu controls every dish approved across the peninsula. It's bureaucratic blasphemy."),
                new Dialogue("Nonna", "Get in, stir the pot, and get out. And try not to get flambéed. I like my " + name + " al dente — not burnt.")
        ), this::nextStep);
    }

    // ──────────────────────────────────────────────────────
    // Step 1 – Infiltration path choice
    // ──────────────────────────────────────────────────────

    private void infiltrationChoice() {
        DialogueWithInput choice = new DialogueWithInput(
                "Narrator",
                "You approach Al Dente under cover of dusk. How do you enter the feast hall?",
                List.of(
                        new ChoiceOption("Slip in dressed as a waiter", () -> {
                            forkChoice = 1;
                            nextStep();
                        }),
                        new ChoiceOption("Assault with a flaming cheese wheel", () -> {
                            forkChoice = 2;
                            nextStep();
                        })
                ),
                DialogueInputType.CHOICE_ONLY
        );
        dialogueService.runDialogueWithInput(choice);
    }

    // ──────────────────────────────────────────────────────
    // Step 2 – First encounter, based on path choice
    // ──────────────────────────────────────────────────────

    private void firstBattle() {

        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", forkChoice == 1 ?
                        "Dressed in a pressed white apron, you blend in — until a suspicious goon points at your boots." :
                        "You barrel through the gates atop a flaming wheel of provolone. Gasps, screams — and then swords."),
                new Dialogue("Hero", forkChoice == 1 ?
                        "Uhh... Soup of the day? It's... panic." :
                        "Let’s turn up the heat!")
        ), () -> {
            Battle battle = switch (forkChoice) {
                case 1 -> new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createLinguiniGoon(playerLevel));
                case 2 -> new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createLinguiniKnight(playerLevel));
                default -> throw new IllegalStateException("Invalid fork choice");
            };

            battle.setOnBattleEnd(r -> {
                if (r == BattleResult.VICTORY) {
                    DeveloperLogger.log("First obstacle at Linguini feast passed");
                    SceneManager.get().switchTo(this);
                } else {
                    failAndKick(r);
                }
            });

            SceneManager.get().switchTo(battle);
        });
    }

    // ──────────────────────────────────────────────────────
    // Step 3 – Boss: Linguini Champion guarding the Menu
    // ──────────────────────────────────────────────────────

    private void secondBattle() {
        String name = GameState.get().getPlayer().getName();

        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "Deeper inside the palace kitchen, you find it: the Menu of Dominion, guarded by the Linguini Champion."),
                new Dialogue("Hero", "That’s it. The holy menu... and a giant with marinara in his veins."),
                new Dialogue("Champion", "No dish leaves this kitchen uncooked."),
                new Dialogue("Nonna", "Breathe, " + name + ". Remember your training. And if that fails, aim for his kneecaps.")
        ), () -> {
            Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createLinguiniChampion(playerLevel));

            battle.setOnBattleEnd(r -> {
                if (r == BattleResult.VICTORY) {
                    GameState.get().setMissionFlag(MissionType.MISSION_5);
                    MessageDialog.showMessageDialog(gui, "Item Found", "You found the Sicilian Roses book!");
                    GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_thornsurge"));
                    DeveloperLogger.log("Defeated Linguini Champion, stole the Menu of Dominion");
                    SceneManager.get().switchTo(this);
                } else {
                    failAndKick(r);
                }
            });

            SceneManager.get().switchTo(battle);
        });
    }

    // ──────────────────────────────────────────────────────
    // Step 4 – Mission outro and reward setup
    // ──────────────────────────────────────────────────────

    private void outroDialogue() {
        String name = GameState.get().getPlayer().getName();
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "You escape Al Dente with the Menu of Dominion clutched in hand."),
                new Dialogue("Nonna", "Well well, look who didn’t get flambéed. Maybe there’s hope for you after all, " + name + "."),
                new Dialogue("Hero", "That Champion had more muscles than marinara."),
                new Dialogue("Nonna", "Please. I've beaten fiercer with a wooden spoon and a deadline."),
                new Dialogue("Nonna", "Come home. The pasta is boiling, and we’re one step closer to the recipe they all forgot."),
                new Dialogue("Narrator", "As the sun sets over Al Dente, you feel the winds of final preparation stirring...")
        ), this::nextStep);
    }

    // ──────────────────────────────────────────────────────
    // Utility – Handle defeat or retreat
    // ──────────────────────────────────────────────────────

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", r == BattleResult.DEFEAT ?
                        "You’re tossed into a boiling pot and declared unseasoned." :
                        "You flee Al Dente in disgrace, empty-stomached and empty-handed.")
        ), () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }
}
