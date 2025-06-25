package spells;

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
