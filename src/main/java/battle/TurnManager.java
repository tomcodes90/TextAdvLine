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

/**
 * Class: TurnManager
 * <p>
 * Purpose:
 * Controls the entire battle loop between the player and a single enemy.
 * Handles action queuing, turn order, spell cooldowns, status effects, and battle result dispatch.
 */
@Getter
public class TurnManager {

    private final Player player;
    private final Enemy enemy;

    /**
     * Callback triggered at the end of battle with the result (Victory, Defeat, Fled)
     */
    @Setter
    private Consumer<BattleResult> onBattleEnd;

    /**
     * Callback to show prompt (e.g. menu or input screen) each turn
     */
    @Setter
    private Runnable promptCallback;

    /**
     * Set to true when battle ends (victory, death, or fleeing)
     */
    @Setter
    private boolean battleOver = false;

    /**
     * Set by enemy or player action to indicate battle is finished
     */
    private final AtomicBoolean finished = new AtomicBoolean(false);

    /**
     * True if the player successfully fled the battle
     */
    private volatile boolean playerFled = false;

    /**
     * Final result of the battle
     */
    @Getter
    private BattleResult result;

    /**
     * Queue to wait for player input before proceeding with the turn
     */
    private final BlockingQueue<BattleAction> playerActionQueue = new ArrayBlockingQueue<>(1);

    /**
     * Constructor
     *
     * @param player The player character
     * @param enemy  The enemy entity
     */
    public TurnManager(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    /**
     * Starts the battle loop. This method blocks and controls the entire fight.
     */
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

    /**
     * Adds the player's selected action to the queue (non-blocking).
     */
    public void queuePlayerAction(BattleAction action) {
        boolean ok = playerActionQueue.offer(action);
        DeveloperLogger.log(
                ok ? "[TurnManager] Queued " + action.name()
                        : "[TurnManager] Queue FULL – click ignored");
    }

    /**
     * Called when the player successfully flees.
     * Immediately ends the battle and marks result as FLED.
     */
    public void onPlayerFlee() {
        playerFled = true;
        battleOver = true;
    }

    /**
     * Reduces spell cooldowns for both player and enemy at the start of each round.
     */
    private void tickCooldowns() {
        for (Spell s : player.getSpellsEquipped()) {
            if (s != null) s.tickCooldown();
        }
        for (Spell s : enemy.getSpellsEquipped()) {
            if (s != null) s.tickCooldown();
        }
    }

    /**
     * Main battle loop:
     * Waits for player input, generates enemy move, resolves turns based on speed,
     * and checks battle status after each round.
     */
    private void runLoop() {
        if (promptCallback != null) {
            DeveloperLogger.log("[TurnManager] Showing INITIAL prompt");
            promptCallback.run();
        }

        PlayerLogger.log("\n         The battle begins!");
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
                playerAction = playerActionQueue.take(); // waits for user input
                DeveloperLogger.log("[TurnManager] …got " + playerAction.getClass().getSimpleName());
            } catch (InterruptedException e) {
                DeveloperLogger.log("[TurnManager] Battle interrupted");
                return;
            }

            BattleAction enemyAction = enemy.getAiRole().play(enemy, player);

            // Determine turn order (speed-based, with tiebreaker)
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

            // Apply status effects (e.g. temporary buffs)
            player.tickStatusEffects();
            enemy.tickStatusEffects();

            // Re-prompt if the fight continues
            if (!battleOver && player.isAlive() && enemy.isAlive()) {
                if (promptCallback != null) {
                    DeveloperLogger.log("[TurnManager] Re-displaying prompt after actions");
                    promptCallback.run();
                }
            }

            DevLogOverlay.clearLog(); // Optional: clears dev log UI
        }

        // Cleanup temporary boosts after battle
        player.getTempBoosts().clear();

        // Determine final result
        if (!player.isAlive()) {
            result = BattleResult.DEFEAT;
        } else if (!enemy.isAlive()) {
            result = BattleResult.VICTORY;
        } else if (battleOver) {
            result = BattleResult.FLED;
        }

        // Notify whoever is listening
        if (onBattleEnd != null) onBattleEnd.accept(result);
    }

    /**
     * Executes a BattleAction for the given actor.
     * Handles action execution, HP log update, and death checks.
     *
     * @param actor  The entity performing the action
     * @param action The action to perform
     */
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

/*
    === Battle Flow Overview ===

          +------------------+
          |  Start Battle    |
          +------------------+
                    |
                    v
        +------------------------+
        | Display initial prompt |
        +------------------------+
                    |
                    v
        +------------------------+
        | Wait for player input  |
        +------------------------+
                    |
                    v
        +------------------------+
        | Determine turn order   |
        +------------------------+
                    |
                    v
        +------------------------+
        | Execute first action   |
        +------------------------+
                    |
                    v
        +-------------------------------+
        | If opponent is still alive →  |
        |   Execute second action       |
        +-------------------------------+
                    |
                    v
        +------------------------+
        | Tick status effects    |
        +------------------------+
                    |
                    v
        +-------------------------------------------+
        | If both player and enemy are alive and    |
        | battle is not over → Re-display prompt    |
        +-------------------------------------------+
                    |
                    v
        +------------------------+
        | Check end conditions   |
        +------------------------+
                    |
                    v
        +------------------------+
        |   Call onBattleEnd     |
        +------------------------+
*/
