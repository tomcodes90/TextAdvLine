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
/*
  ⚔️ Battle Scene: Handles turn-based combat between the player and a single enemy.
  <p>
  🧩 Structure Overview:
  - Root Layout: Horizontal panel with three parts:
  🔹 PlayerCard (left) → displays player's stats/portrait
  🔹 Middle panel (center) → includes battle log and action menu
  🔹 EnemyCard (right) → displays enemy's stats/portrait
  <p>
  🔁 Combat Flow:
  - Uses {@link TurnManager} to control turn order and execute player/enemy actions.
  - UI updates and input run on the main thread (Lanterna restriction),
  while battle logic runs in a separate thread ("battle-loop").
  <p>
  💬 Log + UI:
  - logBox shows text updates during battle using {@link PlayerLogger}.
  - action panel is dynamically filled with buttons depending on available actions.
  <p>
  ✅ Battle Ends:
  - On end, shows modal with result (Victory/Fled/Defeat).
  - If won or fled → returns to {@link scenes.worldhub.WorldHub}.
  - If defeated → returns to {@link scenes.menu.MainMenu}.
  - Optional: `onBattleEnd` callback allows mission progression logic to override post-battle behavior.
  <p>
  🎁 Rewards:
  - On victory, displays earned EXP, gold, and any dropped items.
  - Automatically heals the player (except on defeat).
  <p>
  🧠 Notes:
  - Do not touch the `onBattleEnd` logic block; it is needed by missions.
  - Uses {@link EntityCard} to render the player/enemy display.
  - {@code restorePlayerHealth()} is static for reuse after battles.
 */


/**
 * ⚔️ Battle Scene: Handles turn-based combat between the player and a single enemy.
 */
public class Battle implements Scene {

    private final MultiWindowTextGUI gui;
    private final Player player;
    private final Enemy enemy;

    private final BasicWindow win = new BasicWindow("Battle");
    private final TextBox logBox = new TextBox(new TerminalSize(50, 3), TextBox.Style.MULTI_LINE);
    private final Panel action = new Panel(new LinearLayout(Direction.VERTICAL));

    @Setter
    private Consumer<BattleResult> onBattleEnd; // Optional callback to control post-battle behavior

    /**
     * Constructor initializes GUI components and logger.
     */
    public Battle(MultiWindowTextGUI gui, Player player, Enemy enemy) {
        this.gui = gui;
        this.player = player;
        this.enemy = enemy;

        logBox.setReadOnly(true);
        logBox.setTheme(new SimpleTheme(TextColor.ANSI.DEFAULT, TextColor.ANSI.BLACK));
        PlayerLogger.init(logBox, gui, () -> ActionMenu.refreshSafe(gui));
    }

    /**
     * Entry point for the battle scene. Creates UI layout, sets up turn manager and starts logic thread.
     */
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

        new Thread(tm::startBattle, "battle-loop").start(); // Launch turn logic in separate thread
        gui.addWindowAndWait(win); // Start the GUI loop
    }

    @Override
    public void exit() {
        // Currently no exit logic needed
    }

    /**
     * Constructs the full UI layout for the battle window.
     */
    private Component buildRoot() {
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));
        root.addComponent(EntityCard.getPCard()); // Player info card
        root.addComponent(buildMiddlePane());     // Log + action buttons
        root.addComponent(EntityCard.getECard()); // Enemy info card
        return root;
    }

    /**
     * Constructs the middle panel with battle log and action area.
     */
    private Component buildMiddlePane() {
        Panel mid = new Panel(new LinearLayout(Direction.VERTICAL));

        Label logLabel = new Label("Battle Log");
        logLabel.setForegroundColor(TextColor.ANSI.WHITE);
        mid.addComponent(logLabel);
        mid.addComponent(logBox); // Multi-line combat log
        mid.addComponent(new Separator(Direction.HORIZONTAL));

        action.setPreferredSize(new TerminalSize(50, 4));
        mid.addComponent(action); // Dynamic action buttons go here

        return mid;
    }

    /**
     * Finishes the battle and shows a modal with the result and rewards.
     */
    @SneakyThrows
    private void finishBattle(BattleResult result, Enemy defeated) {
        String msg = switch (result) {
            case FLED -> "\n\uD83C\uDFC3  You fled the battle.";
            case VICTORY -> "\n\uD83C\uDFC6  " + player.getName() + " wins!";
            case DEFEAT -> "\n\uD83D\uDC80  " + enemy.getName() + " wins!";
        };
        PlayerLogger.logBlocking(msg);

        try {
            gui.getGUIThread().invokeAndWait(() -> {
                try {
                    gui.updateScreen();
                } catch (IOException e) {
                    throw new RuntimeException(e); // re-wrap
                }
            });
        } catch (Exception ignored) {
        }


        win.close();

        boolean playerWon = result == BattleResult.VICTORY;
        boolean fled = result == BattleResult.FLED;

        // Result modal UI
        String title = playerWon ? "Victory" : fled ? "Fled" : "Defeat";
        BasicWindow resultWin = new BasicWindow(title);

        Panel pane = new Panel(new LinearLayout(Direction.VERTICAL));
        pane.setPreferredSize(new TerminalSize(40, 12));

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        content.addComponent(centeredLabel(playerWon ? "You win!" : fled ? "You fled the battle." : "You lose."));
        content.addComponent(new EmptySpace());

        if (playerWon) {
            addExpAndLootToPanel(content, defeated); // Show rewards
            content.addComponent(new EmptySpace());
        }

        Button cont = new Button("Continue", resultWin::close);
        cont.takeFocus();
        content.addComponent(cont);

        pane.addComponent(new EmptySpace());
        pane.addComponent(content);
        pane.addComponent(new EmptySpace());

        resultWin.setComponent(pane);
        resultWin.setHints(List.of(Window.Hint.CENTERED, Window.Hint.MODAL));
        resultWin.setFixedSize(new TerminalSize(40, 12));

        gui.addWindowAndWait(resultWin);

        // Heal only if player survived
        if (result != BattleResult.DEFEAT && player.isAlive()) {
            restorePlayerHealth(player);
        }

        // Go back to next screen depending on outcome
        switch (result) {
            case VICTORY, FLED -> {
                restorePlayerHealth(player);
                if (onBattleEnd != null) {
                    onBattleEnd.accept(result); // Callback for mission chaining
                } else {
                    SceneManager.get().switchTo(new WorldHub(gui, player));
                }
            }
            case DEFEAT -> SceneManager.get().switchTo(new MainMenu(gui));
        }
    }

    /**
     * Appends gained EXP, gold and item drops to the result modal.
     */
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

    /**
     * Fully heals the player after a battle.
     */
    public static void restorePlayerHealth(Player p) {
        p.setStat(StatsType.HP, p.getStat(StatsType.MAX_HP));
    }

    /**
     * Utility method for centering labels in UI.
     */
    private Label centeredLabel(String text) {
        Label label = new Label(text);
        label.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        return label;
    }
}
