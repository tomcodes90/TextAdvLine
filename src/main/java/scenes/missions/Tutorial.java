// File: scenes/missions/Tutorial.java

/*
 * This class implements the tutorial mission scene for the game.
 * It uses a simple step-based progression system to walk the player through:
 * 1. Introduction dialogue
 * 2. Character creation (name, stat boost, elemental weakness)
 * 3. A pre-battle explanation
 * 4. A turn-based battle
 * 5. Post-battle story continuation
 * 6. Transition to the World Hub
 *
 * Lanterna is used via MultiWindowTextGUI to display text windows and UI components.
 * DialogueService is used to drive the narrative and prompt player input.
 *
 * ⚠ Note on threading with Lanterna:
 *  - All GUI actions must happen in the main thread.
 *  - Blocking calls like `addWindowAndWait()` will freeze input unless carefully structured.
 *  - Always ensure callbacks don't conflict with the current GUI event loop.
 */

package scenes.missions;

import characters.EnemyFactory;
import characters.Player;
import characters.StatsType;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import dialogues.*;
import scenes.menu.MainMenu;
import spells.ElementalType;
import spells.SpellType;
import state.GameState;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.WorldHub;
import scenes.ui.Battle;
import scenes.ui.DialogueUI;
import util.DeveloperLogger;
import util.ItemRegistry;

import java.util.List;

public class Tutorial implements Scene {

    // === State ===
    private final MultiWindowTextGUI gui;
    private final DialogueService dialogueService;
    private int step = 0;
    private ElementalType weakness;
    private StatsType statsPreference;
    private String pendingName;

    public Tutorial(MultiWindowTextGUI gui) {
        this.gui = gui;
        this.dialogueService = DialogueService.getInstance();
    }

    @Override
    public void enter() {
        dialogueService.setUI(new DialogueUI(gui));
        nextStep();
    }

    @Override
    public void exit() {
    }

    // === Step Controller ===
    private void nextStep() {
        switch (step++) {
            case 0 -> showIntroDialogue();
            case 1 -> createPlayer();
            case 2 -> showPreBattleDialogue();
            case 3 -> startBattle();
            case 4 -> showPostBattleDialogues();
            case 5 -> goToWorldHub();
        }
    }

