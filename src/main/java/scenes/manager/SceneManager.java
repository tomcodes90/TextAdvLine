// File: scenes/SceneManager.java
package scenes.manager;

/**
 * SceneManager is a singleton responsible for controlling which "scene" (UI view)
 * is currently active. Scenes represent different parts of the game, such as the main menu,
 * character info, battle screen, etc.
 * <p>
 * Why we use it:
 * - Centralized control over scene transitions (e.g., from menu to battle).
 * - Prevents multiple scenes from conflicting by ensuring only one is active at a time.
 * - Keeps the UI lifecycle clear and controlled.
 * <p>
 * Why it's helpful in Lanterna:
 * - Lanterna doesn't manage "screens" or "states" like a game engine would.
 * You must explicitly close and open windows to change views.
 * - This class abstracts that logic, reducing the complexity of handling UI flow manually.
 * <p>
 * Risks:
 * - If `exit()` or `enter()` are not implemented correctly in scenes, it may cause UI glitches or
 * memory/resource leaks (especially if windows are left open).
 * - Must be careful with threading: `enter()` typically adds components to Lanterna's GUI thread,
 * so avoid doing heavy work inside it or make sure UI updates run on the proper thread.
 */
public class SceneManager {
    private static SceneManager instance;  // Singleton instance
    private Scene currentScene;            // Currently active scene

    private SceneManager() {
        // Private constructor prevents external instantiation
    }

    /**
     * Returns the singleton instance of the SceneManager.
     */
    public static SceneManager get() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    /**
     * Switch to a new scene.
     * Calls exit() on the old scene, enter() on the new one.
     */
    public void switchTo(Scene newScene) {
        if (currentScene != null) {
            currentScene.exit();  // Clean up old scene
        }
        currentScene = newScene;
        currentScene.enter();     // Initialize new scene
    }
}
