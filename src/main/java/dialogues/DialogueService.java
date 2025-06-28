package dialogues;

import lombok.Getter;
import scenes.ui.DialogueUI;

import java.io.IOException;
import java.util.List;

/**
 * Class: DialogueService
 * <p>
 * Purpose:
 * Central singleton that manages the flow of dialogue scenes.
 * Handles both simple dialogues and interactive dialogues with input or choices.
 * Delegates UI rendering to DialogueUI.
 */
public class DialogueService {

    /**
     * Global singleton instance of the service
     */
    @Getter
    private static final DialogueService instance = new DialogueService();

    /**
     * Reference to the current DialogueUI responsible for rendering
     */
    private DialogueUI dialogueUI;

    /**
     * Private constructor (singleton pattern)
     */
    private DialogueService() {
    }

    /**
     * Sets the UI implementation to use for rendering dialogues.
     *
     * @param ui The DialogueUI instance (typically created per scene)
     */
    public void setUI(DialogueUI ui) {
        this.dialogueUI = ui;
    }

    /**
     * Runs a sequence of dialogue lines one after another.
     * Blocks the flow until all dialogues are shown.
     *
     * @param dialogues List of dialogue lines to show in order
     * @param onFinish  Callback to run once all lines are finished
     */
    public void runDialogues(List<Dialogue> dialogues, Runnable onFinish) {
        try {
            for (Dialogue d : dialogues) {
                dialogueUI.showDialogue(d.getSpeaker(), d.getText());
            }
            onFinish.run();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // Optional: replace with a custom logger
        }
    }

    /**
     * Runs a single dialogue that includes a player input interaction.
     * Decides between free-text input or multiple choice based on inputType.
     *
     * @param dialogue A dialogue line expecting player input
     */
    public void runDialogueWithInput(DialogueWithInput dialogue) {
        try {
            if (dialogue.getInputType() == DialogueInputType.TEXT_INPUT) {
                dialogueUI.showInputDialogue(
                        dialogue.getSpeaker(),
                        dialogue.getText(),
                        dialogue.getOptions()
                );
            } else {
                dialogueUI.showDialogueWithInput(
                        dialogue.getSpeaker(),
                        dialogue.getText(),
                        dialogue.getOptions()
                );
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // Optional: replace with a custom logger
        }
    }
}
