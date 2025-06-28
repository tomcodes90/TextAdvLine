package items.consumables;

import characters.Entity;
import items.Item;
import lombok.Getter;

/**
 * Abstract Class: Consumable
 * <p>
 * Purpose:
 * Serves as the base class for all consumable items (e.g., potions, stat boosters).
 * Consumables can be used during or outside of battle and apply immediate effects to an entity.
 * <p>
 * Fields:
 * - pointsToApply: Represents how much effect this item has (e.g., how many HP it restores).
 * <p>
 * Usage:
 * - Subclasses must implement the {@code use()} method to define what the item does.
 */
@Getter
public abstract class Consumable extends Item {

    /**
     * Amount of effect this item applies (e.g., healing points, stat increase).
     */
    protected int pointsToApply;

    /**
     * Constructor used by all subclasses.
     *
     * @param id            Unique identifier
     * @param name          Display name
     * @param description   Description used in UI
     * @param pointsToApply Effect value of the item
     * @param price         Shop value
     */
    protected Consumable(String id, String name, String description, int pointsToApply, int price) {
        super(id, name, description, price);
        this.pointsToApply = pointsToApply;
    }

    /**
     * Executes the effect of the consumable on a target entity.
     *
     * @param target The entity receiving the effect
     */
    public abstract void use(Entity target);
}
