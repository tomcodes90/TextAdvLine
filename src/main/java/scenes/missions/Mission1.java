package scenes.missions;

import battle.actions.BattleResult;
import characters.EnemyFactory;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import dialogues.*;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.ui.Battle;
import scenes.ui.DialogueUI;
import scenes.worldhub.WorldHub;
import state.GameState;
import util.DeveloperLogger;

import java.util.List;

/**
 * Mission 1 – “The Golden Garlic of Belmonte”
 * <p>
 * Flow:
 * 0  Intro → step++
 * 1  Battle #1  (Parmesani Goons)
 * 2  Branching dialogue – choose path
 * 3  Battle #2  (enemy depends on choice)
 * 4  Outro → WorldHub
 */
public class Mission1 implements Scene {

    private final MultiWindowTextGUI gui;
    private final DialogueService dialogueService;

    private int step = 0;

    /**
     * Stores which branch the player picked
     */
    private Branch branch;
    private int playerLevel;

    private enum Branch {QUIET_ROUTE, LOUD_ROUTE}

    public Mission1(MultiWindowTextGUI gui) {
        this.gui = gui;
        this.dialogueService = DialogueService.getInstance();
    }

    // ───────────────────────────────────────── Scene API ──────────────────────────────────────────
    @Override
    public void enter() {
        dialogueService.setUI(new DialogueUI(gui));
        nextStep();
    }

    @Override
    public void handleInput() { /* nothing */ }

    @Override
    public void exit() { /* nothing */ }

    private void nextStep() {
        switch (step++) {
            case 0 -> introDialogue();
            case 1 -> firstBattle();
            case 2 -> branchingDialogue();
            case 3 -> afterBranchDialogue();
            case 4 -> secondBattle();
            case 5 -> outroDialogue();
            case 6 -> SceneManager.get().switchTo(new WorldHub(gui, GameState.get().getPlayer()));
        }
    }

    // ──────────────────────────────────────── Step 0 – Intro ─────────────────────────────────────
    private void introDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator",
                        "Belmonte – rolling hills, sun-kissed vines… and the smell of priceless Golden Garlic."),
                new Dialogue("Narrator",
                        "You follow Nonna’s directions and soon hear rough voices ahead… Parmesani goons!")
        ), this::nextStep);
    }

    // ───────────────────────────────────── Step 1 – First Battle ─────────────────────────────────
    private void firstBattle() {
        Battle battle = new Battle(gui,
                GameState.get().getPlayer(),
                EnemyFactory.createParmesaniGoon(playerLevel));

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                DeveloperLogger.log("Won battle #1");
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });
        SceneManager.get().switchTo(battle);
    }

    // ─────────────────────────────── Step 2 – Branching Dialogue ────────────────────────────────
    private void branchingDialogue() {
        DialogueWithInput choice = new DialogueWithInput(
                "Narrator",
                "Ahead, the road splits:\n" +
                        "• Left: a quiet olive-grove path.\n" +
                        "• Right: the main gate, crawling with guards.\n\n" +
                        "Which way, giovane cuoco?",
                List.of(
                        new ChoiceOption("Sneak through the olive grove",
                                () -> {
                                    branch = Branch.QUIET_ROUTE;
                                    nextStep();
                                }),
                        new ChoiceOption("Charge the front gate (Nonna would be proud!)",
                                () -> {
                                    branch = Branch.LOUD_ROUTE;
                                    nextStep();
                                })
                ),
                DialogueInputType.CHOICE_ONLY
        );

        dialogueService.runDialogueWithInput(choice);
    }

    private void afterBranchDialogue() {
        String playerName = GameState.get().getPlayer().getName();

        List<Dialogue> dialogues;

        if (branch == Branch.QUIET_ROUTE) {
            dialogues = List.of(
                    new Dialogue("Narrator", "You tread lightly among the olive trees, cicadas humming in rhythm."),
                    new Dialogue("Hero", "Almost too quiet... Better stay sharp."),
                    new Dialogue("Narrator", "A shadow shifts — a Ricottelli scout springs from the grove!")
            );
        } else {
            dialogues = List.of(
                    new Dialogue("Narrator", "You storm toward the front gate. Shouts ring out as guards spot you."),
                    new Dialogue("Hero", "Time to make Nonna proud!"),
                    new Dialogue("Narrator", "A burly Parmesan captain cracks his knuckles, stepping forward.")
            );
        }

        dialogueService.runDialogues(dialogues, this::nextStep);
    }

    // ─────────────────────────────────── Step 3 – Second Battle ─────────────────────────────────
    private void secondBattle() {
        Battle battle;

        if (branch == Branch.QUIET_ROUTE) {
            battle = new Battle(gui,
                    GameState.get().getPlayer(),
                    EnemyFactory.createRicottelliScout(playerLevel));
        } else {
            battle = new Battle(gui,
                    GameState.get().getPlayer(),
                    EnemyFactory.createParmesaniCaptain(playerLevel + 1));
        }

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                DeveloperLogger.log("Won battle #2 (" + branch + ")");
                GameState.get().setMissionFlag(MissionType.MISSION_1);
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });
        SceneManager.get().switchTo(battle);
    }

    // ───────────────────────────────────── Step 4 – Outro ───────────────────────────────────────
    private void outroDialogue() {
        String name = GameState.get().getPlayer().getName();

        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator",
                        "With the guards beaten, you pluck the legendary Golden Garlic. It glows softly, almost humming."),
                new Dialogue("Hero",
                        "Nonna’s going to flip the whole table when she sees this!"),
                new Dialogue("Narrator",
                        "Mission Complete — you pocket the garlic and head back. Dinner awaits… and so do new adventures!"),

                new Dialogue("Nonna", branch == Branch.LOUD_ROUTE ?
                        String.format("Finalmente! You smell like sweat and victory, %s. Hand me that garlic before it bruises.", name) :
                        String.format("The olive grove? Clever move, %s — I taught you well. But next time, don’t shake every tree on the way.", name)),

                new Dialogue("Hero",
                        "Here it is, still shining."),

                new Dialogue("Nonna",
                        String.format("Bravissimo, %s! One ingredient down, a pantry full to go. Rest for now — tomorrow we hunt basil.", name)),
                new Dialogue("Hero",
                        "Basil? That sounds almost too easy after this…"),
                new Dialogue("Nonna",
                        "Ha! Wait until you meet the Pesto Priests. Now sit. Eat. Then off to bed with you.")

        ), this::nextStep);
    }

    // ─────────────────────────────── Helper – defeat / flee path ────────────────────────────────
    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator",
                        r == BattleResult.DEFEAT
                                ? "You fall. The last thing you hear is mocking laughter… and your stomach growling."
                                : "You flee, garlic-less and hungry. Nonna will NOT be amused.")
        ), () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }
}
