package battle.actions;

import characters.Entity;
import items.consumables.Consumable;

/**
 * Class: UseItemAction
 * <p>
 * Purpose:
 * Represents the action of using a consumable item in battle (e.g., potion, stat boost).
 * Applies the item's effect and then removes it from the user's equipped slot.
 */
public class UseItemAction implements BattleAction {

    /**
     * The entity using the item
     */
    private final Entity user;

    /**
     * The consumable item to be used
     */
    private final Consumable item;

    /**
     * Constructor
     *
     * @param user The entity using the item
     * @param item The consumable item to apply
     */
    public UseItemAction(Entity user, Consumable item) {
        this.user = user;
        this.item = item;
    }

    /**
     * Returns the name of the action (for UI or combat log).
     */
    @Override
    public String name() {
        return "use " + item.getName();
    }

    /**
     * Executes the item's effect, then removes it from the user's equipped consumables.
     */
    @Override
    public void execute() {
        item.use(user); // Apply the item's effect

        // Remove item from equipped slot after use
        for (int i = 0; i < user.getConsumablesEquipped().length; i++) {
            if (user.getConsumablesEquipped()[i] == item) {
                user.getConsumablesEquipped()[i] = null;
                break;
            }
        }
    }
}
