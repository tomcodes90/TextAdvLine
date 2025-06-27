package characters;

import battle.actions.*;
import items.consumables.Consumable;
import items.consumables.Potion;
import items.consumables.StatEnhancer;
import spells.ElementalType;
import spells.Spell;

import java.util.Arrays;

import static characters.StatsType.HP;

public enum AIRole {

    BERSERKER {
        @Override
        public BattleAction play(Entity self, Entity target) {
            return new AttackAction(self, target);
        }
    },

    MAGE {
        @Override
        public BattleAction play(Entity self, Entity target) {
            return resolveSpellAction(self, target);
        }
    },
    MAGE_BOOSTER {
        @Override
        public BattleAction play(Entity self, Entity target) {
            Consumable enhancer = Arrays.stream(self.getConsumablesEquipped())
                    .filter(c -> c instanceof StatEnhancer)
                    .findFirst()
                    .orElse(null);
            if (enhancer != null) {
                return new UseItemAction(self, enhancer);
            }

            return resolveSpellAction(self, target);
        }
    },
    MAGE_HEALER {
        @Override
        public BattleAction play(Entity self, Entity target) {
            Consumable potion = Arrays.stream(self.getConsumablesEquipped())
                    .filter(c -> c instanceof Potion)
                    .findFirst()
                    .orElse(null);

            if (self.getStat(HP) < 50 && potion != null) {
                return new UseItemAction(self, potion);
            }

            return resolveSpellAction(self, target);
        }
    },

    FIGHTER_HEALER {
        @Override
        public BattleAction play(Entity self, Entity target) {
            if (target.getStat(HP) < target.getStat(StatsType.MAX_HP) / 2) {
                return new AttackAction(self, target);
            }

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
    FIGHTER_BOOSTER {
        @Override
        public BattleAction play(Entity self, Entity target) {
            if (target.getStat(HP) < target.getStat(StatsType.MAX_HP) / 2) {
                return new AttackAction(self, target);
            }

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

    private static BattleAction resolveSpellAction(Entity self, Entity target) {
        ElementalType weakness = target.getElementalWeakness();

        Spell best = Arrays.stream(self.getSpellsEquipped())
                .filter(s -> s != null && s.isReady() && s.getElement() == weakness)
                .findFirst()
                .orElse(null);

        if (best == null) {
            best = Arrays.stream(self.getSpellsEquipped())
                    .filter(s -> s != null && s.isReady())
                    .findFirst()
                    .orElse(null);
        }

        if (best != null) {
            return new CastSpellAction(self, best, target);
        } else {
            return new AttackAction(self, target);
        }
    }

    public abstract BattleAction play(Entity self, Entity target);
}
