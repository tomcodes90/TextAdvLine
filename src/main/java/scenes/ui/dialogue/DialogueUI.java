package scenes.ui.dialogue;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import dialogues.ChoiceOption;
import dialogues.DialogueWithInput;

import java.io.IOException;
import java.util.List;

public class DialogueUI {
    private final WindowBasedTextGUI gui;

    public DialogueUI(WindowBasedTextGUI gui) {
        this.gui = gui;
    }

    // DialogueUI.java
    public void showDialogue(String speaker, String portrait, String text) throws InterruptedException, IOException {
        BasicWindow window = new BasicWindow();
        Panel root = new Panel();
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        root.addComponent(new Label(portrait + " " + speaker));
        root.addComponent(new EmptySpace());

        Label dialogueLabel = new Label("");
        root.addComponent(dialogueLabel); // Empty, filled in by typewriter
        root.addComponent(new EmptySpace());

        root.addComponent(new Label("(Press Enter to continue)"));

        window.setComponent(root);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindow(window);

        try {
            typewriter(dialogueLabel, text); // 🎯 Animate it here
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Wait for Enter
        KeyStroke key;
        do {
            key = gui.getScreen().readInput();
        } while (key == null || key.getKeyType() != KeyType.Enter);

        gui.removeWindow(window);
    }


    public void showChoices(DialogueWithInput dialogueWithInput) {
        BasicWindow window = new BasicWindow("Make a Choice");
        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));
        window.setHints(java.util.List.of(Window.Hint.CENTERED));

        Label promptLabel = new Label(dialogueWithInput.getText());
        promptLabel.setForegroundColor(TextColor.ANSI.CYAN);
        root.addComponent(promptLabel);

        ActionListBox listBox = new ActionListBox(new TerminalSize(40, dialogueWithInput.getOptions().size()));
        for (ChoiceOption option : dialogueWithInput.getOptions()) {
            listBox.addItem(option.getText(), option::execute);
        }

        root.addComponent(listBox);
        window.setComponent(root);

        gui.addWindowAndWait(window);
    }

    private void typewriter(Label label, String text) throws InterruptedException, IOException {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(c);
            label.setText(sb.toString());
            gui.updateScreen(); // ✅ refresh display
            Thread.sleep(15);
        }
    }


}

