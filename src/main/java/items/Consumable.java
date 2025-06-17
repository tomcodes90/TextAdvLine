package items;

import characters.Entity;

public abstract class Consumable extends Item {
    int valueToApply;

    Consumable(String id, String name, String description, int valueToApply) {
        super(id, name, description);
       this.valueToApply = valueToApply;
    }

    public abstract void use(Entity target);
}
