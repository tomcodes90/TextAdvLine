package dialogues;

import lombok.Getter;
import scenes.ui.DialogueUI;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class DialogueService {


    @Getter
    private static final DialogueService instance = new DialogueService();
    private DialogueUI dialogueUI;

    private DialogueService() {
    }

    public void setUI(DialogueUI ui) {
        this.dialogueUI = ui;
    }

    public void runDialogues(List<Dialogue> dialogues, Runnable onFinish) {
        try {
            for (Dialogue d : dialogues) {
                dialogueUI.showDialogue(d.getSpeaker(), d.getText());
            }
            onFinish.run();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void runDialogueWithInput(DialogueWithInput dialogue) {
        try {
            if (dialogue.getInputType() == DialogueInputType.TEXT_INPUT) {
                dialogueUI.showInputDialogue(
                        dialogue.getSpeaker(),
                        dialogue.getText(),
                        dialogue.getOptions()
                );
            } else {
                dialogueUI.showDialogueWithInput(
                        dialogue.getSpeaker(),
                        dialogue.getText(),
                        dialogue.getOptions()
                );
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
