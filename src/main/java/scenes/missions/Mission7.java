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

public class Mission7 implements Scene {
    private final MultiWindowTextGUI gui;
    private final DialogueService dialogueService;
    private int step = 0;
    private int playerLevel;

    public Mission7(MultiWindowTextGUI gui) {
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
            case 1 -> ranchEncounter();
            case 2 -> finalBattle();
            case 3 -> outroDialogue();
            case 4 -> SceneManager.get().switchTo(new scenes.worldhub.WorldHub(gui, GameState.get().getPlayer()));
        }
    }

    private void introDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "The ragù demands its second meat: the elusive Maremmana Ox."),
                        new Dialogue("Nonna", "They say one steak from this beast feeds a village. But we only need enough for flavor."),
                        new Dialogue("Hero", "And where exactly do we find it?"),
                        new Dialogue("Nonna", "Head to Valle di Bistecca. And bring salt. For the meat — and for your attitude."),
                        new Dialogue("Narrator", "The sun beats down on the grassy pastures, where the oxen roam wild and wary."),
                        new Dialogue("Nonna", "This isn’t a slaughter. It’s a selection. Only the worthy may stew.")),
                this::nextStep);
    }

    private void ranchEncounter() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createRicottelliChef(playerLevel));

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                DeveloperLogger.log("Defeated Beef Rustler");
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });

        SceneManager.get().switchTo(battle);
    }

    private void finalBattle() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createLinguiniKnight(playerLevel));

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                GameState.get().setMissionFlag(MissionType.MISSION_7);
                DeveloperLogger.log("Secured Maremmana Ox");
                SceneManager.get().switchTo(this);
            } else {
                failAndKick(r);
            }
        });

        SceneManager.get().switchTo(battle);
    }

    private void outroDialogue() {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "With strength, seasoning, and sass, the Maremmana Ox is yours."),
                        new Dialogue("Hero", "Nonna, the ragù has its second soul."),
                        new Dialogue("Nonna", "Now it simmers. Now it sings. You’ve earned a stir."),
                        new Dialogue("Nonna", "And remember: real sauce is patient. Like me, when I was younger and less disappointed."),
                        new Dialogue("Narrator", "The Bolognese is nearly complete. One dish, one legend, one step away...")),
                this::nextStep);
    }

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", r == BattleResult.DEFEAT ?
                                "You are flattened by a charging ox. Nonna sends flowers — and a recipe you’ll never complete." :
                                "You flee Valle di Bistecca with hoofprints on your pride.")),
                () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }
}
