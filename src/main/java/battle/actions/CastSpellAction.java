package battle.actions;

import battle.BattleSystem;
import characters.Entity;
import spells.Spell;

public class CastSpellAction implements BattleAction {
    private final Entity caster;
    private final Spell spell;
    private final Entity target;

    public CastSpellAction(Entity caster, Spell spell, Entity target) {
        this.caster = caster;
        this.spell = spell;
        this.target = target;
    }

    @Override
    public String name() {
        return "Cast " + spell.getName();
    }

    @Override
    public void execute() {
        BattleSystem.castSpell(caster, spell, target);
    }
}
