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

public class Mission6 implements Scene {
    private final MultiWindowTextGUI gui;
    private final DialogueService dialogueService;
    private int step = 0;
    private int playerLevel;

    public Mission6(MultiWindowTextGUI gui) {
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
            case 1 -> firstBattle();
            case 2 -> trackPigBattle();
            case 3 -> outroDialogue();
            case 4 -> SceneManager.get().switchTo(new scenes.worldhub.WorldHub(gui, GameState.get().getPlayer()));
        }
    }

    private void introDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "The Bolognese must be perfect. And for that, you need meat — not just any, but the legendary Cinta Noble Pig."),
                        new Dialogue("Nonna", "You don’t just chase pigs, ragazzo. You court them. These swine have standards."),
                        new Dialogue("Hero", "You want me to impress a pig?"),
                        new Dialogue("Nonna", "If it runs cleaner than your knife skills, yes."),
                        new Dialogue("Narrator", "The forests of Monte Prosciutto await. And they don’t like visitors with empty spice jars."),
                        new Dialogue("Nonna", "Bring garlic. Bring charm. And for the love of mozzarella, don’t step in anything.")),
                this::nextStep);
    }

    private void firstBattle() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createBoarHunter(playerLevel));

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                DeveloperLogger.log("Defeated Wild Boar Hunter");
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });

        SceneManager.get().switchTo(battle);
    }

    private void trackPigBattle() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createPigGuardian(playerLevel));

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                GameState.get().setMissionFlag(MissionType.MISSION_6);
                DeveloperLogger.log("Secured Cinta Noble Pig");
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });

        SceneManager.get().switchTo(battle);
    }

    private void outroDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "With cunning, courage, and a bit of garlic, you’ve captured the prized Cinta Noble Pig."),
                        new Dialogue("Hero", "Nonna, we’ve got pork."),
                        new Dialogue("Nonna", "Good. Half the Bolognese is ready. The other half? Let’s just say it moos."),
                        new Dialogue("Nonna", "Now wash your hands and your conscience — that pig had better lived a good life."),
                        new Dialogue("Narrator", "The journey continues, as the ragù demands its second sacrifice...")),
                this::nextStep);
    }

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", r == BattleResult.DEFEAT ?
                                "You are trampled by a herd of insulted pigs. Nonna is not pleased." :
                                "You flee Monte Prosciutto with only shame and mud on your boots.")),
                () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }
}
