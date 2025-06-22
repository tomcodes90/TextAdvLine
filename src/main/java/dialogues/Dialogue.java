package dialogues;

import lombok.Getter;

@Getter
public class Dialogue {
    private final String speaker;
    private final String text;
    private final String portrait;

    public Dialogue(String speaker, String text, String portrait) {
        this.speaker = speaker;
        this.text = text;
        this.portrait = portrait;
    }

}
