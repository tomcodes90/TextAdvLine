package scenes.missions;

import characters.EnemyFactory;
import characters.Player;
import characters.StatsType;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import dialogues.*;
import lombok.Lombok;
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
    private final MultiWindowTextGUI gui;
    private final DialogueService dialogueService;
    ;
    private int step = 0;
    private ElementalType weakness;
    private StatsType statsPreference;

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
    public void handleInput() {
    }

    @Override
    public void exit() {
    }

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


    private void showIntroDialogue() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "Italy... a land divided. The great families — the " +
                        " — fight for culinary supremacy."),
                new Dialogue("Narrator", "From north to south, the scent of betrayal and tomato sauce fills the air."),
                new Dialogue("Narrator", "But you... you only care about one thing: making it home in time for Nonna's dinner.")
        ), this::nextStep);
    }

    private String pendingName; // store name between steps

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
                    nextStep();// go to next step
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
                        new ChoiceOption("I hit enemies like a bull!", () -> {
                            statsPreference = StatsType.STRENGTH;
                        }),
                        new ChoiceOption("I prefer to set them on fire.", () -> {
                            statsPreference = StatsType.INTELLIGENCE;
                        })
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
                        new ChoiceOption("It is the fiery bite of Peperoncino.", () -> {
                            weakness = ElementalType.FIRE;
                        }),
                        new ChoiceOption("Or maybe a pinch of Sea Salt.", () -> {
                            weakness = ElementalType.ICE;
                        }),
                        new ChoiceOption("Perhaps the earthy scent of Basil.", () -> {
                            weakness = ElementalType.NATURE;
                        })
                ),
                DialogueInputType.CHOICE_ONLY
        );

        dialogueService.runDialogueWithInput(weaknessChoice);
    }


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
                new Dialogue("Narrator", "Here are your options:"),
                new Dialogue("Narrator", """
                        - Attack: A straightforward physical strike.
                        - Spells: Up to 3 -> they have Cooldowns.
                        * Books: Assigns Spells -> No duplicates.
                        - Items: Up to 3 Potion/Elixirs -> No duplicates\s
                        - Flee: Escape -> 50% chance, ends the Battle""")
        ), this::nextStep);
    }

    private void startBattle() {
        Battle battle = new Battle(gui, GameState.get().getPlayer(), EnemyFactory.createBandit(GameState.get().getPlayer().getLevel()));

        battle.setOnBattleEnd(result -> {
            switch (result) {
                case VICTORY ->
                // Resume the tutorial sequence
                {
                    DeveloperLogger.log("Player won");
                    GameState.get().setMissionFlag(MissionType.TUTORIAL);
                    MessageDialog.showMessageDialog(gui, "Item Found", "You found the Booma Zap book!");
                    GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_energyblast"));
                    MessageDialog.showMessageDialog(gui, "Item Found", "You found the Fire Meatball book!");
                    GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_fireball"));
                    MessageDialog.showMessageDialog(gui, "Item Found", "You found the Brrr Gelato book!");
                    GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_frostbite"));
                    MessageDialog.showMessageDialog(gui, "Item Found", "You found the Green Finger Slap book!");
                    GameState.get().getPlayer().addItemToInventory(ItemRegistry.getItemById("book_vinewhip"));
                    SceneManager.get().switchTo(this);
                }

                case DEFEAT ->
                    // Show alternate dialogue and go to WorldHub
                        dialogueService.runDialogues(List.of(
                                new Dialogue("Narrator", "You... lost.")
                        ), () -> SceneManager.get().switchTo(new MainMenu(gui)));
                case FLED ->
                    // Show alternate dialogue and go to WorldHub
                        dialogueService.runDialogues(List.of(
                                new Dialogue("Narrator", "You... escaped from the Tutorial?! Get out of Here!!!")
                        ), () -> SceneManager.get().switchTo(new MainMenu(gui)));

                default -> throw new IllegalStateException("Unexpected value: " + result);
            }
        });

        SceneManager.get().switchTo(battle);
    }


    private void showPostBattleDialogues() {
        dialogueService.runDialogues(List.of(
                new Dialogue("Narrator", "Against all odds — and after surviving that disgraceful bandit — you make it home."),
                new Dialogue("Narrator", "The door creaks open. The aroma of garlic, simmering tomatoes, and destiny fills the air..."),
                new Dialogue("Nonna", "Ah, finally! You show up just in time — the pasta's al dente and so is your fate."),
                new Dialogue("Nonna", "Sit, mangia. But listen closely... there's something you must know."),
                new Dialogue("Nonna", "Last night, while I was kneading dough, the flour... it whispered to me."),
                new Dialogue("Nonna", "A vision — of a time long past. Of an ancient recipe lost to war and greed."),
                new Dialogue("Nonna", "They say whoever gathers the sacred ingredients will restore balance to the kitchen... and perhaps, the world."),
                new Dialogue("Nonna", "And you, my little gnocchetto... you are the chosen one."),
                new Dialogue("Nonna", "Eat well tonight — for tomorrow, you begin the journey of a thousand bites."),
                // The day after
                new Dialogue("Narrator", "The night passes quietly. Outside, the village stirs awake. But inside Nonna’s house, a storm of oregano-scented destiny is about to brew. The sun rises. And so does Nonna — already halfway through her third espresso."),
                new Dialogue("Nonna", "You slept like a sack of potatoes, ragazzo. But today... you boil."),
                new Dialogue("Hero", "Nonna, about last night... was that real? The flour prophecy and the lost recipe?"),
                new Dialogue("Nonna", "Real as a wood-fired oven. And twice as hot. The world is broken. These families fight for control of taste itself, and not one of them remembers the old ways."),
                new Dialogue("Nonna", "You — you will gather the sacred ingredients. The **true** recipe lies scattered, torn, and hidden."),
                new Dialogue("Hero", "But where do I even begin?"),
                new Dialogue("Nonna", "Start with what we lack: the Golden Garlic of Belmonte. Last I heard, some goons from the Parmesani family were sniffing around there."),
                new Dialogue("Hero", "Golden... garlic?"),
                new Dialogue("Nonna", "Yes. Not that market rotten nonsense. This one shines. It sings."),
                new Dialogue("Nonna", "Now get going, before I turn you into sauce myself!"),
                new Dialogue("Nonna", "And remember: if you die out there, you better still show up for dinner — or I’ll drag you back myself."),
                new Dialogue("Narrator", "Thus, your journey begins — with nothing but your courage, Nonna’s words, and a faint smell of roasted garlic in your clothes.")
        ), this::nextStep);
    }

    private void goToWorldHub() {
        SceneManager.get().switchTo(new WorldHub(gui, GameState.get().getPlayer()));
    }
}
