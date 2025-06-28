package battle.actions;

import battle.BattleSystem;
import characters.Entity;
import spells.Spell;

/**
 * Class: CastSpellAction
 * <p>
 * Purpose:
 * Represents a spell-casting action during combat.
 * Executes the spell logic via BattleSystem and applies cooldown.
 */
public class CastSpellAction implements BattleAction {

    /**
     * The entity casting the spell
     */
    private final Entity caster;

    /**
     * The spell being cast
     */
    private final Spell spell;

    /**
     * The target of the spell
     */
    private final Entity target;

    /**
     * Constructor
     *
     * @param caster The entity using the spell
     * @param spell  The spell to be cast
     * @param target The spell's target
     */
    public CastSpellAction(Entity caster, Spell spell, Entity target) {
        this.caster = caster;
        this.spell = spell;
        this.target = target;
    }

    /**
     * Returns the spell name (used in combat logs or UI).
     * A space is prepended for display alignment.
     */
    @Override
    public String name() {
        return " " + spell.getName().toString();
    }

    /**
     * Executes the spell: calls BattleSystem logic and applies cooldown.
     */
    @Override
    public void execute() {
        BattleSystem.castSpell(caster, spell, target);
        spell.setOnCooldown(); // Prevent immediate reuse
    }
}
