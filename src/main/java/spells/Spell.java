package spells;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class Spell {

    private final SpellType name;
    private final ElementalType element;
    private final int damage;
    private final int cooldown;

    @Setter
    private int cooldownCounter = 0;

    public boolean isReady() {
        return cooldownCounter == 0;
    }

    public void tickCooldown() {
        if (cooldownCounter > 0) cooldownCounter--;
    }

    public void setOnCooldown() {
        cooldownCounter = cooldown;
    }

    /**
     * Returns a *fresh* copy with cooldownCounter reset to 0
     */
    public static Spell copyOf(Spell src) {
        return new Spell(src.name, src.element, src.damage, src.cooldown);
    }
}
