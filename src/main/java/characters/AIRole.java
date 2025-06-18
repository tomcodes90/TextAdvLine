package characters;

import spells.SpellType;

import java.util.Random;

public enum AIRole {
    BERSERKER {
        @Override
        public void decideAction(Entity self, Entity target) {
            if (self.getStat(StatsType.HP) < 20 && self.getSpell(SpellType.BUFF) != null) {
                self.castSpell(self.getSpell(SpellType.BUFF), self); // buff self when low HP
            } else {
                self.attack(target); // aggressive attack
            }
        }
    },
    MAGE {
        @Override
        public void decideAction(Entity self, Entity target) {
                self.castSpell(self.getSpells().get(SpellType.ENERGYBLAST), target);
        }
    },
    PRIEST {
        @Override
        public void decideAction(Entity self, Entity target) {
            if (self.getStat(StatsType.HP) < 40 && self.getSpell(SpellType.HEAL) != null) {
                self.castSpell(self.getSpell(SpellType.HEAL), self);
            } else {
                self.attack(target); // fallback poke
            }
        }
    },
    KNIGHT {
        @Override
        public void decideAction(Entity self, Entity target) {
            if (target.getStat(StatsType.STRENGTH) > self.getStat(StatsType.STRENGTH)) {
                if (self.getSpell(SpellType.DEBUFF) != null)
                    self.castSpell(self.getSpell(SpellType.DEBUFF), target);
                else
                    self.attack(target);
            } else {
                self.attack(target);
            }
        }
    };

    public abstract void decideAction(Entity self, Entity target);
}
