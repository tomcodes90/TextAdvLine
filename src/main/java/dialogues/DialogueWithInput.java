package dialogues;

import lombok.Getter;

import java.util.List;

@Getter
public class DialogueWithInput extends Dialogue {
    private final List<ChoiceOption> options;
    private final DialogueInputType inputType;

    public DialogueWithInput(String speaker, String text,
                             List<ChoiceOption> options, DialogueInputType inputType) {
        super(speaker, text);
        this.options = options;
        this.inputType = inputType;
    }
}
