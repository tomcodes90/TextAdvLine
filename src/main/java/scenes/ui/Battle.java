package scenes.ui;

import battle.TurnManager;
import battle.actions.*;
import characters.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import items.Item;
import lombok.Setter;
import lombok.SneakyThrows;
import scenes.Scene;
import util.DeveloperLogger;
import util.PlayerLogger;

import java.io.IOException;
import java.util.List;

public class Battle implements Scene {

    private final MultiWindowTextGUI gui;
    private final Player player;
    private final Enemy enemy;

    private final BasicWindow win = new BasicWindow("Battle");
    private final TextBox logBox = new TextBox(new TerminalSize(50, 3), TextBox.Style.MULTI_LINE);
    private final Panel action = new Panel(new LinearLayout(Direction.VERTICAL));
    @Setter
    private Runnable onBattleEnd;

    public Battle(MultiWindowTextGUI gui, Player player, Enemy enemy) {
        this.gui = gui;
        this.player = player;
        this.enemy = enemy;

        logBox.setReadOnly(true);
        logBox.setTheme(new SimpleTheme(TextColor.ANSI.DEFAULT, TextColor.ANSI.BLACK));
        PlayerLogger.init(logBox, gui, () -> ActionMenu.refreshSafe(gui));
    }

    @Override

    public void enter() {
        DeveloperLogger.log("entered battle");

        win.setHints(List.of(Window.Hint.CENTERED));
        win.setComponent(buildRoot());
        PlayerLogger.init(logBox, gui, () -> ActionMenu.refreshSafe(gui));

        TurnManager tm = new TurnManager(player, enemy);
        tm.setPromptCallback(() ->
                gui.getGUIThread().invokeLater(() ->
                        ActionMenu.showActionsMenu(tm, player, enemy, action, gui)
                )
        );
        tm.setOnBattleEnd(() ->
                gui.getGUIThread().invokeLater(() -> finishBattle(tm.getResult(), enemy))
        );

        new Thread(tm::startBattle, "battle-loop").start(); // ✅ logic thread

        // 👇 This MUST stay on the main thread!
        gui.addWindowAndWait(win); // ✅ actual UI loop
    }


    @Override
    public void handleInput() {

    }

    @Override
    public void exit() {

    }

    private Component buildRoot() {
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));
        root.addComponent(EntityCard.getPCard());
        root.addComponent(buildMiddlePane());
        root.addComponent(EntityCard.getECard());
        return root;
    }

    private Component buildMiddlePane() {
        Panel mid = new Panel(new LinearLayout(Direction.VERTICAL));

        Label logLabel = new Label("Battle Log");
        logLabel.setForegroundColor(TextColor.ANSI.WHITE);
        mid.addComponent(logLabel);

        mid.addComponent(logBox);
        mid.addComponent(new Separator(Direction.HORIZONTAL));

        action.setPreferredSize(new TerminalSize(50, 4));
        mid.addComponent(action);

        return mid;
    }

    @SneakyThrows
    private void finishBattle(BattleResult result, Enemy defeated) {
        String msg = switch (result) {
            case FLED -> "\n🏃  You fled the battle.";
            case VICTORY -> "\n🏆  " + player.getName() + " wins!";
            case DEFEAT -> "\n💀  " + enemy.getName() + " wins!";
        };
        PlayerLogger.logBlocking(msg);

        try {
            gui.getGUIThread().invokeAndWait(() -> {
                try {
                    gui.updateScreen();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception ignored) {
        }

        win.close();

        boolean playerWon = result == BattleResult.VICTORY;
        boolean fled = result == BattleResult.FLED;

        String title = playerWon ? "Victory" : fled ? "Fled" : "Defeat";
        BasicWindow resultWin = new BasicWindow(title);

        Panel pane = new Panel(new LinearLayout(Direction.VERTICAL));
        if (fled) {
            pane.addComponent(new Label("You fled the battle."));
        } else {
            pane.addComponent(new Label(playerWon ? "You win!" : "You lose."));
        }
        pane.addComponent(new EmptySpace());

        if (playerWon) {
            addExpAndLootToPanel(pane, defeated);
            pane.addComponent(new EmptySpace());
        }

        Button cont = new Button("Continue", resultWin::close);
        cont.takeFocus();
        pane.addComponent(cont);

        resultWin.setComponent(pane);
        resultWin.setHints(List.of(Window.Hint.CENTERED, Window.Hint.MODAL));
        resultWin.setFixedSize(new TerminalSize(40, 12));

        gui.addWindowAndWait(resultWin);

        // ✅ Go back to MainMenu
        if (onBattleEnd != null) onBattleEnd.run();
    }

    private void addExpAndLootToPanel(Panel pane, Enemy defeated) {
        int xp = defeated.getExpReward();
        pane.addComponent(new Label("EXP gained: " + xp));
        player.collectExp(xp);

        List<Item> loot = defeated.getLootReward();
        if (loot.isEmpty()) {
            pane.addComponent(new Label("No loot found."));
            return;
        }

        pane.addComponent(new Label("Loot:"));
        for (Item item : loot) {
            player.addItemToInventory(item);
            pane.addComponent(new Label(" • " + item.getName()));
        }
    }
}
