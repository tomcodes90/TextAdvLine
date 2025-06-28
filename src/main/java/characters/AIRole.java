package characters;

import battle.actions.*;
import items.consumables.Consumable;
import items.consumables.Potion;
import items.consumables.StatEnhancer;
import spells.ElementalType;
import spells.Spell;

import java.util.Arrays;

import static characters.StatsType.HP;

/**
 * Enum: AIRole
 * <p>
 * Purpose:
 * Defines various enemy AI roles in battle.
 * Each enum constant implements its own `play()` strategy, deciding
 * what action an enemy should take based on current context like HP,
 * equipped items, or target weakness.
 */
public enum AIRole {

    /**
     * BERSERKER: Always performs a basic attack regardless of context.
     * Pure aggression with no decision-making.
     */
    BERSERKER {
        @Override
        public BattleAction play(Entity self, Entity target) {
            return new AttackAction(self, target);
        }
    },

    /**
     * MAGE: Attempts to cast a spell, prioritizing target's weakness.
     * Falls back to any ready spell, or basic attack if none available.
     */
    MAGE {
        @Override
        public BattleAction play(Entity self, Entity target) {
            return resolveSpellAction(self, target);
        }
    },

    /**
     * MAGE_BOOSTER: Uses a StatEnhancer first if available,
     * then behaves like a MAGE (casts spells).
     */
    MAGE_BOOSTER {
        @Override
        public BattleAction play(Entity self, Entity target) {
            // Try using a stat enhancer first
            Consumable enhancer = Arrays.stream(self.getConsumablesEquipped())
                    .filter(c -> c instanceof StatEnhancer)
                    .findFirst()
                    .orElse(null);
            if (enhancer != null) {
                return new UseItemAction(self, enhancer);
            }

            // Fall back to spell casting
            return resolveSpellAction(self, target);
        }
    },

    /**
     * MAGE_HEALER: Prioritizes healing with a potion if HP < 50.
     * Otherwise, casts a spell like a normal MAGE.
     */
    MAGE_HEALER {
        @Override
        public BattleAction play(Entity self, Entity target) {
            // Look for a potion
            Consumable potion = Arrays.stream(self.getConsumablesEquipped())
                    .filter(c -> c instanceof Potion)
                    .findFirst()
                    .orElse(null);

            // Heal if low on HP
            if (self.getStat(HP) < 50 && potion != null) {
                return new UseItemAction(self, potion);
            }

            // Otherwise cast spells
            return resolveSpellAction(self, target);
        }
    },

    /**
     * FIGHTER_HEALER: Attacks if target is injured.
     * Otherwise, heals if own HP is low and potion is available.
     */
    FIGHTER_HEALER {
        @Override
        public BattleAction play(Entity self, Entity target) {
            // Attack if target HP < 50%
            if (target.getStat(HP) < target.getStat(StatsType.MAX_HP) / 2) {
                return new AttackAction(self, target);
            }

            // Heal if own HP is low
            Consumable potion = Arrays.stream(self.getConsumablesEquipped())
                    .filter(c -> c instanceof Potion)
                    .findFirst()
                    .orElse(null);

            if (self.getStat(HP) < 50 && potion != null) {
                return new UseItemAction(self, potion);
            } else {
                return new AttackAction(self, target);
            }
        }
    },

    /**
     * FIGHTER_BOOSTER: Attacks if target is weak.
     * Otherwise, uses a StatEnhancer if available.
     */
    FIGHTER_BOOSTER {
        @Override
        public BattleAction play(Entity self, Entity target) {
            // Attack if target HP < 50%
            if (target.getStat(HP) < target.getStat(StatsType.MAX_HP) / 2) {
                return new AttackAction(self, target);
            }

            // Use enhancer if available
            Consumable enhancer = Arrays.stream(self.getConsumablesEquipped())
                    .filter(c -> c instanceof StatEnhancer)
                    .findFirst()
                    .orElse(null);

            if (enhancer != null) {
                return new UseItemAction(self, enhancer);
            } else {
                return new AttackAction(self, target);
            }
        }
    };

    /**
     * Helper method for spell-based roles (Mage, Mage Booster, Mage Healer).
     * Prioritizes casting a spell matching the target's elemental weakness.
     * Falls back to any ready spell, or a basic attack if none are ready.
     */
    private static BattleAction resolveSpellAction(Entity self, Entity target) {
        ElementalType weakness = target.getElementalWeakness();

        // Try to find a spell matching the target's weakness
        Spell best = Arrays.stream(self.getSpellsEquipped())
                .filter(s -> s != null && s.isReady() && s.getElement() == weakness)
                .findFirst()
                .orElse(null);

        // Fallback: any ready spell
        if (best == null) {
            best = Arrays.stream(self.getSpellsEquipped())
                    .filter(s -> s != null && s.isReady())
                    .findFirst()
                    .orElse(null);
        }

        // Cast if available, otherwise fallback to basic attack
        if (best != null) {
            return new CastSpellAction(self, best, target);
        } else {
            return new AttackAction(self, target);
        }
    }

    /**
     * Abstract method implemented by each enum constant.
     * Determines the battle action taken by the AI role.
     */
    public abstract BattleAction play(Entity self, Entity target);
}
