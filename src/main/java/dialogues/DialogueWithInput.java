package dialogues;

import lombok.Getter;

import java.util.List;

@Getter
public class DialogueWithInput extends Dialogue {
    private final List<ChoiceOption> options;
    private final DialogueInputType inputType;

    public DialogueWithInput(String speaker, String text, String portrait,
                             List<ChoiceOption> options, DialogueInputType inputType) {
        super(speaker, text, portrait);
        this.options = options;
        this.inputType = inputType;
    }
}
