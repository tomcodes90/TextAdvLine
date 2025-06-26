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

public class Mission5 implements Scene {
    private final MultiWindowTextGUI gui;
    private final DialogueService dialogueService;
    private int step = 0;
    private int playerLevel;
    private int forkChoice = 0;

    public Mission5(MultiWindowTextGUI gui) {
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
    public void handleInput() {
    }

    @Override
    public void exit() {
    }

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

    private void introDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "The Linguini family has declared a Grand Feast in their capital, Al Dente."),
                        new Dialogue("Narrator", "All major food families gather — but this is no banquet. It's a trap."),
                        new Dialogue("Nonna", "The Linguini think they're the sauce on top, but their noodles are hollow."),
                        new Dialogue("Nonna", "I need you to crash their feast and steal the Menu of Dominion. Yes, it's real."),
                        new Dialogue("Hero", "A menu? Seriously?"),
                        new Dialogue("Nonna", "That menu controls every dish approved across the peninsula. It's bureaucratic blasphemy."),
                        new Dialogue("Nonna", "Get in, stir the pot, and get out. And try not to get flambéed.")),
                this::nextStep);
    }

    private void infiltrationChoice() {
        DialogueWithInput choice = new DialogueWithInput(
                "Narrator",
                "You approach Al Dente under cover of dusk. How do you enter the feast hall?",
                List.of(
                        new ChoiceOption("Slip in dressed as a waiter", () -> {
                            forkChoice = 1;
                            nextStep();
                        }),
                        new ChoiceOption("Crash through the front with a flaming cheese wheel", () -> {
                            forkChoice = 2;
                            nextStep();
                        })
                ),
                DialogueInputType.CHOICE_ONLY
        );
        dialogueService.runDialogueWithInput(choice);
    }

    private void firstBattle() {
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
    }

    private void secondBattle() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createLinguiniChampion(playerLevel));

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                GameState.get().setMissionFlag(MissionType.MISSION_5);
                DeveloperLogger.log("Defeated Linguini Champion, stole the Menu of Dominion");
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });
        SceneManager.get().switchTo(battle);
    }

    private void outroDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "You escape Al Dente with the Menu of Dominion clutched in hand."),
                        new Dialogue("Nonna", "Well well, look who didn’t get flambéed. Maybe there’s hope for you after all."),
                        new Dialogue("Hero", "That Champion had more muscles than marinara."),
                        new Dialogue("Nonna", "Please. I've beaten fiercer with a wooden spoon and a deadline."),
                        new Dialogue("Nonna", "Come home. The pasta is boiling, and we’re one step closer to the recipe they all forgot."),
                        new Dialogue("Narrator", "As the sun sets over Al Dente, you feel the winds of final preparation stirring...")),
                this::nextStep);
    }

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", r == BattleResult.DEFEAT ?
                                "You’re tossed into a boiling pot and declared unseasoned." :
                                "You flee Al Dente in disgrace, empty-stomached and empty-handed.")),
                () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }
}
