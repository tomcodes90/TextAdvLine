package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.CharacterOverview;
import items.Item;
import util.UIHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class InventoryMenu implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    public InventoryMenu(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    public void enter() {
        window = new BasicWindow("Inventory");
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        List<Item> items = player.getInventory().keySet().stream()
                .sorted(Comparator.comparing(Item::getName))
                .toList();

        if (items.isEmpty()) {
            mainPanel.addComponent(new Label("Your inventory is empty."));
        } else {
            final int ITEMS_PER_PAGE = 6;
            final int totalPages = (int) Math.ceil(items.size() / (double) ITEMS_PER_PAGE);
            final int[] currentPage = {0};

            Panel itemListWrapper = new Panel(new LinearLayout(Direction.VERTICAL));
            Runnable updatePage = () -> {
                itemListWrapper.removeAllComponents();
                itemListWrapper.addComponent(UIHelper.itemListPanel(currentPage[0], ITEMS_PER_PAGE));
            };

            updatePage.run();
            mainPanel.addComponent(itemListWrapper);

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
