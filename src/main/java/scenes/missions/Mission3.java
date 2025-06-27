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
        String name = GameState.get().getPlayer().getName();
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "You descend into the Parmigiano Mines — where cheese crystals grow in darkness."),
                new Dialogue("Narrator", "Nonna swears the lasagna’s base lies deep below."),
                new Dialogue("Nonna", "Watch your step. One wrong move and you’re grated like mozzarella.")
        ), this::nextStep);
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
        String name = GameState.get().getPlayer().getName();

        List<Dialogue> preBattleDialogue = switch (route) {
            case STEALTH -> List.of(
                    new Dialogue("Narrator", "You sneak through the shafts and drop behind a lone guard."),
                    new Dialogue("Parmesani Goon", "Huh? Who goes th— *urk*!")
            );
            case DISTRACTION -> List.of(
                    new Dialogue("Narrator", "The elevator crashes down. A guard captain storms toward the noise."),
                    new Dialogue("Parmesani Captain", "Intruder! You'll regret bringing noise to the mines!")
            );
        };

        dialogueService.runDialogues(preBattleDialogue, () -> {
            Battle battle = switch (route) {
                case STEALTH ->
                        new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createParmesaniGoon(playerLevel));
                case DISTRACTION ->
                        new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createParmesaniCaptain(playerLevel));
            };

            battle.setOnBattleEnd(r -> {
                if (r == BattleResult.VICTORY) {
                    DeveloperLogger.log("Infiltration successful via " + route);
                    List<Dialogue> postRoute = switch (route) {
                        case STEALTH -> List.of(
                                new Dialogue("Narrator", "You slip past the curdled guards, unseen."),
                                new Dialogue("Nonna", "Smooth as melted provola, " + name + ".")
                        );
                        case DISTRACTION -> List.of(
                                new Dialogue("Narrator", "The guards are scattered like grated cheese."),
                                new Dialogue("Nonna", "Subtle as a parm hammer, but it works.")
                        );
                    };
                    dialogueService.runDialogues(postRoute, this::nextStep);
                } else {
                    failAndKick(r);
                }
            });

            SceneManager.get().switchTo(battle);
        });
    }


    private void bossBattle() {
        String name = GameState.get().getPlayer().getName();
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "A massive rind-encrusted creature looms ahead — its breath smells of age... and glory."),
                new Dialogue("Cheese Guardian", "You shall not take the Sacred Wedge, mortal."),
                new Dialogue(name, "Great. A lactose-intolerant gatekeeper."),
                new Dialogue("Nonna", "Mind your manners, " + name + ". That thing’s older than my sourdough starter.")
        ), () -> {
            Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createCheeseGuardian(playerLevel));
            battle.setOnBattleEnd(r -> {
                if (r == BattleResult.VICTORY) {
                    GameState.get().setMissionFlag(MissionType.MISSION_3);
                    MessageDialog.showMessageDialog(gui, "Item Found", "You found the Frozen Peas book!");
                    GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_glacialspike"));
                    DeveloperLogger.log("Boss defeated");
                    SceneManager.get().switchTo(this);
                } else {
                    failAndKick(r);
                }
            });
            SceneManager.get().switchTo(battle);
        });
    }

    private void outroDialogue() {
        String name = GameState.get().getPlayer().getName();
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "The crystals crackle behind you. In your hands, a golden wedge of Parmigiano."),
                new Dialogue(name, "One more step toward Nonna's ultimate lasagna."),
                new Dialogue("Nonna", "Bravissimo. Now all we need is a sauce thick enough to silence a debate.")
        ), this::nextStep);
    }

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", r == BattleResult.DEFEAT ?
                        "You fall in the cheese caverns, buried under regret and rind." :
                        "You flee the Parmigiano Mines, empty-handed. Nonna will not forget this.")
        ), () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }

}
