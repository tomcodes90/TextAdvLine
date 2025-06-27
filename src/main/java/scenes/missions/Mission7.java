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
        String name = GameState.get().getPlayer().getName();
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "The ragù demands its second meat: the elusive Maremmana Ox."),
                new Dialogue("Nonna", "They say one steak from this beast feeds a village. But we only need enough for flavor."),
                new Dialogue(name, "And where exactly do we find it?"),
                new Dialogue("Nonna", "Head to Valle di Bistecca. And bring salt. For the meat — and for your attitude."),
                new Dialogue("Narrator", "The sun beats down on the grassy pastures, where the oxen roam wild and wary."),
                new Dialogue("Nonna", "This isn’t a slaughter. It’s a selection. Only the worthy may stew.")
        ), this::nextStep);
    }

    private void ranchEncounter() {
        String name = GameState.get().getPlayer().getName();
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createRicottelliChef(playerLevel));

        battle.setOnBattleEnd(r -> {
            if (r == BattleResult.VICTORY) {
                DeveloperLogger.log("Defeated Beef Rustler");
                dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "The rustler drops his pan and scurries off, muttering about underseasoned defeat."),
                        new Dialogue(name, "What kind of chef carries a branding iron...?"),
                        new Dialogue("Nonna", "The kind that salts wounds and steaks alike. Now move — something bigger is coming.")
                ), this::nextStep);
            } else {
                failAndKick(r);
            }
        });

        SceneManager.get().switchTo(battle);
    }

    private void finalBattle() {
        String name = GameState.get().getPlayer().getName();
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "A shadow blocks the sun. No, not a storm — muscle."),
                new Dialogue("Linguini Knight", "Step away from the ox, peasant. Only nobles dine on legends."),
                new Dialogue(name, "You brought a lance to a sauce fight?"),
                new Dialogue("Linguini Knight", "This isn’t just a lance. It’s a pasta-press pike. Forged in gluten, honed in war."),
                new Dialogue("Nonna", "Careful, " + name + ". He’s got a recipe... for ruin.")
        ), () -> {
            Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createLinguiniKnight(playerLevel));
            battle.setOnBattleEnd(r -> {
                if (r == BattleResult.VICTORY) {
                    GameState.get().setMissionFlag(MissionType.MISSION_7);
                    MessageDialog.showMessageDialog(gui, "Item Found", "You found the GarlicNova book!");
                    GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_garlicnova"));
                    DeveloperLogger.log("Secured Maremmana Ox");
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
                new Dialogue("Narrator", "With strength, seasoning, and sass, the Maremmana Ox is yours."),
                new Dialogue(name, "Nonna, the ragù has its second soul."),
                new Dialogue("Nonna", "Now it simmers. Now it sings. You’ve earned a stir."),
                new Dialogue("Nonna", "And remember: real sauce is patient. Like me, when I was younger and less disappointed."),
                new Dialogue("Narrator", "The Bolognese is nearly complete. One dish, one legend, one step away...")
        ), this::nextStep);
    }

    private void failAndKick(BattleResult r) {
        dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", r == BattleResult.DEFEAT ?
                                "You are flattened by a charging ox. Nonna sends flowers — and a recipe you’ll never complete." :
                                "You flee Valle di Bistecca with hoofprints on your pride.")),
                () -> SceneManager.get().switchTo(new scenes.menu.MainMenu(gui)));
    }
}
