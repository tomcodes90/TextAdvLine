// File: scenes/missions/MissionType.java
package scenes.missions;

/**
 * Enum representing the major missions of the game.
 * Used to track story progress and unlock mission-specific content.
 */
public enum MissionType {
    TUTORIAL,   // Special introductory mission
    MISSION_1,  // Mission identifiers follow a clear numeric sequence
    MISSION_2,
    MISSION_3,
    MISSION_4,
    MISSION_5,
    MISSION_6,
    MISSION_7,
    MISSION_8;

    /**
     * Overrides the default enum name to provide a more human-readable string.
     * Example:
     * - TUTORIAL → "Tutorial"
     * - MISSION_3 → "Mission 3"
     */
    @Override
    public String toString() {
        if (this == TUTORIAL) {
            return "Tutorial";
        }
        // Convert enum name (e.g. MISSION_3) into "Mission 3"
        return name().replace("MISSION_", "Mission ");
    }
}
