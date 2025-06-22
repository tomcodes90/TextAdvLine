package dialogues;

import lombok.Getter;
import scenes.ui.dialogue.DialogueUI;

import java.io.IOException;
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

    public void runDialogues(List<Dialogue> dialogues) throws IOException, InterruptedException {
        for (Dialogue d : dialogues) {
            dialogueUI.showDialogue(d.getSpeaker(), d.getPortrait(), d.getText());
        }
    }
}
