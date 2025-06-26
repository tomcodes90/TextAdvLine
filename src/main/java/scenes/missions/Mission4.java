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

public class Mission4 implements Scene {
    private final MultiWindowTextGUI gui;
    private final DialogueService dialogueService;
    private int step = 0;
    private Path path;
    private int playerLevel;

    private enum Path {SMUGGLER_ROUTE, FRONT_GATE}

    public Mission4(MultiWindowTextGUI gui) {
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
            case 1 -> pathChoice();
            case 2 -> encounterBattle();
            case 3 -> bossBattle();
            case 4 -> outroDialogue();
            case 5 -> SceneManager.get().switchTo(new scenes.worldhub.WorldHub(gui, GameState.get().getPlayer()));
        }
    }

    private void introDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "San Marzano – where the ground bubbles with sauce and tomatoes ripen in volcanic ash."),
                        new Dialogue("Narrator", "Nonna says the secret to the perfect lasagna lies in these lands."),
                        new Dialogue("Nonna", "These tomatoes aren't your supermarket garbage. They're blessed by fire and forged in flavor."),
                        new Dialogue("Nonna", "If you ruin this, don't bother coming home. Even the basil will judge you."),
                        new Dialogue("Hero", "Got it. No pressure, right?"),
                        new Dialogue("Nonna", "Pressure? Please. This is a tomato, not your sad little ego.")),
                this::nextStep);
    }

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

    private void bossBattle() {
        Battle battle = new Battle(gui,
                GameState.get().getPlayer(),
                EnemyFactory.createRicottelliChef(playerLevel));

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                GameState.get().setMissionFlag(MissionType.MISSION_4);
                DeveloperLogger.log("Defeated Fermenting Golem");
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });
        SceneManager.get().switchTo(battle);
    }

    private void outroDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "You bottle the sacred San Marzano sauce. The air is thick with spice and triumph."),
                        new Dialogue("Hero", "Nonna, I’ve got it. Still hot. Still pure."),
                        new Dialogue("Nonna", "Don’t spill a drop, or I’ll make you lick it off the lava rocks."),
                        new Dialogue("Hero", "Understood."),
                        new Dialogue("Nonna", "Good. You're finally starting to cook with passion. Not talent – we’re not there yet."),
                        new Dialogue("Narrator", "The lasagna nears completion. But the greatest challenge lies ahead...")),
                this::nextStep);
    }

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", r == BattleResult.DEFEAT ?
                                "You collapse into a pool of bubbling sauce. Your journey ends medium-rare." :
                                "You flee, leaving the sacred tomatoes behind. Nonna is... disappointed.")),
                () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }
}
