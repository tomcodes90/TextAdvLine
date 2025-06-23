package items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class KeyItem extends Item {
    private final MissionType keyItemMission;

    @JsonCreator
    public KeyItem(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("keyItemMission") MissionType keyItemMission
    ) {
        super(id, name, description, 0);
        this.keyItemMission = keyItemMission;
    }

    public MissionType getKeyItemType() {
        return keyItemMission;
    }
}
