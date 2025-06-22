package dialogues;

import lombok.Getter;

import java.util.List;

@Getter

public class DialogueWithInput extends Dialogue {

    private final List<ChoiceOption> options;

    public DialogueWithInput(String speaker, String text, String portrait, List<ChoiceOption> options) {
        super(speaker, text, portrait);
        this.options = options;
    }
}
