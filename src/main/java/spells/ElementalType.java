package spells;

/**
 * Enum: ElementalType
 * <p>
 * Purpose:
 * Defines elemental affinities for spells and characters.
 * <p>
 * Values:
 * - FIRE: Fire-based spells
 * - ICE: Ice-based spells
 * - NATURE: Nature-based spells
 * - NONE: Neutral or non-elemental magic
 */
public enum ElementalType {
    FIRE("Fire"),
    ICE("Ice"),
    NATURE("Nature"),
    NONE("None");

    private final String displayName;

    ElementalType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
