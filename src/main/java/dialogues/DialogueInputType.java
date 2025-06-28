package dialogues;

/**
 * Enum: DialogueInputType
 * <p>
 * Purpose:
 * Specifies the type of player interaction expected during a dialogue event.
 * Used to determine whether to prompt the player for a typed input or to show predefined choices.
 */
public enum DialogueInputType {

    /**
     * Player is prompted to type a custom text input (e.g. name entry)
     */
    TEXT_INPUT,

    /**
     * Player is shown a set of choices to pick from
     */
    CHOICE_ONLY
}
