package battle;

import characters.Entity;
import spells.Spell;
import util.DeveloperLogger;

import static characters.StatsType.*;

/**
 * Class: BattleSystem
 * <p>
 * Purpose:
 * Core utility class for executing combat logic.
 * Handles physical attacks, spell casting, damage calculation, and death checks.
 * <p>
 * All methods are static and are called by BattleActions during combat.
 */
public class BattleSystem {

    /**
     * Performs a basic physical attack from one entity to another.
     * <p>
     * Damage = weapon damage (scaled by attacker stats) - target's defense
     *
     * @param attacker The entity initiating the attack
     * @param target   The entity being attacked
     */
    public static void attack(Entity attacker, Entity target) {
        int damage = attacker.getWeapon().getEffectiveDamage(attacker.getStats())
                - target.getEffectiveDefense();
        applyDamage(target, damage);
    }

    /**
     * Casts a spell from caster to target, factoring in caster's intelligence
     * and elemental weakness.
     *
     * @param caster The entity casting the spell
     * @param spell  The spell to cast
     * @param target The target of the spell
     */
    public static void castSpell(Entity caster, Spell spell, Entity target) {
        if (!spell.isReady()) {
            DeveloperLogger.log("Spell " + spell.getName() + " is on cooldown.");
            return;
        }

        // Base damage + caster INT
        int spellPower = spell.getDamage() + caster.getStat(INTELLIGENCE);

        // Bonus if target is weak to element
        if (spell.getElement() == target.getElementalWeakness()) {
            spellPower *= 1.25;
        }

        applyDamage(target, spellPower);
        spell.setOnCooldown(); // ⏳ Start cooldown after casting

        // Log the result
        DeveloperLogger.log(caster.getName() + " casts " + spell.getName() +
                " on " + target.getName() + " for " + spellPower + " damage.");
    }

    /**
     * Applies raw damage to a target.
     * Reduces HP and sets isAlive = false if HP drops to 0 or below.
     *
     * @param target The entity receiving damage
     * @param amount The damage amount to apply
     */
    private static void applyDamage(Entity target, int amount) {
        target.modifyStat(HP, -amount);
    }
}
