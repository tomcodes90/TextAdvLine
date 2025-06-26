package scenes.worldhub;

import characters.Player;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import items.Item;
import util.ItemRegistry;
import scenes.missions.MissionType;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import state.GameState;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
        window = new BasicWindow("Item Shop");

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("Welcome to the Item Shop!"));
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("Buy Items", this::openBuyMenu));
        panel.addComponent(new Button("Sell Items", this::openSellMenu));
        panel.addComponent(new Button("Back", () -> {
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

        Label nameLabel = new Label("Item: ");
        Label descriptionLabel = new Label("Description: ");
        Label effectLabel = new Label("Effect: ");

        panel.addComponent(nameLabel);
        panel.addComponent(descriptionLabel);
        panel.addComponent(effectLabel);

        panel.addComponent(new EmptySpace());

        ItemRegistry.getAllItems().stream()
                .filter(item -> item.getPrice() > 0)
                .filter(item -> {
                    MissionType currentMission = GameState.get().getMissionFlag();
                    String id = item.getId();

                    return switch (id) {
                        // Always available
                        case "healing_potion", "sage_elixir", "power_elixir" -> true;

                        // Unlocked at MISSION_2
                        case "greater_healing_potion", "fortitude_tonic", "swift_draught" ->
                                currentMission != null && currentMission.ordinal() >= MissionType.MISSION_2.ordinal();

                        // Unlocked at MISSION_4
                        case "elixir_of_life", "mind_elixir", "rage_brew" ->
                                currentMission != null && currentMission.ordinal() >= MissionType.MISSION_4.ordinal();

                        // Equipment logic (from before)
                        case "iron_sword", "leather_armor" -> true;
                        case "steel_sword", "chainmail_armor" ->
                                currentMission != null && currentMission.ordinal() >= MissionType.MISSION_3.ordinal();
                        case "crimson_blade", "plate_armor" ->
                                currentMission != null && currentMission.ordinal() >= MissionType.MISSION_5.ordinal();
                        case "dragonfang_sword", "dragon_scale_armor" ->
                                currentMission != null && currentMission.ordinal() >= MissionType.MISSION_7.ordinal();

                        default -> {
                            System.out.println("❌ Unknown item id: " + id);
                            yield false;
                        }
                    };
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
                    }) {
                        {
                            // Hover listener
                            addListener((Button.Listener) b -> {
                                nameLabel.setText("Item: " + item.getName());
                                descriptionLabel.setText("Description: " + item.getDescription());
                                effectLabel.setText("Effect: " + getEffectText(item));
                            });
                        }
                    });
                });

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Back", buyWindow::close));
        buyWindow.setComponent(panel);
        buyWindow.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(buyWindow);
    }

    private String getEffectText(Item item) {
        if (item instanceof items.consumables.Potion p) {
            return "+" + p.getPointsToApply() + " HP";
        }
        if (item instanceof items.consumables.StatEnhancer s) {
            return "Boosts " + s.getStatToBoost() + " by " + s.getPointsToApply() + " for " + s.getLength() + " turns";
        }
        return "-"; // Default fallback
    }

    private void openSellMenu() {
        BasicWindow sellWindow = new BasicWindow("Sell Items");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        /* ── Player gold at top ───────────────────────── */
        Label goldLabel = new Label("Gold: " + player.getGold());
        panel.addComponent(goldLabel);
        panel.addComponent(new EmptySpace());

        /* ── Dynamic info area (name + price) ─────────── */
        Label nameLabel = new Label("Item: ");
        Label priceLabel = new Label("Sale price: ");
        panel.addComponent(nameLabel);
        panel.addComponent(priceLabel);
        panel.addComponent(new EmptySpace());

        /* ── Item list ─────────────────────────────────── */
        if (player.getInventory().isEmpty()) {
            panel.addComponent(new Label("You have nothing to sell."));
        } else {
            player.getInventory().keySet().stream()
                    .filter(item -> item.getPrice() > 0)
                    .sorted(Comparator.comparing(Item::getName))
                    .forEach(item -> {
                        int quantity = player.getInventory().get(item);
                        String btnText = String.format("%s x%d - %dg", item.getName(), quantity, item.getPrice());

                        Button btn = new Button(btnText, () -> {
                            // Confirm sale
                            MessageDialogButton res = MessageDialog.showMessageDialog(
                                    gui, "Confirm",
                                    "Sell " + item.getName() + " for " + item.getPrice() + "g?",
                                    MessageDialogButton.Yes, MessageDialogButton.No
                            );
                            if (res == MessageDialogButton.Yes) {
                                player.removeItemFromInventory(item);
                                player.collectGold(item.getPrice());
                                goldLabel.setText("Gold: " + player.getGold());
                                sellWindow.close();
                                openSellMenu(); // refresh list
                            }
                        });

                        // Hover/select listener updates info labels
                        btn.addListener(b -> {
                            nameLabel.setText("Item: " + item.getName());
                            priceLabel.setText("Sale price: " + item.getPrice() + "g");
                        });

                        panel.addComponent(btn);
                    });
        }

        /* ── Footer ───────────────────────────────────── */
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Back", sellWindow::close));

        sellWindow.setComponent(panel);
        sellWindow.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(sellWindow);
    }


    @Override
    public void handleInput() {
    }

    @Override
    public void exit() {
        if (window != null) {
            gui.removeWindow(window);
        }
    }
}