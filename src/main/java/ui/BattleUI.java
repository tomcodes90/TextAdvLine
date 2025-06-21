package ui;

import battle.TurnManager;
import battle.actions.*;
import characters.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import items.Item;
import util.DeveloperLogger;
import util.PlayerLogger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class BattleUI {

    /* ------------------------------------------------------------------ */
    /*  FIELDS                                                             */
    /* ------------------------------------------------------------------ */
    private final WindowBasedTextGUI gui;
    private final Player player;
    private final Enemy enemy;

    private final BasicWindow win = new BasicWindow("Battle");

    private final TextBox logBox =
            new TextBox(new TerminalSize(50, 15), TextBox.Style.MULTI_LINE);

    private final Panel pCard = new Panel();                    // rebuilt every turn
    private final Panel eCard = new Panel();
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
        PlayerLogger.init(logBox, () -> gui.getGUIThread().invokeLater(this::refreshSafe));
    }

    /* ------------------------------------------------------------------ */
    /*  PUBLIC ENTRY POINT                                                 */
    /* ------------------------------------------------------------------ */
    public void start() {
        win.setHints(List.of(Window.Hint.CENTERED));
        win.setComponent(buildRoot());

        TurnManager tm = new TurnManager(player, enemy);

        tm.setPromptCallback(() ->
                gui.getGUIThread().invokeLater(() -> showMainMenu(tm))
        );

        tm.setOnBattleEnd(() -> {
                    DeveloperLogger.log("Finish battle invoked");
                    gui.getGUIThread().invokeLater(() -> finishBattle(player.isAlive(), enemy));
                }

        );

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
        root.addComponent(pCard);
        root.addComponent(buildMiddlePane());
        root.addComponent(eCard);
        return root;
    }

    private Component buildMiddlePane() {
        Panel mid = new Panel(new LinearLayout(Direction.VERTICAL));
        mid.addComponent(new Label("Battle Log"));
        mid.addComponent(logBox);
        mid.addComponent(new Separator(Direction.HORIZONTAL));
        action.setPreferredSize(new TerminalSize(50, 7));
        mid.addComponent(action);
        return mid;
    }

    /* ------------------------------------------------------------------ */
    /*  MENUS                                                              */
    /* ------------------------------------------------------------------ */
    private void showMainMenu(TurnManager tm) {
        refreshCards();
        action.removeAllComponents();

        ActionListBox menu = new ActionListBox(new TerminalSize(50, 7));
        menu.addItem("Attack", () -> tm.queuePlayerAction(new AttackAction(player, enemy)));
        menu.addItem("Cast Spell", () -> showSpellMenu(tm));
        menu.addItem("Use Item", () -> showItemMenu(tm));
        menu.addItem("Flee", () -> tm.queuePlayerAction(new FleeAction(player, tm)));

        action.addComponent(menu);
        gui.getGUIThread().invokeLater(menu::takeFocus);
        refreshSafe();
    }

    private void showSpellMenu(TurnManager tm) {
        action.removeAllComponents();
        ActionListBox list = new ActionListBox(new TerminalSize(50, 7));

        Arrays.stream(player.getSpellsEquipped())
                .filter(Objects::nonNull)
                .forEach(spell -> {
                    boolean ready = spell.isReady();
                    String label = ready
                            ? spell.getName().name()
                            : spell.getName().name() + " (Cooldown: " + spell.getCooldownCounter() + ")";
                    Runnable action = ready
                            ? () -> tm.queuePlayerAction(new CastSpellAction(player, spell, enemy))
                            : () -> PlayerLogger.log("⏳ Spell is on cooldown!");
                    list.addItem(label, action);
                });

        list.addItem("Back", () -> showMainMenu(tm));
        action.addComponent(list);
        gui.getGUIThread().invokeLater(list::takeFocus);
        refreshSafe();
    }


    private void showItemMenu(TurnManager tm) {
        action.removeAllComponents();
        ActionListBox list = new ActionListBox(new TerminalSize(50, 7));

        Arrays.stream(player.getConsumablesEquipped())
                .filter(Objects::nonNull)
                .forEach(item ->
                        list.addItem(item.getName(),
                                () -> tm.queuePlayerAction(new UseItemAction(player, item))));

        list.addItem("Back", () -> showMainMenu(tm));
        action.addComponent(list);
        gui.getGUIThread().invokeLater(list::takeFocus);
        refreshSafe();
    }

    /* ------------------------------------------------------------------ */
    /*  RESULT SCREENS                                                     */
    /* ------------------------------------------------------------------ */
    private void finishBattle(boolean playerWon, Enemy defeated) {
        win.close();

        BasicWindow result = new BasicWindow(playerWon ? "Victory" : "Defeat");
        Panel pane = new Panel(new LinearLayout(Direction.VERTICAL));

        pane.addComponent(new Label(playerWon ? "You win!" : "You lose."));
        pane.addComponent(new EmptySpace());

        Button close = new Button("Continue", result::close);
        close.takeFocus();  // ensures focus stays here
        pane.addComponent(close);

        result.setComponent(pane);
        gui.addWindowAndWait(result);

        if (onBattleEnd != null) onBattleEnd.run();
    }


    private BasicWindow buildResultWindow(boolean playerWon, Enemy defeated) {
        BasicWindow res = new BasicWindow(playerWon ? "Victory" : "Defeat");
        Panel pane = new Panel(new LinearLayout(Direction.VERTICAL));

        if (playerWon) {
            pane.addComponent(new Label("🏆  Victory!"));
            pane.addComponent(new Label("XP gained: " + defeated.getExpReward()));
            player.collectExp(defeated.getExpReward());
            if (!defeated.getLootReward().isEmpty()) {
                pane.addComponent(new Label("Loot:"));
                defeated.getLootReward().forEach(i -> {
                    pane.addComponent(new Label(" • " + i.getName()));
                    player.addItemToInventory(i);
                });
            } else pane.addComponent(new Label("No loot."));
        } else {
            pane.addComponent(new Label("☠  You were defeated…"));
        }

        pane.addComponent(new EmptySpace());
        pane.addComponent(new Button("Back to menu", () -> {
            res.close();                    // close result window
            if (onBattleEnd != null) onBattleEnd.run();   // show menu now
        }));

        res.setComponent(pane);
        res.setHints(List.of(Window.Hint.CENTERED));
        return res;
    }


    /* ------------------------------------------------------------------ */
    /*  CARDS & HP BAR                                                     */
    /* ------------------------------------------------------------------ */
    private void refreshCards() {
        pCard.removeAllComponents();
        eCard.removeAllComponents();
        pCard.addComponent(buildCard(player, "Player"));
        eCard.addComponent(buildCard(enemy, "Enemy"));
    }

    private Component buildCard(Entity e, String title) {
        Panel g = new Panel(new GridLayout(2));
        addRow(g, "Name", e.getName());
        addRow(g, "HP", hpBar(e));
        addRow(g, "ATK", e.getStat(StatsType.STRENGTH));
        addRow(g, "DEF", e.getStat(StatsType.DEFENSE));
        addRow(g, "SPD", e.getStat(StatsType.SPEED));
        return g.withBorder(Borders.singleLine(title));
    }

    private void addRow(Panel p, String k, Object v) {
        p.addComponent(new Label(k + ":"));
        p.addComponent(new Label(String.valueOf(v)));
    }

    /**
     * A simple coloured HP bar – green / yellow / red
     */
    /**
     * 10-cell ASCII bar, no colour codes
     */
    private String hpBar(Entity e) {
        int hp = e.getStat(StatsType.HP);
        int max = e.getStat(StatsType.MAX_HP);
        int filled = (int) Math.round((hp / (double) max) * 10);

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) sb.append(i < filled ? "#" : "-");
        sb.append("] ");
        sb.append(hp).append("/").append(max);
        return sb.toString();
    }


    /* ------------------------------------------------------------------ */
    private void refreshSafe() {
        try {
            gui.updateScreen();
        } catch (IOException ignored) { /* swallow */ }
    }
}
