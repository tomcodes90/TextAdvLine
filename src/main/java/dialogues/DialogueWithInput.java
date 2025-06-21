package dialogues;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DialogueWithInput {
    private final String prompt;
    private final List<ChoiceOption> options;
}
