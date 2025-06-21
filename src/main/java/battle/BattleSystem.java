package battle;

import characters.Entity;
import spells.Spell;

import static characters.StatsType.*;

public class BattleSystem {
    public static void attack(Entity attacker, Entity target) {
        int damage = attacker.getWeapon().getEffectiveDamage(attacker.getStats()) - target.getEffectiveDefense();
        applyDamage(target, damage);
    }

    public static void castSpell(Entity caster, Spell spell, Entity target) {
        int spellPower = spell.getDamage() + caster.getStat(INTELLIGENCE);
        if (spell.getElement() == target.getElementalWeakness()) {
            spellPower *= 1.25;
        }
        applyDamage(target, (int) spellPower);
    }

    private static void applyDamage(Entity target, int amount) {
        target.modifyStat(HP, -amount);
        if (target.getStat(HP) <= 0) {
            target.setAlive(false);
        }
    }
}
