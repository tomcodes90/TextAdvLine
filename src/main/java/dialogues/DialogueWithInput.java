package dialogues;

import lombok.Getter;

import java.util.List;

/**
 * Class: DialogueWithInput
 * <p>
 * Purpose:
 * Extends a basic dialogue line with additional interactivity.
 * Supports either:
 * - A set of predefined choices (multiple choice)
 * - A free-form text input (e.g. for name entry)
 * <p>
 * Used for interactive dialogues like questions, decisions, or custom player input.
 */
@Getter
public class DialogueWithInput extends Dialogue {

    /**
     * List of possible options shown to the player (can be empty for text input)
     */
    private final List<ChoiceOption> options;

    /**
     * Defines how the player is expected to respond: typed text or choice selection
     */
    private final DialogueInputType inputType;

    /**
     * Constructor
     *
     * @param speaker   The character speaking
     * @param text      The dialogue line spoken
     * @param options   List of options (used only in CHOICE_ONLY mode)
     * @param inputType Type of player interaction expected
     */
    public DialogueWithInput(String speaker, String text,
                             List<ChoiceOption> options, DialogueInputType inputType) {
        super(speaker, text);
        this.options = options;
        this.inputType = inputType;
    }
}
