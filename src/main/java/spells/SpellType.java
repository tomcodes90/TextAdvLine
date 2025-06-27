package spells;

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

    // Replacing utility with new damage spells
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
