package dialogues;

import lombok.Getter;

/**
 * Class: Dialogue
 * <p>
 * Purpose:
 * Represents a single dialogue in the game.
 * Holds both the speaker's name and the line of text.
 * Used in cutscenes, conversations, and scripted events.
 */
@Getter
public class Dialogue {

    /**
     * Name of the speaker (e.g. "Nonna", "Bandit")
     */
    private final String speaker;

    /**
     * Text spoken by the speaker
     */
    private final String text;

    /**
     * Constructor
     *
     * @param speaker The character delivering the line
     * @param text    The actual dialogue content
     */
    public Dialogue(String speaker, String text) {
        this.speaker = speaker;
        this.text = text;
    }
}
