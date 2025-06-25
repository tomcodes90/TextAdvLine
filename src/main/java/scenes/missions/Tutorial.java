package scenes.missions;

import characters.EnemyFactory;
import characters.Player;
import characters.StatsType;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import dialogues.*;
import state.GameState;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.WorldHub;
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
                new Dialogue("nonna", "You wake up in a quiet village...", "📜"),
                new Dialogue("nonna", "The world is at war, but you were kept hidden.", "📜"),
                new Dialogue("nonna", "Today, everything will change.", "📜")
        ), this::nextStep);
    }

    private String pendingName; // store name between steps

    private void askNameAndCreatePlayer() {
        DialogueWithInput askName = new DialogueWithInput(
                "nonna",
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
                "nonna",
                "And what kind of fighter are you?",
                "⚔",
                List.of(
                        new ChoiceOption("=> I hit enemies like a bull!", () -> {
                            player = new Player(pendingName, StatsType.STRENGTH);
                            DeveloperLogger.log("Creating player " + pendingName + " with boost " + StatsType.STRENGTH);
                            GameState.get().setPlayer(player);
                            nextStep();
                        }),
                        new ChoiceOption("I prefer to set them on fire.", () -> {
                            player = new Player(pendingName, StatsType.INTELLIGENCE);
                            DeveloperLogger.log("Creating player " + pendingName + " with boost " + StatsType.INTELLIGENCE);
                            GameState.get().setPlayer(player);
                            nextStep();
                        })
                ),
                DialogueInputType.CHOICE_ONLY
        );

        dialogueService.runDialogueWithInput(statChoice);
    }


    private void showPreBattleDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("nonna", "A scream echoes outside...", "📜"),
                new Dialogue("nonna", "You're under attack! Grab a weapon!", "📜"),
                new Dialogue("nonna", "I'll teach you how to fight.", "🧙")
        ), this::nextStep);
    }

    private void startBattle() {
        Battle battle = new Battle(gui, player, EnemyFactory.createBandit());

        battle.setOnBattleEnd(result -> {
            switch (result) {
                case VICTORY ->
                // Resume the tutorial sequence
                {
                    DeveloperLogger.log("you won");
                    SceneManager.get().switchTo(this);
                }

                case DEFEAT, FLED ->
                    // Show alternate dialogue and go to WorldHub
                        dialogueService.runDialogues(List.of(
                                new Dialogue("Mentor", "You... lost. But you’re still breathing somehow.", "🧙"),
                                new Dialogue("Mentor", "We’ll need to train harder next time.", "🧙")
                        ), () -> SceneManager.get().switchTo(new WorldHub(gui, player)));

                default -> throw new IllegalStateException("Unexpected value: " + result);
            }
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
        SceneManager.get().switchTo(new WorldHub(gui, player));
    }
}
