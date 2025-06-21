package dialogues;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChoiceOption {
    private final String text;
    private final Runnable action;

    public void execute() {
        action.run();
    }
}
