package spells;

public enum SpellType {
    // Non-elemental
    ENERGYBLAST("Energy Blast"),
    FLARE("Flare"),

    // Fire
    FIREBALL("Fireball"),
    INFERNO("Inferno"),

    // Ice
    FROSTBITE("Frostbite"),
    GLACIALSPIKE("Glacial Spike"),

    // Nature
    VINEWHIP("Vine Whip"),
    THORNSURGE("Thorn Surge"),

    // Healing / Utility
    HEAL("Heal"),
    CURESTATUS("Cure Status");

    private final String displayName;

    SpellType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

