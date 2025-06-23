package scenes.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import dialogues.ChoiceOption;

import java.io.IOException;
import java.util.List;

public class DialogueUI {
    private final WindowBasedTextGUI gui;
    public static String lastInput = "";


    public DialogueUI(WindowBasedTextGUI gui) {
        this.gui = gui;
    }

    public void showDialogue(String speaker, String portrait, String text) throws InterruptedException, IOException {
        BasicWindow window = new BasicWindow();
        Panel root = new Panel();
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        String speakerLine = (portrait != null ? portrait + " " : "") + speaker;
        root.addComponent(new Label(speakerLine));
        root.addComponent(new EmptySpace());

        Label dialogueLabel = new Label("");
        root.addComponent(dialogueLabel);
        root.addComponent(new EmptySpace());

        Button continueButton = new Button("Continue", window::close);
        root.addComponent(continueButton);

        window.setComponent(root);
        window.setHints(List.of(Window.Hint.CENTERED));

        // ✅ Start animation in a background thread
        new Thread(() -> {
            try {
                typewriter(dialogueLabel, text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // ✅ Block until user clicks "Continue"
        gui.addWindowAndWait(window);
    }


    public void showDialogueWithInput(String speaker, String portrait, String text, List<ChoiceOption> options)
            throws InterruptedException, IOException {

        BasicWindow window = new BasicWindow("Your Response");
        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));
        window.setHints(List.of(Window.Hint.CENTERED));

        if (speaker != null || portrait != null) {
            String line = (portrait != null ? portrait + " " : "") + (speaker != null ? speaker : "");
            root.addComponent(new Label(line));
        }

        root.addComponent(new EmptySpace());

        Label promptLabel = new Label(text);
        promptLabel.setForegroundColor(TextColor.ANSI.CYAN);
        root.addComponent(promptLabel);
        root.addComponent(new EmptySpace());

        ActionListBox listBox = new ActionListBox(new TerminalSize(40, options.size()));
        for (ChoiceOption option : options) {
            listBox.addItem(option.getText(), () -> {
                gui.removeWindow(window); // Close first
                option.execute();         // Then run action
            });
        }

        root.addComponent(listBox);
        window.setComponent(root);

        gui.addWindowAndWait(window); // ✅ blocks until Enter is pressed

    }

    public void showInputDialogue(String speaker, String portrait, String prompt, List<ChoiceOption> options)
            throws IOException, InterruptedException {

        BasicWindow window = new BasicWindow("Input Required");
        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));
        window.setHints(List.of(Window.Hint.CENTERED));

        if (speaker != null || portrait != null) {
            String line = (portrait != null ? portrait + " " : "") + (speaker != null ? speaker : "");
            root.addComponent(new Label(line));
        }

        root.addComponent(new EmptySpace());

        root.addComponent(new Label(prompt));
        root.addComponent(new EmptySpace());

        TextBox inputField = new TextBox(new TerminalSize(40, 1));
        inputField.takeFocus(); // ✅ this ensures input
        root.addComponent(inputField);
        root.addComponent(new EmptySpace());

        ActionListBox listBox = new ActionListBox(new TerminalSize(40, options.size()));
        for (ChoiceOption option : options) {
            listBox.addItem(option.getText(), () -> {
                DialogueUI.lastInput = inputField.getText().trim();
                window.close();
                option.execute();
            });
        }

        root.addComponent(listBox);
        window.setComponent(root);

        gui.addWindowAndWait(window); // ✅ THIS LINE IS KEY
    }


    private void typewriter(Label label, String text) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(c);
            String current = sb.toString();

            gui.getGUIThread().invokeLater(() -> label.setText(current));
            Thread.sleep(15);
        }

    }

}
