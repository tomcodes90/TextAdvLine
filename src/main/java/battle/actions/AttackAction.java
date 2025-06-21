package battle.actions;

import battle.BattleSystem;
import characters.Entity;

public class AttackAction implements BattleAction {
    private final Entity attacker;
    private final Entity target;

    public AttackAction(Entity attacker, Entity target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public String name() {
        return "Attack";
    }

    @Override
    public void execute() {
        BattleSystem.attack(attacker, target);
    }
}
