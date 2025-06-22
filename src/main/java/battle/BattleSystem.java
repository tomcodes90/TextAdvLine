package battle;

import characters.Entity;
import spells.Spell;
import util.DeveloperLogger;

import static characters.StatsType.*;

public class BattleSystem {
    public static void attack(Entity attacker, Entity target) {
        int damage = attacker.getWeapon().getEffectiveDamage(attacker.getStats()) - target.getEffectiveDefense();
        applyDamage(target, damage);
    }

    public static void castSpell(Entity caster, Spell spell, Entity target) {
        if (!spell.isReady()) {
            DeveloperLogger.log("❌ Spell " + spell.getName() + " is on cooldown.");
            return;
        }

        int spellPower = spell.getDamage() + caster.getStat(INTELLIGENCE);
        if (spell.getElement() == target.getElementalWeakness()) {
            spellPower *= 1.25;
        }

        applyDamage(target, spellPower);
        spell.setOnCooldown(); // ⏳ Start cooldown after casting

        DeveloperLogger.log("🔥 " + caster.getName() + " casts " + spell.getName() +
                " on " + target.getName() + " for " + spellPower + " damage.");
    }

    private static void applyDamage(Entity target, int amount) {
        target.modifyStat(HP, -amount);
        if (target.getStat(HP) <= 0) {
            target.setAlive(false);
        }
    }
}

