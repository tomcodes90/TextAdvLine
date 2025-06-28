package spells;

/**
 * Enum: SpellType
 * <p>
 * Purpose:
 * Defines all the types of spells available in the game.
 * Used for spell selection, book creation, and factory instantiation.
 * Each spell has a fun, thematic display name.
 */
public enum SpellType {
    // Non-elemental
    ENERGYBLAST("BoomaZap"),
    FLARE("SugoFlare"),

    // Fire
    FIREBALL("FireMeatball"),
    INFERNO("InfernoGrana"),

    // Ice
    FROSTBITE("BrrrGelato"),
    GLACIALSPIKE("FrozenPeas"),

    // Nature
    VINEWHIP("GreenSlap"),
    THORNSURGE("SicilianRoses"),

    // Special
    MEATBALLMETEOR("PizzaRevenge"),
    GARLICNOVA("GarlicNova O_O");

    private final String displayName;

    SpellType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
