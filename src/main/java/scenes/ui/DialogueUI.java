// File: scenes/ui/DialogueUI.java
package scenes.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import dialogues.ChoiceOption;
import state.GameState;
import util.PortraitRegistry;

import java.io.IOException;
import java.util.List;

/**
 * DialogueUI is responsible for displaying dialogue boxes, character portraits,
 * and branching options in the form of selectable choices.
 * <p>
 * 🧠 Lanterna Notes:
 * - We use `BasicWindow` for each dialogue box. It's modal and waits for user interaction.
 * - Threading is delicate: the typewriter effect uses a background thread but
 * updates the UI with `invokeLater()` which is safe.
 * - All UI elements (Label, Panel, Button) must be manipulated on the GUI thread.
 */
public class DialogueUI {
    private final WindowBasedTextGUI gui;

    // Stores the latest text input from the user (used for name input)
    public static String lastInput = "";

    public DialogueUI(WindowBasedTextGUI gui) {
        this.gui = gui;
    }

    /**
     * Shows a basic dialogue with speaker, text, portrait, and a "Continue" button.
     * A typewriter animation is used for text rendering.
     */
    public void showDialogue(String speaker, String text) throws InterruptedException, IOException {
        BasicWindow window = new BasicWindow();
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));

        // === LEFT SIDE: Portrait panel ===
        Panel leftPanel = createPortraitPanel(speaker);
        leftPanel.setPreferredSize(new TerminalSize(20, 10));

        // === RIGHT SIDE: Dialogue content ===
        Panel rightContent = new Panel(new LinearLayout(Direction.VERTICAL));

        Label dialogueLabel = new Label(""); // text rendered letter by letter
        Label speakerLabel = new Label(resolveDisplayName(speaker));
        speakerLabel.setForegroundColor(TextColor.ANSI.YELLOW);
        speakerLabel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        rightContent.addComponent(speakerLabel);
        rightContent.addComponent(new EmptySpace());
        rightContent.addComponent(dialogueLabel);
        rightContent.addComponent(new EmptySpace());

        Button continueButton = new Button("Continue", window::close);
        Panel continuePanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        continuePanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        continuePanel.addComponent(continueButton);

        rightContent.addComponent(continuePanel);

        // Wrap right content for vertical centering flexibility
        Panel rightWrapper = wrapRightContent(rightContent);

        root.addComponent(leftPanel);
        root.addComponent(new EmptySpace(new TerminalSize(2, 1))); // spacing between panels
        root.addComponent(rightWrapper);

        window.setComponent(root);
        window.setHints(List.of(Window.Hint.CENTERED)); // Centered modal window

        // 🧵 Typewriter effect runs in separate thread but updates UI via invokeLater
        new Thread(() -> {
            try {
                typewriter(dialogueLabel, wrapText(text, 50));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        gui.addWindowAndWait(window); // Blocks until window is closed
    }

    /**
     * Shows dialogue with a list of selectable options.
     * Each option executes a callback.
     */
    public void showDialogueWithInput(String speaker, String text, List<ChoiceOption> options)
            throws InterruptedException, IOException {

        BasicWindow window = new BasicWindow("Your Response");
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));
        window.setHints(List.of(Window.Hint.CENTERED));

        Panel leftPanel = createPortraitPanel(speaker);

        Panel rightContent = new Panel(new LinearLayout(Direction.VERTICAL));
        rightContent.setPreferredSize(new TerminalSize(50, 10));

        Label speakerLabel = new Label(speaker);
        speakerLabel.setForegroundColor(TextColor.ANSI.YELLOW);
        speakerLabel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        rightContent.addComponent(speakerLabel);
        rightContent.addComponent(new EmptySpace());

        Label promptLabel = new Label(wrapText(text, 48));
        promptLabel.setForegroundColor(TextColor.ANSI.BLUE_BRIGHT);
        rightContent.addComponent(promptLabel);
        rightContent.addComponent(new EmptySpace());

        // 📦 ListBox for choices
        ActionListBox listBox = new ActionListBox(new TerminalSize(48, options.size()));
        for (ChoiceOption option : options) {
            listBox.addItem(option.text(), () -> {
                gui.removeWindow(window);
                option.execute();
            });
        }
        rightContent.addComponent(listBox);

        Panel rightWrapper = wrapRightContent(rightContent);
        root.addComponent(leftPanel);
        root.addComponent(new EmptySpace(new TerminalSize(2, 1)));
        root.addComponent(rightWrapper);

        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    /**
     * Shows dialogue where the user must type input (e.g., for name or response),
     * then choose an option.
     */
    public void showInputDialogue(String speaker, String prompt, List<ChoiceOption> options)
            throws IOException, InterruptedException {

        BasicWindow window = new BasicWindow("Input Required");
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));
        window.setHints(List.of(Window.Hint.CENTERED));

        Panel leftPanel = createPortraitPanel(speaker);

        Panel rightContent = new Panel(new LinearLayout(Direction.VERTICAL));
        rightContent.setPreferredSize(new TerminalSize(50, 10));

        Label speakerLabel = new Label(speaker);
        speakerLabel.setForegroundColor(TextColor.ANSI.YELLOW);
        speakerLabel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        rightContent.addComponent(speakerLabel);
        rightContent.addComponent(new EmptySpace());

        Label promptLabel = new Label(wrapText(prompt, 48));
        promptLabel.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        rightContent.addComponent(promptLabel);
        rightContent.addComponent(new EmptySpace());

        TextBox inputField = new TextBox(new TerminalSize(48, 1));
        inputField.takeFocus();
        rightContent.addComponent(inputField);
        rightContent.addComponent(new EmptySpace());

        ActionListBox listBox = new ActionListBox(new TerminalSize(48, options.size()));
        for (ChoiceOption option : options) {
            listBox.addItem(option.text(), () -> {
                lastInput = inputField.getText().trim(); // Capture user input
                window.close();
                option.execute();
            });
        }
        rightContent.addComponent(listBox);

        Panel rightWrapper = wrapRightContent(rightContent);
        root.addComponent(leftPanel);
        root.addComponent(new EmptySpace(new TerminalSize(2, 1)));
        root.addComponent(rightWrapper);

        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    // === INTERNAL HELPERS ===

    /**
     * Creates a vertically-centered portrait panel using ASCII art from PortraitRegistry
     */
    private Panel createPortraitPanel(String speaker) {
        Panel leftPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        leftPanel.setPreferredSize(new TerminalSize(20, 12));

        Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        contentPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        if (speaker != null) {
            String[] portraitLines = PortraitRegistry.get(speaker);
            if (portraitLines == null) {
                portraitLines = PortraitRegistry.get("default");
            }
            for (String line : portraitLines) {
                contentPanel.addComponent(new Label(line));
            }
        }

        // Add spacing above and below for vertical balance
        leftPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)),
                LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        leftPanel.addComponent(contentPanel);
        leftPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)),
                LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        return leftPanel;
    }

    /**
     * Wraps right-side content to allow flexible centering
     */
    private Panel wrapRightContent(Panel content) {
        Panel wrapper = new Panel(new LinearLayout(Direction.VERTICAL));
        wrapper.addComponent(content);
        return wrapper;
    }

    /**
     * Simulates a typewriter effect by gradually revealing text character by character.
     * Note: Uses `invokeLater()` to avoid modifying GUI from a non-GUI thread.
     */
    private void typewriter(Label label, String text) throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(c);
            String current = sb.toString();
            gui.getGUIThread().invokeLater(() -> label.setText(current));
            Thread.sleep(15); // control speed of typewriter effect
        }
    }

    /**
     * Word-wraps text at a specific character width for consistent formatting
     */
    private String wrapText(String input, int width) {
        return input.replaceAll("(.+" + width + "})(\\s+|$)", "$1\n");
    }

    /**
     * Replaces the name "Hero" with the actual player name from GameState
     */
    private String resolveDisplayName(String speaker) {
        if ("Hero".equals(speaker)) {
            return GameState.get().getPlayer().getName();
        }
        return speaker;
    }
}
