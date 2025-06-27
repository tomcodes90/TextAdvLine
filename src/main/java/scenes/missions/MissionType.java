package scenes.missions;

public enum MissionType {
    TUTORIAL,
    MISSION_1,
    MISSION_2,
    MISSION_3,
    MISSION_4,
    MISSION_5,
    MISSION_6,
    MISSION_7,
    MISSION_8;

    @Override
    public String toString() {
        if (this == TUTORIAL) {
            return "Tutorial";
        }
        return name().replace("MISSION_", "Mission ");
    }
}
