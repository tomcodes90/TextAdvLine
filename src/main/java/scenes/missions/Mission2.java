// File: scenes/missions/Mission2.java

/*
 * Mission 2 – “The Emerald Basil Crown”
 *
 * In this mission, the player infiltrates Santa Verde to retrieve a legendary basil relic.
 * It features:
 *  1. Intro with Nonna's warning
 *  2. Infiltration choice: stealth (tunnel) or distraction (cart crash)
 *  3. Route-based battle (Basil Cultist or Ricottelli Priest)
 *  4. Encounter and battle with boss (Pesto Monk)
 *  5. Reward: player receives the Sugo Flare book
 *  6. Outro and transition back to World Hub
 *
 * Implementation notes:
 *  - Mission flow is tracked by `step` and routed through `nextStep()`
 *  - Infiltration path is tracked via the `Route` enum
 *  - Rewards are handled directly after boss victory using GameState and ItemRegistry
 *
 * ⚠ Lanterna notes:
 *  - Asynchronous transitions (battle → resume) must always call SceneManager.switchTo(this)
 *  - GUI message dialogs are used for item pickup notifications
 */

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

public class Mission2 implements Scene {

    private final MultiWindowTextGUI gui;
    private final DialogueService dialogueService;
    private int step = 0;
    private Route route;
    private int playerLevel;

    private enum Route {STEALTH, DISTRACTION}

    public Mission2(MultiWindowTextGUI gui) {
        this.gui = gui;
        this.dialogueService = DialogueService.getInstance();
    }

    @Override
    public void enter() {
        dialogueService.setUI(new DialogueUI(gui));
        playerLevel = GameState.get().getPlayer().getLevel();
        nextStep();
    }

    @Override
    public void exit() {
    }

    // ───────────────────────────── Step Routing ─────────────────────────────

    private void nextStep() {
        switch (step++) {
            case 0 -> introDialogue();
            case 1 -> infiltrationChoice();
            case 2 -> preRouteBattleDialogue();
            case 3 -> routeBattle();
            case 4 -> preBossBattleDialogue();
            case 5 -> bossBattle();
            case 6 -> outroDialogue();
            case 7 -> SceneManager.get().switchTo(new scenes.worldhub.WorldHub(gui, GameState.get().getPlayer()));
        }
    }

    // ───────────────────────────── Step 0 – Intro ─────────────────────────────

    private void introDialogue() {
        String name = GameState.get().getPlayer().getName();

        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "Santa Verde – basil capital of the Ricottelli empire."),
                new Dialogue("Narrator", "You're here for one thing: the Emerald Basil Crown."),
                new Dialogue("Nonna", "Don’t get caught, " + name + ". If they spot you, pretend you’re lost… or a spice merchant."),
                new Dialogue("Hero", "Piece of cake, Nonna. I’ve got this."),
                new Dialogue("Nonna", "Save the cake talk for dessert. Now move!")
        ), this::nextStep);
    }

    // ──────────────── Step 1 – Infiltration Path Selection ────────────────

    private void infiltrationChoice() {
        DialogueWithInput choice = new DialogueWithInput(
                "Narrator",
                "You reach the basil compound. Do you:",
                List.of(
                        new ChoiceOption("Sneak through the irrigation tunnels", () -> {
                            route = Route.STEALTH;
                            nextStep();
                        }),
                        new ChoiceOption("Distract the guards with a loud sauce cart crash", () -> {
                            route = Route.DISTRACTION;
                            nextStep();
                        })
                ),
                DialogueInputType.CHOICE_ONLY
        );
        dialogueService.runDialogueWithInput(choice);
    }

    // ───────────── Step 2 – Pre-Battle Flavor for Route ─────────────

    private void preRouteBattleDialogue() {
        List<Dialogue> dialogues = switch (route) {
            case STEALTH -> List.of(
                    new Dialogue("Narrator", "You slide into the tunnels, damp and mossy. The scent of basil is overwhelming."),
                    new Dialogue("Hero", "Stealth mode activated. Let's just hope there's no guard enjoying a tunnel stroll."),
                    new Dialogue("Narrator", "A hooded figure blocks the way — a Basil Cultist!")
            );
            case DISTRACTION -> List.of(
                    new Dialogue("Narrator", "You roll the sauce cart downhill. It crashes spectacularly. Guards rush over in chaos."),
                    new Dialogue("Hero", "Nonna’s tomato special — now weaponized."),
                    new Dialogue("Narrator", "Amid the confusion, a Ricottelli Priest steps forward, suspicious and armed.")
            );
        };

        dialogueService.runDialogues(dialogues, this::nextStep);
    }

    // ───────────── Step 3 – Route Battle (Cultist or Priest) ─────────────

    private void routeBattle() {
        Battle battle = switch (route) {
            case STEALTH -> new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createBasilCultist(playerLevel));
            case DISTRACTION ->
                    new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createRicottelliPriest(playerLevel + 1));
        };

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                DeveloperLogger.log("Infiltration successful via " + route);
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });

        SceneManager.get().switchTo(battle);
    }

    // ───────────── Step 4 – Boss Battle Intro Dialogue ─────────────

    private void preBossBattleDialogue() {

        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "You slip into the inner sanctum. At its center: the Emerald Basil Crown on a velvet pillow."),
                new Dialogue("Hero", "There it is..."),
                new Dialogue("Narrator", "Before you can grab it, the doors slam shut. A figure emerges — the Pesto Monk."),
                new Dialogue("Pestomonk", "You dare trespass in Santa Verde's sacred garden? Prepare to be blended, intruder."),
                new Dialogue("Hero", "Not before I pluck that basil and season your defeat.")
        ), this::nextStep);
    }

    // ───────────── Step 5 – Boss Battle vs. Pesto Monk ─────────────

    private void bossBattle() {
        Battle battle = new Battle(gui,
                GameState.get().getPlayer(),
                EnemyFactory.createPestoMonkBoss(playerLevel + 2));

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                DeveloperLogger.log("Boss defeated");
                GameState.get().setMissionFlag(MissionType.MISSION_2);

                // Reward: Sugo Flare book
                MessageDialog.showMessageDialog(gui, "Item Found", "You found the Sugo Flare book!");
                GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_flare"));

                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });

        SceneManager.get().switchTo(battle);
    }

    // ───────────── Step 6 – Outro and Transition ─────────────

    private void outroDialogue() {
        String name = GameState.get().getPlayer().getName();

        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "You escape Santa Verde with the Emerald Basil in hand."),
                new Dialogue("Hero", "That monk nearly made pesto out of me..."),
                new Dialogue("Nonna", name + ", you came back with the crown and all limbs intact. Bravissimo!"),
                new Dialogue("Hero", "What's next, Nonna?"),
                new Dialogue("Nonna", "A basil crown is good, but a cheese throne is better. Next stop: the Parmigiano Mines."),
                new Dialogue("Hero", "Can’t wait to get grated."),
                new Dialogue("Nonna", "Rest, ragazzo. Tomorrow we melt cheese and egos.")
        ), this::nextStep);
    }

    // ───────────── Battle Loss / Flee Consequence ─────────────

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", r == BattleResult.DEFEAT
                        ? "You fall in the basil fields, choking on herbs and regret."
                        : "You flee Santa Verde, empty-handed. Nonna will not forget this.")
        ), () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }
}
