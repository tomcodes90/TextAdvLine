package spells;

import lombok.*;

/**
 * Class: Spell
 * <p>
 * Purpose:
 * Represents a single spell with its type, element, damage, and cooldown logic.
 */
@Getter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class Spell {

    /**
     * Type of the spell (used for display and identification)
     */
    private final SpellType name;

    /**
     * Elemental type of the spell (used for weakness/resistance checks)
     */
    private final ElementalType element;

    /**
     * Base damage of the spell
     */
    private final int damage;

    /**
     * Number of turns before the spell can be reused
     */
    private final int cooldown;

    /**
     * Remaining turns before the spell becomes usable again
     */
    @Setter
    private int cooldownCounter = 0;

    /**
     * @return true if the spell can currently be cast
     */
    public boolean isReady() {
        return cooldownCounter == 0;
    }

    /**
     * Reduces the cooldown counter at the end of a turn
     */
    public void tickCooldown() {
        if (cooldownCounter > 0) cooldownCounter--;
    }

    /**
     * Resets the cooldown counter to the full cooldown
     */
    public void setOnCooldown() {
        cooldownCounter = cooldown;
    }

    /**
     * Creates a fresh copy of the given spell with cooldownCounter = 0
     *
     * @param src The source spell to clone
     * @return A new Spell instance
     */
    public static Spell copyOf(Spell src) {
        return new Spell(src.name, src.element, src.damage, src.cooldown);
    }
}
