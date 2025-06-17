package dialogues;

public class ChoiceOption {
    private final String text;
    private final Runnable action;

    public ChoiceOption(String text, Runnable action) {
        this.text = text;
        this.action = action;
    }

    public String getText() {
        return text;
    }

    public void execute() {
        action.run();
    }
}
