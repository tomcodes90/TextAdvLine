package scenes.manager;

public interface Scene {
    void enter();          // Called when the scene starts

    void handleInput();    // (Optional) Called regularly or for manual input handling

    void exit();           // Called before switching to another scene
}