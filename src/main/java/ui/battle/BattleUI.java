package ui.battle;

import battle.TurnManager;
import battle.actions.*;
import characters.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import items.Item;
import lombok.SneakyThrows;
import util.PlayerLogger;

import java.io.IOException;
import java.util.List;


public class BattleUI {

    /* ------------------------------------------------------------------ */
    /*  FIELDS                                                             */
    /* ------------------------------------------------------------------ */
    private final WindowBasedTextGUI gui;
    private final Player player;
    private final Enemy enemy;

    private final BasicWindow win = new BasicWindow("Battle");

    private final TextBox logBox =
            new TextBox(new TerminalSize(50, 3), TextBox.Style.MULTI_LINE);

    private final Panel action = new Panel(new LinearLayout(Direction.VERTICAL));

    /**
     * Called by whoever created the BattleUI when the fight finishes
     */
    private Runnable onBattleEnd;

    public void setOnBattleEnd(Runnable cb) {
        this.onBattleEnd = cb;
    }

    /* ------------------------------------------------------------------ */
    public BattleUI(WindowBasedTextGUI gui, Player player, Enemy enemy) {
        this.gui = gui;
        this.player = player;
        this.enemy = enemy;

        logBox.setReadOnly(true);
        logBox.setTheme(new SimpleTheme(TextColor.ANSI.DEFAULT, TextColor.ANSI.BLACK));

        // tie PlayerLogger to the on-screen battle log
        PlayerLogger.init(logBox, () -> BattleMenuHelper.refreshSafe(gui));
    }

    /* ------------------------------------------------------------------ */
    /*  PUBLIC ENTRY POINT                                                 */
    /* ------------------------------------------------------------------ */
    public void start() {
        win.setHints(List.of(Window.Hint.CENTERED));
        win.setComponent(buildRoot());

        TurnManager tm = new TurnManager(player, enemy);

        tm.setPromptCallback(() ->
                gui.getGUIThread().invokeLater(() -> BattleMenuHelper.showMainMenu(tm, player, enemy, action, gui))
        );

        tm.setOnBattleEnd(() ->
                gui.getGUIThread().invokeLater(() ->
                        finishBattle(tm.getResult(), enemy)
                ));


        // Show battle UI (BLOCKS here!)
        new Thread(() -> {
            gui.getGUIThread().invokeLater(() -> gui.addWindowAndWait(win));
        }, "gui-thread").start();

        // Run battle loop
        new Thread(tm::startBattle, "battle-thread").start();
    }


    /* ------------------------------------------------------------------ */
    /*  BUILD ROOT LAYOUT                                                  */
    /* ------------------------------------------------------------------ */
    private Component buildRoot() {
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));
        root.addComponent(CardHelper.getPCard());
        root.addComponent(buildMiddlePane());
        root.addComponent(CardHelper.getECard());
        return root;
    }

    private Component buildMiddlePane() {
        Panel mid = new Panel(new LinearLayout(Direction.VERTICAL));

        Label logLabel = new Label("Battle Log");
        logLabel.setForegroundColor(TextColor.ANSI.WHITE);
        mid.addComponent(logLabel);

        logBox.setTheme(new SimpleTheme(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK));
        mid.addComponent(logBox);

        mid.addComponent(new Separator(Direction.HORIZONTAL));

        action.setPreferredSize(new TerminalSize(50, 4));
        // ❌ Don’t theme this — let Lanterna handle focus highlights!
        // action.setTheme(...) ← REMOVE this line if you have it
        mid.addComponent(action);

        return mid;
    }


    /* ------------------------------------------------------------------ */
    /*  MENUS                                                              */
    /* ------------------------------------------------------------------ */


    /* ------------------------------------------------------------------ */
    /*  RESULT SCREENS                                                     */
    /* ------------------------------------------------------------------ */
    @SneakyThrows
    private void finishBattle(BattleResult result, Enemy defeated) {
        // 1️⃣ Final log line
        String msg = switch (result) {
            case FLED -> "🏃  You fled the battle.";
            case VICTORY -> "🏆  " + player.getName() + " wins!";
            case DEFEAT -> "💀  " + enemy.getName() + " wins!";
        };
        PlayerLogger.logBlocking(msg);           // waits for typing to finish

        // 2️⃣ Force one repaint so the line is visible
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

        // 3️⃣ Close battle window & continue with your result modal …
        win.close();

        // 3️⃣ Setup result modal window
        boolean playerWon = result == BattleResult.VICTORY;
        boolean fled = result == BattleResult.FLED;

        String title = playerWon ? "Victory" : fled ? "Fled" : "Defeat";
        BasicWindow resultWin = new BasicWindow(title);

        Panel pane = new Panel(new LinearLayout(Direction.VERTICAL));

        // Outcome text
        if (fled) {
            pane.addComponent(new Label("You fled the battle."));
        } else {
            pane.addComponent(new Label(playerWon ? "You win!" : "You lose."));
        }
        pane.addComponent(new EmptySpace());

        // Rewards if player won
        if (playerWon) {
            addExpAndLootToPanel(pane, defeated);
            pane.addComponent(new EmptySpace());
        }

        // Continue button
        Button cont = new Button("Continue", resultWin::close);
        cont.takeFocus();
        pane.addComponent(cont);

        resultWin.setComponent(pane);
        resultWin.setHints(List.of(Window.Hint.CENTERED, Window.Hint.MODAL));
        resultWin.setFixedSize(new TerminalSize(40, 12));

        gui.addWindowAndWait(resultWin);

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


    /* ------------------------------------------------------------------ */
    /*  CARDS & HP BAR                                                     */
    /* ------------------------------------------------------------------ */


    /* ------------------------------------------------------------------ */

}
