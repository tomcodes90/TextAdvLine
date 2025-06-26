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

public class Mission3 implements Scene {
    private final MultiWindowTextGUI gui;
    private final DialogueService dialogueService;
    private int step = 0;
    private Route route;
    private int playerLevel;

    private enum Route {STEALTH, DISTRACTION}

    public Mission3(MultiWindowTextGUI gui) {
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
                        new Dialogue("Narrator", "You descend into the Parmigiano Mines — where cheese crystals grow in darkness."),
                        new Dialogue("Narrator", "Nonna swears the lasagna’s base lies deep below."),
                        new Dialogue("Nonna", "Watch your step. One wrong move and you’re grated like mozzarella.")),
                this::nextStep);
    }

    private void infiltrationChoice() {
        DialogueWithInput choice = new DialogueWithInput(
                "Narrator",
                "You reach the mine’s old shaft entrance. Do you:",
                List.of(
                        new ChoiceOption("Crawl through the cracked ventilation shaft", () -> {
                            route = Route.STEALTH;
                            nextStep();
                        }),
                        new ChoiceOption("Drop in through the freight elevator with a loud crash", () -> {
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
            case STEALTH -> new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createParmesaniGoon(playerLevel));
            case DISTRACTION ->
                    new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createParmesaniCaptain(playerLevel));
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
                EnemyFactory.createCheeseGuardian(playerLevel));

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                GameState.get().setMissionFlag(MissionType.MISSION_3);
                DeveloperLogger.log("Boss defeated");
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });
        SceneManager.get().switchTo(battle);
    }

    private void outroDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "The crystals crackle behind you. In your hands, a golden wedge of Parmigiano."),
                        new Dialogue("Hero", "One more step toward Nonna's ultimate lasagna."),
                        new Dialogue("Nonna", "Bravissimo. Now all we need is the sauce thick enough to silence a debate.")),
                this::nextStep);
    }

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", r == BattleResult.DEFEAT ?
                                "You fall in the cheese caverns, buried under regret and rind." :
                                "You flee the Parmigiano Mines, empty-handed. Nonna will not forget this.")),
                () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }
}
