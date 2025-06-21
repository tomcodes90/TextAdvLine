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
            new TextBox(new TerminalSize(50, 3), TextBox.Style.MULTI_LINE);

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
        root.addComponent(pCard);
        root.addComponent(buildMiddlePane());
        root.addComponent(eCard);
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
    private void showMainMenu(TurnManager tm) {
        refreshCards();
        action.removeAllComponents();

        ActionListBox menu = new ActionListBox(new TerminalSize(50, 4));
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
    private void finishBattle(BattleResult result, Enemy defeated) {
        win.close();

        boolean playerWon = result == BattleResult.VICTORY;
        boolean fled = result == BattleResult.FLED;

        BasicWindow resultWin = new BasicWindow(playerWon ? "Victory"
                : fled ? "Fled"
                : "Defeat");

        /* ---------- content ---------- */
        Panel pane = new Panel(new LinearLayout(Direction.VERTICAL));

        if (fled) {
            pane.addComponent(new Label("You fled the battle."));
        } else {
            pane.addComponent(new Label(playerWon ? "You win!" : "You lose."));
        }
        pane.addComponent(new EmptySpace());

        if (playerWon) {
            int xp = defeated.getExpReward();
            pane.addComponent(new Label("EXP gained: " + xp));
            player.collectExp(xp);

            List<Item> loot = defeated.getLootReward();
            if (!loot.isEmpty()) {
                pane.addComponent(new Label("Loot:"));
                for (Item it : loot) {
                    player.addItemToInventory(it);
                    pane.addComponent(new Label(" • " + it.getName()));
                }
            } else {
                pane.addComponent(new Label("No loot found."));
            }
            pane.addComponent(new EmptySpace());
        }

        Button cont = new Button("Continue", resultWin::close);
        cont.takeFocus();
        pane.addComponent(cont);

        resultWin.setComponent(pane);

        /* ---------- centre the window ---------- */
        resultWin.setHints(List.of(Window.Hint.CENTERED, Window.Hint.MODAL));
        // give it a sensible size so CENTERED works
        resultWin.setFixedSize(new TerminalSize(40, 12));   // ← key line

        gui.addWindowAndWait(resultWin);

        if (onBattleEnd != null) onBattleEnd.run();
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
        addRow(g, "INT", e.getStat(StatsType.INTELLIGENCE));
        addRow(g, "DEF", e.getStat(StatsType.DEFENSE));
        addRow(g, "SPD", e.getStat(StatsType.SPEED));
        return g.withBorder(Borders.singleLine(title));
    }

    private void addRow(Panel p, String k, Object v) {
        p.addComponent(new Label(k + ":"));
        p.addComponent(new Label(String.valueOf(v)));
    }

    private void addRow(Panel panel, String label, String value) {
        panel.addComponent(new Label(label));
        panel.addComponent(new Label(value));
    }

    private void addRow(Panel panel, String label, Component value) {
        panel.addComponent(new Label(label));
        panel.addComponent(value);
    }

    private Component hpBar(Entity e) {
        int hp = e.getStat(StatsType.HP);
        int max = e.getStat(StatsType.MAX_HP);
        int filled = (int) Math.round((hp / (double) max) * 10);

        /* ---------------- text parts ---------------- */
        String hpText = hp + "/" + max + " ";
        StringBuilder barSb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) barSb.append(i < filled ? "#" : "-");
        barSb.append(']');

        /* ---------------- choose bar colour ---------------- */
        double pct = hp / (double) max;
        TextColor barColour = (pct >= 0.6) ? TextColor.ANSI.GREEN
                : (pct >= 0.3) ? TextColor.ANSI.CYAN
                : TextColor.ANSI.RED;

        /* ---------------- assemble component ---------------- */
        Panel row = new Panel(new LinearLayout(Direction.HORIZONTAL));

        // 1️⃣ hp/max in default colour
        row.addComponent(new Label(hpText));

        // 2️⃣ coloured bar
        Label barLbl = new Label(barSb.toString());
        barLbl.setForegroundColor(barColour);
        row.addComponent(barLbl);

        return row;
    }


    /* ------------------------------------------------------------------ */
    private void refreshSafe() {
        try {
            gui.updateScreen();
        } catch (IOException ignored) { /* swallow */ }
    }
}
