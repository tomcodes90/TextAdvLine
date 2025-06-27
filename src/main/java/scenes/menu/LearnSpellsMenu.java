package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import items.Book;
import items.Item;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.CharacterOverview;
import spells.Spell;
import spells.SpellFactory;
import spells.SpellType;

import java.util.List;

public class LearnSpellsMenu implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    public LearnSpellsMenu(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("Learn Spells");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("Select a spell-book:"));
        panel.addComponent(new EmptySpace());

        boolean hasBooks = false;
        for (Item item : player.getInventory().keySet()) {
            if (item instanceof Book book) {
                hasBooks = true;
                panel.addComponent(new Button(
                        "x " + book.getSpell().getName(),
                        () -> openSlotSelector(book)
                ));
            }
        }

        if (!hasBooks) panel.addComponent(new Label("You don't have any books."));

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("← Back", () -> {
            window.close();
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
        }));

        window.setComponent(panel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    /* -------- choose slot window -------- */
    private void openSlotSelector(Book book) {
        Spell newSpell = book.getSpell();

        BasicWindow select = new BasicWindow("Choose Slot");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("Equip " + newSpell.getName() + " in which slot?"));

        for (int i = 0; i < 3; i++) {
            int slot = i;
            String current = player.getSpellsEquipped()[i] == null
                    ? "(empty)" : String.valueOf(player.getSpellsEquipped()[i].getName());

            String label = "Slot " + (slot + 1) + ": " + current;
            panel.addComponent(new Button(label, () -> {
                /* ---- duplicate check ---- */
                for (int j = 0; j < 3; j++) {
                    if (j != slot && player.getSpellsEquipped()[j] != null &&
                            player.getSpellsEquipped()[j].getName() == newSpell.getName()) {
                        MessageDialog.showMessageDialog(gui, "Already equipped",
                                "That spell is already in another slot.");
                        return;
                    }
                }
                /* ---- equip via book ---- */
                book.use(player, slot);

                MessageDialog.showMessageDialog(gui, "Learned",
                        "Equipped " + newSpell.getName() + " in slot " + (slot + 1));
                select.close();
            }));
        }

        panel.addComponent(new Button("Cancel", select::close));
        select.setComponent(panel);
        select.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(select);
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}
