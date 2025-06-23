package scenes.missions;



import characters.EnemyFactory;
import characters.Player;
import com.googlecode.lanterna.gui2.*;
import dialogues.Dialogue;
import dialogues.DialogueService;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.menu.MainMenu;
import scenes.ui.Battle;
import scenes.worldhub.WorldHub;
import state.GameState;
import items.MissionType;
import util.DeveloperLogger;


import java.util.List;

public class Mission1 implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    public Mission1(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {


        DialogueService.getInstance().runDialogues(List.of(
                new Dialogue("Narrator", "You head out into the forest near the village...", "H"),
                new Dialogue("Narrator", "Suddenly, a shadow jumps out from the trees!", "H"),
                new Dialogue("???", "Grrr... this is my territory!", "🐺")
        ), this::startBattle);
    }

    private void startBattle() {
        var enemy = EnemyFactory.createBandit(); // Assume you have this or similar
        Battle battle = new Battle((MultiWindowTextGUI) gui, player, enemy);

        battle.setOnBattleEnd(result -> {
            switch (result) {
                case VICTORY -> {
                    GameState.get().completeMission(MissionType.MISSION_1);
                    DeveloperLogger.log("Mission 1 has been completed" + GameState.get().getMissionFlag().toString());
                    SceneManager.get().switchTo(new WorldHub(gui, player));
                }
                case DEFEAT, FLED -> {
                    SceneManager.get().switchTo(new MainMenu((MultiWindowTextGUI) gui));
                }
            }
        });

        SceneManager.get().switchTo(battle);
    }

    @Override
    public void handleInput() {}

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}
