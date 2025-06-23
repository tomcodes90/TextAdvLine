// File: scenes/SceneManager.java
package scenes.manager;

public class SceneManager {
    private static SceneManager instance;
    private Scene currentScene;

    private SceneManager() {
    }

    public static SceneManager get() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    public void switchTo(Scene newScene) {
        if (currentScene != null) {
            currentScene.exit();
        }
        currentScene = newScene;
        currentScene.enter();
    }

    public void update() {
        if (currentScene != null) {
            currentScene.handleInput();
        }
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
