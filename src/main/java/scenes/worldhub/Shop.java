package scenes.worldhub;

import characters.Player;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import items.Item;
import items.ItemRegistry;
import items.MissionType;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import state.GameState;

import java.util.Comparator;
import java.util.List;

public class Shop implements Scene {

    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    public Shop(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("🛒 Item Shop");

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("Welcome to the Item Shop!"));
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("🛍 Buy Items", this::openBuyMenu));
        panel.addComponent(new Button("💰 Sell Items", this::openSellMenu));
        panel.addComponent(new Button("⬅ Back", () -> {
            window.close();
            SceneManager.get().switchTo(new WorldHub(gui, player));
        }));

        window.setComponent(panel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    private void openBuyMenu() {
        BasicWindow buyWindow = new BasicWindow("Buy Items");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        Label goldLabel = new Label("Gold: " + player.getGold());
        panel.addComponent(goldLabel);
        panel.addComponent(new EmptySpace());

        MissionType currentMission = GameState.get().getMissionFlag();

        ItemRegistry.getAllItems().stream()
                .filter(item -> item.getPrice() > 0)
                .filter(item -> {
                    switch (item.getId()) {
                        case "iron_sword", "leather_armor" -> {
                            return true;
                        }
                        case "steel_sword", "chainmail_armor" -> {
                            return currentMission != null &&
                                    currentMission.ordinal() >= MissionType.MISSION_3.ordinal();
                        }
                        case "crimson_blade", "plate_armor" -> {
                            return currentMission != null &&
                                    currentMission.ordinal() >= MissionType.MISSION_5.ordinal();
                        }
                        case "dragonfang_sword", "dragon_scale_armor" -> {
                            return currentMission != null &&
                                    currentMission.ordinal() >= MissionType.MISSION_7.ordinal();
                        }
                        default -> {
                            System.out.println("❌ Unknown item id: " + item.getId());
                            return false;
                        }
                    }
                })
                .sorted(Comparator.comparing(Item::getName))
                .forEach(item -> {
                    String label = String.format("%s - %dg", item.getName(), item.getPrice());
                    panel.addComponent(new Button(label, () -> {
                        if (player.getGold() >= item.getPrice()) {
                            player.decreaseGold(item.getPrice());
                            player.addItemToInventory(item);
                            MessageDialog.showMessageDialog(gui, "Purchase", "You bought: " + item.getName());
                            goldLabel.setText("Gold: " + player.getGold());
                        } else {
                            MessageDialog.showMessageDialog(gui, "Not enough gold", "You can't afford this item.");
                        }
                    }));
                });

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("⬅ Back", buyWindow::close));
        buyWindow.setComponent(panel);
        buyWindow.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(buyWindow);
    }


    private void openSellMenu() {
        BasicWindow sellWindow = new BasicWindow("Sell Items");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        Label goldLabel = new Label("Gold: " + player.getGold());
        panel.addComponent(goldLabel);
        panel.addComponent(new EmptySpace());

        if (player.getInventory().isEmpty()) {
            panel.addComponent(new Label("You have nothing to sell."));
        } else {
            player.getInventory().keySet().stream()
                    .filter(item -> item.getPrice() > 0)
                    .sorted(Comparator.comparing(Item::getName))
                    .forEach(item -> {
                        int quantity = player.getInventory().get(item);
                        String label = String.format("%s x%d - %dg", item.getName(), quantity, item.getPrice());
                        panel.addComponent(new Button(label, () -> {
                            player.removeItemFromInventory(item);
                            player.collectGold(item.getPrice());
                            MessageDialog.showMessageDialog(gui, "Sold", "You sold: " + item.getName());
                            goldLabel.setText("Gold: " + player.getGold());
                            sellWindow.close();
                            openSellMenu(); // Refresh UI
                        }));
                    });
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("<- Back", sellWindow::close));
        sellWindow.setComponent(panel);
        sellWindow.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(sellWindow);
    }

    @Override
    public void handleInput() {}

    @Override
    public void exit() {
        if (window != null) {
            gui.removeWindow(window);
        }
    }
}
