package items.consumables;

import characters.Entity;
import items.Item;

public abstract class Consumable extends Item {
    protected int pointsToApply;

    protected Consumable(String id, String name, String description, int pointsToApply, int price) {
        super(id, name, description, price);
        this.pointsToApply = pointsToApply;
    }

    public abstract void use(Entity target);
}
