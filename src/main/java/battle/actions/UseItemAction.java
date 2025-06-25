package battle.actions;

import characters.Entity;
import items.consumables.Consumable;

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

        for (int i = 0; i < user.getConsumablesEquipped().length; i++) {
            if (user.getConsumablesEquipped()[i] == item) {
                user.getConsumablesEquipped()[i] = null;
                break;
            }
        }
    }

}