    // === Step 0: Opening Narration ===
    private void showIntroDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "Italy... a land divided. The great families — the " +
                        " — fight for culinary supremacy."),
                new Dialogue("Narrator", "From north to south, the scent of betrayal and tomato sauce fills the air."),
                new Dialogue("Narrator", "But you... you only care about one thing: making it home in time for Nonna's dinner.")
        ), this::nextStep);
    }

    // === Step 1: Character Creation ===
    private void createPlayer() {
        DialogueWithInput askName = new DialogueWithInput(
                "Narrator",
                "What is your name?",
                List.of(new ChoiceOption("Continue", () -> {
                    pendingName = DialogueUI.lastInput.trim();
                    if (pendingName.isEmpty()) pendingName = "Francesco";
                    askForStatPreference();
                    askForWeakness();
                    GameState.get().setPlayer(new Player(pendingName, statsPreference, weakness));
                    GameState.get().getPlayer().equipSpell(0, SpellType.FIREBALL);
                    DeveloperLogger.log("Creating player " + pendingName + " with boost " + statsPreference + ", Weak to " + weakness);
                    nextStep();
                })),
                DialogueInputType.TEXT_INPUT
        );
        dialogueService.runDialogueWithInput(askName);
    }

    private void askForStatPreference() {
        DialogueWithInput statChoice = new DialogueWithInput(
                "Narrator",
                "And what kind of fighter are you?",
                List.of(
                        new ChoiceOption("I hit enemies like a bull!", () -> statsPreference = StatsType.STRENGTH),
                        new ChoiceOption("I prefer to set them on fire.", () -> statsPreference = StatsType.INTELLIGENCE)
                ),
                DialogueInputType.CHOICE_ONLY
        );
        dialogueService.runDialogueWithInput(statChoice);
    }

    private void askForWeakness() {
        DialogueWithInput weaknessChoice = new DialogueWithInput(
                "Narrator",
                "Now tell me, bambino... what spice gives you the shivers?",
                List.of(
                        new ChoiceOption("It is the fiery bite of Peperoncino.", () -> weakness = ElementalType.FIRE),
                        new ChoiceOption("Or maybe a pinch of Sea Salt.", () -> weakness = ElementalType.ICE),
                        new ChoiceOption("Perhaps the earthy scent of Basil.", () -> weakness = ElementalType.NATURE)
                ),
                DialogueInputType.CHOICE_ONLY
        );
        dialogueService.runDialogueWithInput(weaknessChoice);
    }

    // === Step 2: Pre-Battle Explanation ===
    private void showPreBattleDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "You walk the cobbled road back to your village, stomach rumbling and nose dreaming of fresh basil..."),
                new Dialogue("Narrator", "But suddenly, a shady figure blocks your path."),
                new Dialogue("Narrator", "A bandit! The kind who puts pineapple on pizza and calls it innovation."),
                new Dialogue("Bandit", "Well, well... what do we have here? A little chefling lost on the road?"),
                new Dialogue("Hero", "Step aside, I’m going home for dinner. Nonna waits for no one."),
                new Dialogue("Bandit", "Nonna? Bah! I've never met my Nonna. Prepare to get sautéed!"),
                new Dialogue("Narrator", "This is a turn-based battle."),
                new Dialogue("Narrator", "The one with the highest speed acts first. Every decision matters, capisce?"),
                new Dialogue("Narrator", """
                        - Attack: A straightforward physical strike.
                        - Spells: Up to 3 -> they have Cooldowns.
                        * Books: Assigns Spells -> No duplicates.
                        - Items: Up to 3 Potion/Elixirs -> No duplicates
                        - Flee: Escape -> 50% chance, ends the Battle""")
        ), this::nextStep);
    }

    // === Step 3: Battle Execution ===
    private void startBattle() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createBandit(GameState.get().getPlayer().getLevel()));
        battle.setOnBattleEnd(result -> {
            switch (result) {
                case VICTORY -> {
                    DeveloperLogger.log("Player won");
                    GameState.get().setMissionFlag(MissionType.TUTORIAL);
                    GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_energyblast"));
                    GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_fireball"));
                    GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_frostbite"));
                    GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_vinewhip"));
                    SceneManager.get().switchTo(this);
                }
                case DEFEAT -> dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "You... lost.")
                ), () -> SceneManager.get().switchTo(new MainMenu(gui)));

                case FLED -> dialogueService.runDialogues(List.of(
                        new Dialogue("Narrator", "You... escaped from the Tutorial?! Get out of Here!!!")
                ), () -> SceneManager.get().switchTo(new MainMenu(gui)));

                default -> throw new IllegalStateException("Unexpected value: " + result);
            }
        });
        SceneManager.get().switchTo(battle);
    }

    // === Step 4: Aftermath Dialogue ===
    private void showPostBattleDialogues() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "Against all odds — and after surviving that disgraceful bandit — you make it home."),
                new Dialogue("Narrator", "The door creaks open. The aroma of garlic, simmering tomatoes, and destiny fills the air..."),
                new Dialogue("Nonna", "Ah, finally! You show up just in time — the pasta's al dente and so is your fate."),
                new Dialogue("Nonna", "Sit, mangia. But listen closely... there's something you must know."),
                new Dialogue("Nonna", "Last night, while I was kneading dough, the flour... it whispered to me."),
                new Dialogue("Nonna", "A vision — of a time long past. Of an ancient recipe lost to war and greed."),
                new Dialogue("Nonna", "You — you will gather the sacred ingredients. The **true** recipe lies scattered, torn, and hidden."),
                new Dialogue("Hero", "But where do I even begin?"),
                new Dialogue("Nonna", "Start with what we lack: the Golden Garlic of Belmonte. Last I heard, some goons from the Parmesani family were sniffing around there."),
                new Dialogue("Narrator", "Thus, your journey begins — with nothing but your courage, Nonna’s words, and a faint smell of roasted garlic in your clothes.")
        ), this::nextStep);
    }

    // === Step 5: Transition ===
    private void goToWorldHub() {
        SceneManager.get().switchTo(new WorldHub(gui, GameState.get().getPlayer()));
    }
}
