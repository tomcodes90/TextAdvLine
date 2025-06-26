package scenes.missions;

import battle.actions.BattleResult;
import characters.EnemyFactory;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import dialogues.*;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.ui.Battle;
import scenes.ui.DialogueUI;
import state.GameState;
import util.DeveloperLogger;

import java.util.List;

public class Mission8 implements Scene {
    private final MultiWindowTextGUI gui;
    private final DialogueService dialogueService;
    private int step = 0;
    private int playerLevel;

    public Mission8(MultiWindowTextGUI gui) {
        this.gui = gui;
        this.dialogueService = DialogueService.getInstance();
    }

    // ─────────────────────────────────────────────────────────────
    @Override
    public void enter() {
        dialogueService.setUI(new DialogueUI(gui));
        playerLevel = GameState.get().getPlayer().getLevel();
        nextStep();
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void exit() {
    }

    // ─────────────────────────────────────────────────────────────
    private void nextStep() {
        switch (step++) {
            case 0 -> preparationDialogue();
            case 1 -> arrivalDialogue();
            case 2 -> firstBoss();
            case 3 -> midDialogueOne();
            case 4 -> secondBoss();
            case 5 -> midDialogueTwo();
            case 6 -> thirdBoss();
            case 7 -> finaleDialogue();
            case 8 -> SceneManager.get().switchTo(new scenes.worldhub.WorldHub(gui, GameState.get().getPlayer()));
        }
    }

    // ───────────────────── Intro – Nonna bakes the Lasagna ─────────────────────
    private void preparationDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "With pork, beef, tomatoes, basil, garlic, and parmesan gathered, the kitchen becomes a battlefield of aroma."),
                        new Dialogue("Nonna", "Stand back. Lasagna must be layered with vision – and the stamina of a Roman legion."),
                        new Dialogue("Hero", "That tray is bigger than my future."),
                        new Dialogue("Nonna", "Dream bigger, cut wider. Pass me the rolling pin of destiny."),
                        new Dialogue("Narrator", "Sheets of pasta stack like sandstone cliffs; sauce flows like molten lava; cheese snows upon the summit."),
                        new Dialogue("Nonna", "Into the oven – and may the gods of gluten smile.")),
                this::nextStep);
    }

    // ───────────────────── Arrival at the Great Council ─────────────────────
    private void arrivalDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "City of Forchetta – neutral ground where Ricottelli, Parmesani, and Linguini leaders meet."),
                        new Dialogue("Hero", "Nonna, everyone’s staring."),
                        new Dialogue("Nonna", "Let them. They stare because they’re hungry for truth – and lunch."),
                        new Dialogue("Narrator", "The silver lid lifts. Steam rolls across marble floors. The crowd gasps – then the bosses scoff."),
                        new Dialogue("Ricottelli Boss", "A peasant casserole? Laughable."),
                        new Dialogue("Parmesani Don", "You dare season diplomacy with dairy?"),
                        new Dialogue("Linguini Matriarch", "If that’s your weapon, perhaps we should taste your resolve first.")),
                this::nextStep);
    }

    // ───────────────────── Boss 1: Ricottelli ─────────────────────
    private void firstBoss() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createRicottelliPatriarch(playerLevel));
        battle.setOnBattleEnd(r -> onBattleEnd(r, this::midDialogueOne));
        SceneManager.get().switchTo(battle);
    }

    private void midDialogueOne() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Nonna", "One family down. Two more courses to go."),
                        new Dialogue("Hero", "The crowd is getting restless."),
                        new Dialogue("Nonna", "Good. Appetite sharpens judgement.")),
                this::nextStep);
    }

    // ───────────────────── Boss 2: Parmesani ─────────────────────
    private void secondBoss() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createParmesaniDon(playerLevel));
        battle.setOnBattleEnd(r -> onBattleEnd(r, this::midDialogueTwo));
        SceneManager.get().switchTo(battle);
    }

    private void midDialogueTwo() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Nonna", "Cheese crumbles under pressure. One plate left warm."),
                        new Dialogue("Linguini Matriarch", "Impressive… but pasta reigns supreme!")),
                this::nextStep);
    }

    // ───────────────────── Boss 3: Linguini ─────────────────────
    private void thirdBoss() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createLinguiniMatriarch(playerLevel));
        battle.setOnBattleEnd(r -> onBattleEnd(r, this::finaleDialogue));
        SceneManager.get().switchTo(battle);
    }

    // ───────────────────── Resolution & Feast ─────────────────────
    private void finaleDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "Three titans have fallen. Silence simmers."),
                        new Dialogue("Hero", "Will you finally taste?"),
                        new Dialogue("Crowd", "Taste! Taste! Taste!"),
                        new Dialogue("Nonna", "Mangiate! Taste the freedom of flavor!"),
                        new Dialogue("Narrator", "Forks dive. Eyes widen. Centuries of rivalry melt into mozzarella-like peace."),
                        new Dialogue("Narrator", "Food laws crumble as quickly as the lasagna disappears."),
                        new Dialogue("Nonna", "Remember this layer of history – built on courage… and carbs."),
                        new Dialogue("Narrator", "Thus ends the War of Taste. And thus begins an era where every table is free.")),
                () -> {
                    GameState.get().setMissionFlag(MissionType.MISSION_8);
                    nextStep();
                });
    }

    // ───────────────────── Utility ─────────────────────
    private void onBattleEnd(BattleResult r, Runnable onVictory) {
        if (r == BattleResult.VICTORY) {
            DeveloperLogger.log("Boss defeated in Mission 8");
            onVictory.run();
        } else {
            failAndKick(r);
        }
    }

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", r == BattleResult.DEFEAT ?
                                "Crushed beneath ego and eggplant parm, your dream burns." :
                                "You flee, lasagna cold, hearts colder.")),
                () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }
}
