package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import items.Book;
import items.Item;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.CharacterOverview;
import spells.Spell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EquipSpellsMenu implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    private static final int SPELLS_PER_PAGE = 6;

    public EquipSpellsMenu(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("📖 Learn Spells");

        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        mainPanel.addComponent(new Label("Select a spell-book:"));
        mainPanel.addComponent(new EmptySpace());

        List<Book> spellBooks = player.getInventory().keySet().stream()
                .filter(item -> item instanceof Book)
                .map(item -> (Book) item)
                .sorted(Comparator.comparing(b -> b.getSpell().getName()))
                .toList();

        if (spellBooks.isEmpty()) {
            mainPanel.addComponent(new Label("You don't have any books."));
        } else {
            int totalPages = (int) Math.ceil(spellBooks.size() / (double) SPELLS_PER_PAGE);
            final int[] currentPage = {0};

            Panel spellListPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            mainPanel.addComponent(spellListPanel);

            Runnable updatePage = () -> {
                spellListPanel.removeAllComponents();
                int start = currentPage[0] * SPELLS_PER_PAGE;
                int end = Math.min(start + SPELLS_PER_PAGE, spellBooks.size());

                for (int i = start; i < end; i++) {
                    Book book = spellBooks.get(i);
                    Spell spell = book.getSpell();

                    Panel spellPanel = new Panel(new LinearLayout(Direction.VERTICAL));
                    spellPanel.setPreferredSize(new TerminalSize(50, 4));
                    spellPanel.addComponent(new Label(spell.getName().toString() + " | Element: " + spell.getElement()));

                    spellPanel.addComponent(new Label(spell.getDamage() + " DMG | " + spell.getCooldown() + " Cooldown"));
                    spellPanel.addComponent(new Button("Learn", () -> openSlotSelector(book)));
                    spellPanel.addComponent(new EmptySpace());

                    spellListPanel.addComponent(spellPanel);
                }
            };

            // Pagination controls
            Panel paginationPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            Button prev = new Button("<- Prev", () -> {
                if (currentPage[0] > 0) {
                    currentPage[0]--;
                    updatePage.run();
                }
            });
            Button next = new Button("Next ->", () -> {
                if (currentPage[0] < totalPages - 1) {
                    currentPage[0]++;
                    updatePage.run();
                }
            });

            paginationPanel.addComponent(prev);
            paginationPanel.addComponent(new EmptySpace(new TerminalSize(1, 0)));
            paginationPanel.addComponent(next);

            mainPanel.addComponent(new EmptySpace());
            mainPanel.addComponent(paginationPanel);
            updatePage.run();
        }

        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(new Button("<- Back", () -> {
            window.close();
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
        }));

        window.setComponent(mainPanel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    private void openSlotSelector(Book book) {
        Spell newSpell = book.getSpell();

        BasicWindow select = new BasicWindow("Choose Slot");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("Equip " + newSpell.getName() + " in which slot?"));

        for (int i = 0; i < 3; i++) {
            int slot = i;
            String current = player.getSpellsEquipped()[i] == null
                    ? "(empty)" : player.getSpellsEquipped()[i].getName().toString();

            String label = "Slot " + (slot + 1) + ": " + current;
            panel.addComponent(new Button(label, () -> {
                for (int j = 0; j < 3; j++) {
                    if (j != slot && player.getSpellsEquipped()[j] != null &&
                            player.getSpellsEquipped()[j].getName().equals(newSpell.getName())) {
                        MessageDialog.showMessageDialog(gui, "Already equipped",
                                "That spell is already in another slot.");
                        return;
                    }
                }

                book.use(player, slot);
                MessageDialog.showMessageDialog(gui, "Learned",
                        "Equipped " + newSpell.getName() + " in slot " + (slot + 1));
                select.close();
                enter(); // Refresh
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
        if (window != null) {
            gui.removeWindow(window); // force remove from the stack
            window = null;            // clear reference
        }
    }

}
