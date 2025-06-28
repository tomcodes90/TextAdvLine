package dialogues;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Class: ChoiceOption
 * <p>
 * Purpose:
 * Represents a single selectable option in a dialogue.
 * Each option has a label (text shown to the player) and an action (code to run when selected).
 * <p>
 * Used in DialogueWithInput when the input type is CHOICE_ONLY.
 *
 * @param text   Text label shown to the player (e.g. "Yes", "No", "Pasta!")
 * @param action Action to execute if this option is selected
 */

public record ChoiceOption(String text, Runnable action) {

    /**
     * Executes the associated action for this choice.
     */
    public void execute() {
        action.run();
    }
}
