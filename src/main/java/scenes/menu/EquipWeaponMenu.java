package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import items.Item;
import items.equip.Weapon;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.CharacterOverview;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EquipWeaponMenu implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    private static final int ITEMS_PER_PAGE = 6;

    public EquipWeaponMenu(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("🗡 Equip Weapon");
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));

        Weapon currentWeapon = player.getWeapon();
        String equippedText = (currentWeapon != null) ? currentWeapon.getName() : "None";
        mainPanel.addComponent(new Label("Currently Equipped: " + equippedText));
        mainPanel.addComponent(new EmptySpace());

        // Get sorted weapons from inventory
        List<Weapon> weapons = player.getInventory().keySet().stream()
                .filter(item -> item instanceof Weapon)
                .map(item -> (Weapon) item)
                .sorted(Comparator.comparing(Item::getName))
                .collect(Collectors.toList());

        if (weapons.isEmpty()) {
            mainPanel.addComponent(new Label("No weapons available in inventory."));
        } else {
            int totalPages = (int) Math.ceil(weapons.size() / (double) ITEMS_PER_PAGE);
            final int[] currentPage = {0};

            Panel itemListPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            mainPanel.addComponent(itemListPanel);

            Runnable updatePage = () -> {
                itemListPanel.removeAllComponents();
                int start = currentPage[0] * ITEMS_PER_PAGE;
                int end = Math.min(start + ITEMS_PER_PAGE, weapons.size());

                for (int i = start; i < end; i++) {
                    Weapon weapon = weapons.get(i);
                    int quantity = player.getInventory().getOrDefault(weapon, 0);

                    Panel itemBlock = new Panel(new LinearLayout(Direction.VERTICAL));
                    itemBlock.setPreferredSize(new TerminalSize(50, 4));
                    itemBlock.addComponent(new Label(weapon.getName() + " x" + quantity));
                    itemBlock.addComponent(new Label(weapon.getDescription()));
                    itemBlock.addComponent(new Label("Dmg: " + weapon.getDamage()));
                    itemBlock.addComponent(new Button("Equip", () -> {
                        player.setWeapon(weapon);
                        player.removeItemFromInventory(weapon);
                        if (currentWeapon != null) {
                            player.addItemToInventory(currentWeapon);
                        }
                        MessageDialog.showMessageDialog(gui, "Equipped", "You equipped: " + weapon.getName());
                        window.close();
                        SceneManager.get().switchTo(new EquipWeaponMenu(gui, player));
                    }));
                    itemListPanel.addComponent(itemBlock);
                    itemListPanel.addComponent(new EmptySpace());
                }
            };

            Panel paginationPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            Button prev = new Button("< Prev", () -> {
                if (currentPage[0] > 0) {
                    currentPage[0]--;
                    updatePage.run();
                }
            });
            Button next = new Button("Next >", () -> {
                if (currentPage[0] < totalPages - 1) {
                    currentPage[0]++;
                    updatePage.run();
                }
            });
            paginationPanel.addComponent(prev);
            paginationPanel.addComponent(new EmptySpace(new TerminalSize(1, 0)));
            paginationPanel.addComponent(next);

            updatePage.run();
            mainPanel.addComponent(new EmptySpace());
            mainPanel.addComponent(paginationPanel);
        }

        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(new Button("⬅ Back", () -> {
            window.close();
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
        }));

        window.setComponent(mainPanel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}
