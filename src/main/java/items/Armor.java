package items;

public class Armor extends Item {
    private int defensePoints;

    public Armor(String id, String name, String description, int defensePoints) {
        super(id, name, description);
        this.defensePoints = defensePoints;
    }

    public int getDefensePoints() {
        return defensePoints;
    }
}
