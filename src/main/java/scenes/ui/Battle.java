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
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.menu.MainMenu;
import scenes.worldhub.WorldHub;
import util.DeveloperLogger;
import util.PlayerLogger;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class Battle implements Scene {

    private final MultiWindowTextGUI gui;
    private final Player player;
    private final Enemy enemy;

    private final BasicWindow win = new BasicWindow("Battle");
    private final TextBox logBox = new TextBox(new TerminalSize(50, 3), TextBox.Style.MULTI_LINE);
    private final Panel action = new Panel(new LinearLayout(Direction.VERTICAL));
    @Setter
    private Consumer<BattleResult> onBattleEnd;

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
        tm.setOnBattleEnd(result ->

                gui.getGUIThread().invokeLater(() -> finishBattle(result, enemy))
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
        pane.setPreferredSize(new TerminalSize(40, 12)); // Fixed size

// Create a vertically centered inner panel
        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

// === Add centered content to `content` panel ===
        content.addComponent(centeredLabel(playerWon ? "You win!" : fled ? "You fled the battle." : "You lose."));
        content.addComponent(new EmptySpace());

        if (playerWon) {
            addExpAndLootToPanel(content, defeated);
            content.addComponent(new EmptySpace());
        }

        Button cont = new Button("Continue", resultWin::close);
        cont.takeFocus();
        content.addComponent(cont);

// === Add the content panel to the outer pane ===
        pane.addComponent(new EmptySpace()); // top padding
        pane.addComponent(content);
        pane.addComponent(new EmptySpace()); // bottom padding


        resultWin.setComponent(pane);
        resultWin.setHints(List.of(Window.Hint.CENTERED, Window.Hint.MODAL));
        resultWin.setFixedSize(new TerminalSize(40, 12));

        gui.addWindowAndWait(resultWin);
        if (result != BattleResult.DEFEAT && player.isAlive()) {
            restorePlayerHealth(player);
        }

        // ✅ Go back to MainMenu
        switch (result) {
            case VICTORY, FLED -> {
                restorePlayerHealth(player);
                if (onBattleEnd != null) {
                    onBattleEnd.accept(result); // don't touch
                } else {
                    SceneManager.get().switchTo(new WorldHub(gui, player));
                }
            }
            case DEFEAT -> {
                SceneManager.get().switchTo(new MainMenu(gui)); // ✅ Game Over → Main Menu
            }
        }
    }

    private void addExpAndLootToPanel(Panel pane, Enemy defeated) {
        int xp = defeated.getExpReward();
        pane.addComponent(new Label("EXP +" + xp));
        player.collectExp(xp);
        int gold = defeated.getGoldReward();
        pane.addComponent(new Label("GOLD +" + gold));
        pane.addComponent(new Label(" "));
        player.collectGold(gold);

        List<Item> loot = defeated.getLootReward();
        if (loot.isEmpty()) {
            pane.addComponent(new Label("No loot found."));
            return;
        }

        pane.addComponent(new Label("Loot \n"));
        for (Item item : loot) {
            player.addItemToInventory(item);
            pane.addComponent(new Label(" => " + item.getName()));
        }
    }

    public static void restorePlayerHealth(Player p) {
        p.setStat(StatsType.HP, p.getStat(StatsType.MAX_HP));
    }

    private Label centeredLabel(String text) {
        Label label = new Label(text);
        label.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        return label;
    }

}
