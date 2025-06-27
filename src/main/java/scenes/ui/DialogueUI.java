package scenes.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import dialogues.ChoiceOption;
import state.GameState;
import util.PortraitRegistry;

import java.io.IOException;
import java.util.List;

public class DialogueUI {
    private final WindowBasedTextGUI gui;
    public static String lastInput = "";

    public DialogueUI(WindowBasedTextGUI gui) {
        this.gui = gui;
    }

    public void showDialogue(String speaker, String text) throws InterruptedException, IOException {
        BasicWindow window = new BasicWindow();
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));

        // === LEFT: Portrait + Speaker Panel ===
        Panel leftPanel = createPortraitPanel(speaker);
        leftPanel.setPreferredSize(new TerminalSize(20, 10));

        // === RIGHT: Dialogue Panel Centered Vertically ===
        Panel rightContent = new Panel(new LinearLayout(Direction.VERTICAL));

        Label dialogueLabel = new Label("");
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

        Panel rightWrapper = wrapRightContent(rightContent);

        root.addComponent(leftPanel);
        root.addComponent(new EmptySpace(new TerminalSize(2, 1)));

        root.addComponent(rightWrapper);

        window.setComponent(root);
        window.setHints(List.of(Window.Hint.CENTERED));

        new Thread(() -> {
            try {
                typewriter(dialogueLabel, wrapText(text, 50));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        gui.addWindowAndWait(window);

    }

    public void showDialogueWithInput(String speaker, String text, List<ChoiceOption> options)
            throws InterruptedException, IOException {

        BasicWindow window = new BasicWindow("Your Response");
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));
        window.setHints(List.of(Window.Hint.CENTERED));

        // === LEFT: Portrait Panel ===
        Panel leftPanel = createPortraitPanel(speaker);

        // === RIGHT: Dialogue + Speaker Name + Choices ===
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

        ActionListBox listBox = new ActionListBox(new TerminalSize(48, options.size()));
        for (ChoiceOption option : options) {
            listBox.addItem(option.getText(), () -> {
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


    public void showInputDialogue(String speaker, String prompt, List<ChoiceOption> options)
            throws IOException, InterruptedException {

        BasicWindow window = new BasicWindow("Input Required");
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));
        window.setHints(List.of(Window.Hint.CENTERED));

        // === LEFT: Portrait Panel ===
        Panel leftPanel = createPortraitPanel(speaker);

        // === RIGHT: Speaker Name + Prompt + Input + Choices ===
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
            listBox.addItem(option.getText(), () -> {
                lastInput = inputField.getText().trim();
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


    private Panel createPortraitPanel(String speaker) {
        Panel leftPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        leftPanel.setPreferredSize(new TerminalSize(20, 12));

        Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        contentPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        if (speaker != null) {
            String[] portraitLines = PortraitRegistry.get(speaker);
            for (String line : portraitLines) {
                contentPanel.addComponent(new Label(line));
            }
        }

        leftPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)), LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        leftPanel.addComponent(contentPanel);
        leftPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)), LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        return leftPanel;
    }


    private Panel wrapRightContent(Panel content) {
        Panel wrapper = new Panel(new LinearLayout(Direction.VERTICAL));
        wrapper.addComponent(content); // Let content define its own height
        return wrapper;
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

    private String wrapText(String input, int width) {
        return input.replaceAll("(.{1," + width + "})(\\s+|$)", "$1\n");
    }

    private String resolveDisplayName(String speaker) {
        if ("Hero".equals(speaker)) {
            return GameState.get().getPlayer().getName(); // assumes GameState is your singleton
        }
        return speaker;
    }
}