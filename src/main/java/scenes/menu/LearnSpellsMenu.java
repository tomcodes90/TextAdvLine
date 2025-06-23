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
        window = new BasicWindow("-> Learn Spells");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("Select a spellbook to learn from:"));
        panel.addComponent(new EmptySpace());

        boolean hasBooks = false;
        for (Item item : player.getInventory().keySet()) {
            if (item instanceof Book book) {
                hasBooks = true;
                SpellType spellType = book.getSpellType();
                Spell spell = SpellFactory.create(spellType);

                panel.addComponent(new Button("📖 " + spell.getName(), () -> openSlotSelector(spell)));
            }
        }

        if (!hasBooks) {
            panel.addComponent(new Label("You don't have any books."));
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("⬅ Back", () -> {
            window.close();
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
        }));

        window.setComponent(panel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    private void openSlotSelector(Spell newSpell) {
        BasicWindow selectWindow = new BasicWindow("🧠 Choose Slot");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("Choose a slot to equip:"));

        for (int i = 0; i < player.getSpellsEquipped().length; i++) {
            int slot = i;
            String label = String.format("Slot %d: %s", slot + 1,
                    player.getSpellsEquipped()[i] == null ? "(empty)" : player.getSpellsEquipped()[i].getName());

            panel.addComponent(new Button(label, () -> {
                // Prevent equipping duplicate spells in other slots
                for (int j = 0; j < player.getSpellsEquipped().length; j++) {
                    if (j != slot && newSpell.getName().equals(player.getSpellsEquipped()[j] != null
                            ? player.getSpellsEquipped()[j].getName() : null)) {
                        MessageDialog.showMessageDialog(gui, "Already Learned",
                                "This spell is already equipped in another slot.");
                        return;
                    }
                }

                // Prevent reassigning the same spell to the same slot
                if (newSpell.getName().equals(player.getSpellsEquipped()[slot] != null
                        ? player.getSpellsEquipped()[slot].getName() : null)) {
                    MessageDialog.showMessageDialog(gui, "No Change",
                            "This spell is already equipped in this slot.");
                    return;
                }

                player.getSpellsEquipped()[slot] = newSpell;
                MessageDialog.showMessageDialog(gui, "Learned",
                        "Equipped " + newSpell.getName() + " to slot " + (slot + 1));
                selectWindow.close();
            }));
        }

        panel.addComponent(new Button("⬅ Cancel", selectWindow::close));
        selectWindow.setComponent(panel);
        selectWindow.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(selectWindow);
    }

    @Override
    public void handleInput() {}

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}
