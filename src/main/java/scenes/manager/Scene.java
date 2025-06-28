package scenes.manager;

/**
 * Interface: Scene
 * <p>
 * Purpose:
 * Represents a "screen" or "view" in the game (e.g., main menu, battle, inventory, etc.).
 * All scenes should implement this interface to define how they are initialized and cleaned up.
 * <p>
 * Usage:
 * - Each scene is controlled by the SceneManager.
 * - When a new scene is switched to, its `enter()` method is called.
 * - When switching away, its `exit()` method is called to allow for cleanup.
 */
public interface Scene {

    /**
     * Called when the scene becomes active.
     * Used to build and display the Lanterna UI components (windows, panels, etc.).
     */
    void enter();

    /**
     * Called before switching to a different scene.
     * Used to perform cleanup like closing windows or releasing resources.
     */
    void exit();
}
