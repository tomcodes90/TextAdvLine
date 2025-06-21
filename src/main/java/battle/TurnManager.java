package battle;

import battle.actions.BattleAction;
import battle.actions.BattleResult;
import characters.*;
import util.DeveloperLogger;
import util.PlayerLogger;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;

@Getter
public class TurnManager {

    private final Player player;
    private final Enemy enemy;
    @Setter
    private Runnable onBattleEnd;
    @Setter
    private Runnable promptCallback;
    @Setter
    private Consumer<Runnable> endCallback;

    @Setter
    private boolean battleOver = false;
    private final AtomicBoolean finished = new AtomicBoolean(false);
    private volatile boolean playerFled = false;
    @Getter
    private BattleResult result;


    public void onPlayerFlee() {
        playerFled = true;
        battleOver = true;
    }

    private final BlockingQueue<BattleAction> playerActionQueue = new ArrayBlockingQueue<>(1);

    public TurnManager(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    public void queuePlayerAction(BattleAction action) {
        boolean ok = playerActionQueue.offer(action);
        DeveloperLogger.log(Level.INFO,
                ok ? "[TurnManager] Queued " + action.name()
                        : "[TurnManager] Queue FULL – click ignored");
    }

    public void startBattle() {
        try {
            runLoop();
            // Log outcome
            if (battleOver) {
                PlayerLogger.log("🏃 The player fled the battle.");
            } else if (player.isAlive()) {
                PlayerLogger.log("🏆 " + player.getName() + " wins!");
            } else {
                PlayerLogger.log("💀 " + enemy.getName() + " wins!");
            }

// Trigger UI callback
            if (onBattleEnd != null) {
                fireBattleEnd();
            }
// ⬅ move the old body into this private method
        } catch (Throwable t) {      // *anything* that crosses this line is printed
            DeveloperLogger.log(Level.SEVERE,
                    "[TurnManager] UNCAUGHT EXCEPTION – battle thread died:\n" +
                            t.getClass().getSimpleName() + ": " + t.getMessage());
            t.printStackTrace();     // still goes to your IDE / console
        }
    }


    private void runLoop() {

        // ⬇️  show the very-first menu immediately
        if (promptCallback != null) {
            DeveloperLogger.log("[TurnManager] Showing INITIAL prompt");
            promptCallback.run();
        }

        PlayerLogger.log("\n         ⚔️ The battle begins!");
        DeveloperLogger.log("[TurnManager] battleOver=" + battleOver);

        DeveloperLogger.log(Level.INFO, "loop entered"); // ← Here

        while (player.isAlive() && enemy.isAlive() && !battleOver) {
            playerActionQueue.clear();

            if (promptCallback != null) {
                DeveloperLogger.log("[TurnManager] Running promptCallback");
                promptCallback.run();
            }

            BattleAction playerAction;
            try {
                DeveloperLogger.log(Level.INFO,
                        "[TurnManager] Waiting… queue size=" + playerActionQueue.size());

                playerAction = playerActionQueue.take();

                DeveloperLogger.log(Level.INFO,
                        "[TurnManager] …got " + playerAction.getClass().getSimpleName());

            } catch (InterruptedException e) {
                DeveloperLogger.log("[TurnManager] Battle interrupted");
                return;
            }

            BattleAction enemyAction = enemy.getAiRole().play(enemy, player);

            boolean playerFirst = player.getStat(StatsType.SPEED) > enemy.getStat(StatsType.SPEED)
                    || (player.getStat(StatsType.SPEED) == enemy.getStat(StatsType.SPEED) && Math.random() < 0.5);

            if (playerFirst) {
                execute(player, playerAction);
                if (enemy.isAlive() && !battleOver) {
                    execute(enemy, enemyAction);
                }
            } else {
                execute(enemy, enemyAction);
                if (player.isAlive() && !battleOver) {
                    execute(player, playerAction);
                }
            }

            player.tickStatusEffects();
            enemy.tickStatusEffects();

            // 🟡 This is the key: re-prompt for next turn!
            if (!battleOver && player.isAlive() && enemy.isAlive()) {
                if (promptCallback != null) {
                    DeveloperLogger.log("[TurnManager] Re-displaying prompt after actions");
                    promptCallback.run();
                }
            }
        }

        /* battle finished – decide result */
        if (!player.isAlive()) {
            result = BattleResult.DEFEAT;
        } else if (!enemy.isAlive()) {
            result = BattleResult.VICTORY;
        } else if (battleOver) { // assuming flee sets this
            result = BattleResult.FLED;
        }

        /* notify UI exactly once */
        if (onBattleEnd != null) onBattleEnd.run();

    }

    private void execute(Entity actor, BattleAction action) {
        PlayerLogger.log("\n         " + actor.getName() + " uses " + action.name());
        action.execute();

        DeveloperLogger.log("[TurnManager] After action: "
                + player.getName() + " HP=" + player.getStat(StatsType.HP)
                + ", " + enemy.getName() + " HP=" + enemy.getStat(StatsType.HP));

        // stop the loop when someone’s HP ≤ 0
        if (!player.isAlive() || !enemy.isAlive()) {
            battleOver = true;
        }
    }

    private void fireBattleEnd() {
        if (finished.compareAndSet(false, true) && onBattleEnd != null) {
            fireBattleEnd();
        }
    }
}
