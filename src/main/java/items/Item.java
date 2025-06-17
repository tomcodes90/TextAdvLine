package items;

public abstract class Item {
    private final String id;
    private final String name;
    private final String description;

    Item(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
