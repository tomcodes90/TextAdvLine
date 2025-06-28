// File: scenes/ui/EntityCard.java
package scenes.ui;

import characters.Enemy;
import characters.Entity;
import characters.Player;
import characters.StatsType;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import lombok.Getter;

/**
 * Utility class responsible for rendering the entity stat panels (cards)
 * for the player and the enemy during battle.
 * <p>
 * ⚠️ NOTE: Lanterna is not inherently thread-safe for UI operations.
 * All GUI updates must happen within the GUI thread (usually via GUI.runLater() or GUI.getGUIThread().invokeLater()).
 * If you update UI from outside the main thread, expect strange behavior or crashes.
 */
@Getter
public class EntityCard {

    // Static Panels that are reused to avoid frequent allocations
    @Getter
    private static final Panel pCard = new Panel();  // Panel for Player card
    @Getter
    private static final Panel eCard = new Panel();  // Panel for Enemy card

    /**
     * Clears and rebuilds both entity cards with updated stats.
     * This method is typically called after every action in battle.
     */
    public static void refreshCards(Player player, Enemy enemy) {
        pCard.removeAllComponents();
        eCard.removeAllComponents();

        // Rebuild cards for both entities
        pCard.addComponent(buildCard(player, "Player"));
        eCard.addComponent(buildCard(enemy, "Enemy"));
    }

    /**
     * Constructs a bordered GridLayout panel showing entity stats.
     *
     * @param e     The entity (player or enemy)
     * @param title Title of the card (used in the border)
     */
    private static Component buildCard(Entity e, String title) {
        Panel g = new Panel(new GridLayout(2));  // Two-column grid: label + value

        // Add rows: stat name → stat value or bar
        addRow(g, "Name", e.getName());
        addRow(g, "HP", hpBar(e));  // HP gets a special bar-style component
        addRow(g, "ATK", e.getStat(StatsType.STRENGTH));
        addRow(g, "INT", e.getStat(StatsType.INTELLIGENCE));
        addRow(g, "DEF", e.getStat(StatsType.DEFENSE));
        addRow(g, "SPD", e.getStat(StatsType.SPEED));

        return g.withBorder(Borders.singleLine(title));  // Add a box border with the title
    }

    // === Overloaded helpers for adding a row ===

    // Adds a text value row (e.g., "Name: Bob")
    private static void addRow(Panel p, String k, Object v) {
        p.addComponent(new Label(k + ":"));
        p.addComponent(new Label(String.valueOf(v)));
    }

    // Explicit version for string-based value
    private static void addRow(Panel panel, String label, String value) {
        panel.addComponent(new Label(label));
        panel.addComponent(new Label(value));
    }

    // Version for dynamic components like the HP bar
    private static void addRow(Panel panel, String label, Component value) {
        panel.addComponent(new Label(label));
        panel.addComponent(value);
    }

    /**
     * Builds an ASCII-style health bar component with color feedback.
     * HP bar is split into 10 segments (# = filled, - = empty).
     * Color changes depending on % of remaining HP.
     */
    private static Component hpBar(Entity e) {
        int hp = e.getStat(StatsType.HP);
        int max = e.getStat(StatsType.MAX_HP);
        int filled = (int) Math.round((hp / (double) max) * 10);

        /* ----- text components ----- */
        String hpText = hp + "/" + max + " ";
        StringBuilder barSb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            barSb.append(i < filled ? "#" : "-");  // Build bar segments
        }
        barSb.append(']');

        /* ----- color logic ----- */
        double pct = hp / (double) max;
        TextColor barColour = (pct >= 0.6)
                ? TextColor.ANSI.GREEN
                : (pct >= 0.3)
                ? TextColor.ANSI.YELLOW_BRIGHT
                : TextColor.ANSI.RED_BRIGHT;

        /* ----- assemble panel ----- */
        Panel row = new Panel(new LinearLayout(Direction.HORIZONTAL));

        // 1️⃣ Show current HP in plain text
        row.addComponent(new Label(hpText));

        // 2️⃣ Show colored bar for visual feedback
        Label barLbl = new Label(barSb.toString());
        barLbl.setForegroundColor(barColour);
        row.addComponent(barLbl);

        return row;
    }
}
