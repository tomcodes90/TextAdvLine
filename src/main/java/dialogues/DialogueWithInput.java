package dialogues;

import java.util.List;

public class DialogueWithInput {
    private final String prompt;
    private final List<ChoiceOption> options;

    public DialogueWithInput(String prompt, List<ChoiceOption> options) {
        this.prompt = prompt;
        this.options = options;
    }

    public List<ChoiceOption> getOptions() {
        return options;
    }

    public String getPrompt() {
        return prompt;
    }
}
