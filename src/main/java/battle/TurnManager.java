package battle;

import battle.actions.BattleAction;
import battle.actions.BattleResult;
import characters.*;
import spells.Spell;
import scenes.ui.DevLogOverlay;
import util.DeveloperLogger;
import util.PlayerLogger;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Getter
public class TurnManager {

    private final Player player;
    private final Enemy enemy;

    private Consumer<BattleResult> onBattleEnd;
    @Setter
    private Runnable promptCallback;

    @Setter
    private boolean battleOver = false;
    private final AtomicBoolean finished = new AtomicBoolean(false);
    private volatile boolean playerFled = false;
    @Getter
    private BattleResult result;
 ;

    public void setOnBattleEnd(Consumer<BattleResult> onBattleEnd) {
        this.onBattleEnd = onBattleEnd;
    }
    private final BlockingQueue<BattleAction> playerActionQueue = new ArrayBlockingQueue<>(1);

    public TurnManager(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    public void startBattle() {
        try {
            runLoop();
        } catch (Throwable t) {
            DeveloperLogger.log(
                    "[TurnManager] UNCAUGHT EXCEPTION – battle thread died:\n" +
                            t.getClass().getSimpleName() + ": " + t.getMessage());
            t.printStackTrace();
        }
    }

    public void queuePlayerAction(BattleAction action) {
        boolean ok = playerActionQueue.offer(action);
        DeveloperLogger.log(
                ok ? "[TurnManager] Queued " + action.name()
                        : "[TurnManager] Queue FULL – click ignored");
    }

    public void onPlayerFlee() {
        playerFled = true;
        battleOver = true;
    }

    private void tickCooldowns() {
        for (Spell s : player.getSpellsEquipped()) {
            if (s != null) s.tickCooldown();
        }
        for (Spell s : enemy.getSpellsEquipped()) {
            if (s != null) s.tickCooldown();
        }
    }

    private void runLoop() {
        if (promptCallback != null) {
            DeveloperLogger.log("[TurnManager] Showing INITIAL prompt");
            promptCallback.run();
        }

        PlayerLogger.log("\n         ⚔️ The battle begins!");
        DeveloperLogger.log("[TurnManager] battleOver=" + battleOver);
        DeveloperLogger.log("loop entered");

        while (player.isAlive() && enemy.isAlive() && !battleOver) {
            playerActionQueue.clear();

            if (promptCallback != null) {
                DeveloperLogger.log("[TurnManager] Running promptCallback");
                promptCallback.run();
            }

            tickCooldowns();

            BattleAction playerAction;
            try {
                DeveloperLogger.log("[TurnManager] Waiting… queue size=" + playerActionQueue.size());
                playerAction = playerActionQueue.take();
                DeveloperLogger.log("[TurnManager] …got " + playerAction.getClass().getSimpleName());
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

            if (!battleOver && player.isAlive() && enemy.isAlive()) {
                if (promptCallback != null) {
                    DeveloperLogger.log("[TurnManager] Re-displaying prompt after actions");
                    promptCallback.run();
                }
            }

            DevLogOverlay.clearLog();
        }

        if (!player.isAlive()) {
            result = BattleResult.DEFEAT;
        } else if (!enemy.isAlive()) {
            result = BattleResult.VICTORY;
        } else if (battleOver) {
            result = BattleResult.FLED;
        }

        if (onBattleEnd != null) onBattleEnd.accept(result);
    }

    private void execute(Entity actor, BattleAction action) {
        PlayerLogger.logBlocking("\n         " + actor.getName() + " uses " + action.name());
        action.execute();

        DeveloperLogger.log("[TurnManager] After action: "
                + player.getName() + " HP=" + player.getStat(StatsType.HP)
                + ", " + enemy.getName() + " HP=" + enemy.getStat(StatsType.HP));

        if (!player.isAlive() || !enemy.isAlive()) {
            battleOver = true;
        }
    }
}
