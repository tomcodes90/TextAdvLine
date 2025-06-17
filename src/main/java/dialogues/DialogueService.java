package dialogues;

import java.util.List;
import java.util.Scanner;

public class DialogueService {

    private static final DialogueService instance = new DialogueService();

    private DialogueService() {
        // private constructor
    }

    public static DialogueService getInstance() {
        return instance;
    }

    public void runDialogues(List<Dialogue> dialogues) {
        for (Dialogue d : dialogues) {
            System.out.println(d.getSpeaker() + ": " + d.getText());
            waitForUser();
        }
    }

    private void waitForUser() {
        System.out.println("(Press Enter to continue)");
        new Scanner(System.in).nextLine();
    }
}
