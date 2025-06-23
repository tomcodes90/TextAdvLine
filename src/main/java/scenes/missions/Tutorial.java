package scenes.missions;

import characters.Enemy;
import characters.EnemyFactory;
import characters.Player;
import characters.StatsType;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import dialogues.*;
import scenes.Scene;
import scenes.SceneManager;
import scenes.WorldHub;
import scenes.ui.Battle;
import scenes.ui.DialogueUI;
import util.DeveloperLogger;


import java.util.List;

public class Tutorial implements Scene {
    private final MultiWindowTextGUI gui;
    private final DialogueService dialogueService;
    private Player player;
    private int step = 0;

    public Tutorial(MultiWindowTextGUI gui) {
        this.gui = gui;
        this.dialogueService = DialogueService.getInstance();
    }

    @Override
    public void enter() {
        dialogueService.setUI(new DialogueUI(gui));
        nextStep();
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void exit() {
    }

    private void nextStep() {
        switch (step++) {
            case 0 -> showIntroDialogue();
            case 1 -> askNameAndCreatePlayer();
            case 2 -> showPreBattleDialogue();
            case 3 -> startBattle();
            case 4 -> showPostBattleDialogue();
            case 5 -> goToWorldHub();
        }
    }

    private void showIntroDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "You wake up in a quiet village...", "📜"),
                new Dialogue("Narrator", "The world is at war, but you were kept hidden.", "📜"),
                new Dialogue("Narrator", "Today, everything will change.", "📜")
        ), this::nextStep);
    }

    private String pendingName; // store name between steps

    private void askNameAndCreatePlayer() {
        DialogueWithInput askName = new DialogueWithInput(
                "Narrator",
                "What is your name?",
                "📜",
                List.of(new ChoiceOption("Continue", () -> {
                    pendingName = DialogueUI.lastInput.trim();
                    if (pendingName.isEmpty()) pendingName = "Nameless";
                    askForStatPreference(); // go to next step
                })),
                DialogueInputType.TEXT_INPUT
        );
        dialogueService.runDialogueWithInput(askName);
    }

    private void askForStatPreference() {
        DialogueWithInput statChoice = new DialogueWithInput(
                "Narrator",
                "And what kind of fighter are you?",
                "📜",
                List.of(
                        new ChoiceOption("I hit enemies like a bull!", () -> {
                            player = new Player(pendingName, StatsType.STRENGTH);
                            DeveloperLogger.log("Creating player " + pendingName + " with boost " + StatsType.STRENGTH);
                            nextStep();
                        }),
                        new ChoiceOption("I prefer to set them on fire.", () -> {
                            player = new Player(pendingName, StatsType.INTELLIGENCE);
                            DeveloperLogger.log("Creating player " + pendingName + " with boost " + StatsType.INTELLIGENCE);
                            nextStep();
                        })
                ),
                DialogueInputType.CHOICE_ONLY
        );

        dialogueService.runDialogueWithInput(statChoice);
    }


    private void showPreBattleDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "A scream echoes outside...", "📜"),
                new Dialogue("Narrator", "You're under attack! Grab a weapon!", "📜"),
                new Dialogue("Mentor", "I'll teach you how to fight.", "🧙")
        ), this::nextStep);
    }

    private void startBattle() {
        Enemy tutorialEnemy = EnemyFactory.createDarkMage();
        Battle battle = new Battle(gui, player, tutorialEnemy);
        battle.setOnBattleEnd(() -> {
            SceneManager.get().switchTo(this);
            this.enter(); // resume after battle
        });
        SceneManager.get().switchTo(battle);
        

    }

    private void showPostBattleDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Mentor", "You survived... well done.", "🧙"),
                new Dialogue("Mentor", "Time to begin your journey.", "🧙")
        ), this::nextStep);
    }

    private void goToWorldHub() {
        SceneManager.get().switchTo(new WorldHub(gui));
    }
}
