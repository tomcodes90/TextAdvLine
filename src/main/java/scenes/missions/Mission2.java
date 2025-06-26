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
    public void handleInput() {
    }

    @Override
    public void exit() {
    }

    private void nextStep() {
        switch (step++) {
            case 0 -> introDialogue();
            case 1 -> infiltrationChoice();
            case 2 -> routeBattle();
            case 3 -> bossBattle();
            case 4 -> outroDialogue();
            case 5 -> SceneManager.get().switchTo(new scenes.worldhub.WorldHub(gui, GameState.get().getPlayer()));
        }
    }

    private void introDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "Santa Verde – basil capital of the Ricottelli empire."),
                        new Dialogue("Narrator", "You're here for one thing: the Emerald Basil Crown."),
                        new Dialogue("Nonna", "Don’t get caught, ragazzo. If they spot you, pretend you’re lost… or a spice merchant.")),
                this::nextStep);
    }

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

    private void routeBattle() {
        Battle battle = switch (route) {
            case STEALTH -> new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createBasilCultist(playerLevel));
            case DISTRACTION ->
                    new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createRicottelliPriest(playerLevel));
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

    private void bossBattle() {
        Battle battle = new Battle(gui,
                GameState.get().getPlayer(),
                EnemyFactory.createPestoMonkBoss(playerLevel));

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                DeveloperLogger.log("Boss defeated");
                GameState.get().setMissionFlag(MissionType.MISSION_2);
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });
        SceneManager.get().switchTo(battle);
    }

    private void outroDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "You escape Santa Verde with the Emerald Basil in hand."),
                        new Dialogue("Nonna", "A basil crown? Now that's regality. Next stop: Parmigiano Mines."),
                        new Dialogue("Hero", "That monk nearly made pesto out of me..."),
                        new Dialogue("Nonna", "Rest now, ragazzo. Tomorrow we melt cheese and egos.")),
                this::nextStep);
    }

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", r == BattleResult.DEFEAT ?
                                "You fall in the basil fields, choking on herbs and regret." :
                                "You flee Santa Verde, empty-handed. Nonna will not forget this.")),
                () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }
}
