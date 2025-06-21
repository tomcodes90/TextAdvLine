package battle.actions;

import characters.Entity;
import items.Consumable;

public class UseItemAction implements BattleAction {
    private final Entity user;
    private final Consumable item;

    public UseItemAction(Entity user, Consumable item) {
        this.user = user;
        this.item = item;
    }

    @Override
    public String name() {
        return "Use " + item.getName();
    }

    @Override
    public void execute() {
        item.use(user);
    }
}
