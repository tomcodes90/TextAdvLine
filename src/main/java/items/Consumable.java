package items;

import characters.Entity;

public abstract class Consumable extends Item {
    int pointsToApply;

    Consumable(String id, String name, String description, int pointsToApply, int price) {
        super(id, name, description, price);
        this.pointsToApply = pointsToApply;
    }

    public abstract void use(Entity target);
}
