package items.consumables;

import characters.Entity;
import items.Item;
import lombok.Getter;

public abstract class Consumable extends Item {
    @Getter
    protected int pointsToApply;

    protected Consumable(String id, String name, String description, int pointsToApply, int price) {
        super(id, name, description, price);
        this.pointsToApply = pointsToApply;
    }

    public abstract void use(Entity target);
}
