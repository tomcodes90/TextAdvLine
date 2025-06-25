package dialogues;

import lombok.Getter;

@Getter
public class Dialogue {
    private final String speaker;
    private final String text;

    public Dialogue(String speaker, String text) {
        this.speaker = speaker;
        this.text = text;
    }

}
