package items;

public class KeyItem extends Item {
    private final KeyItemsMissions keyItemMission;

    public KeyItem(String id, String name, String description, KeyItemsMissions keyItemMission) {
        super(id, name, description);
        this.keyItemMission = keyItemMission;
    }

    public KeyItemsMissions getKeyItemType() {
        return keyItemMission;
    }
}
