package scenes.ui;

import characters.Enemy;
import characters.Entity;
import characters.Player;
import characters.StatsType;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import lombok.Getter;

@Getter
public class EntityCard {
    @Getter
    private static final Panel pCard = new Panel();
    @Getter
    private static final Panel eCard = new Panel();

    public static void refreshCards(Player player, Enemy enemy) {
        pCard.removeAllComponents();
        eCard.removeAllComponents();
        pCard.addComponent(buildCard(player, "Player"));
        eCard.addComponent(buildCard(enemy, "Enemy"));
    }

    private static Component buildCard(Entity e, String title) {
        Panel g = new Panel(new GridLayout(2));
        addRow(g, "Name", e.getName());
        addRow(g, "HP", hpBar(e));
        addRow(g, "ATK", e.getStat(StatsType.STRENGTH));
        addRow(g, "INT", e.getStat(StatsType.INTELLIGENCE));
        addRow(g, "DEF", e.getStat(StatsType.DEFENSE));
        addRow(g, "SPD", e.getStat(StatsType.SPEED));
        return g.withBorder(Borders.singleLine(title));
    }

    private static void addRow(Panel p, String k, Object v) {
        p.addComponent(new Label(k + ":"));
        p.addComponent(new Label(String.valueOf(v)));
    }

    private static void addRow(Panel panel, String label, String value) {
        panel.addComponent(new Label(label));
        panel.addComponent(new Label(value));
    }

    private static void addRow(Panel panel, String label, Component value) {
        panel.addComponent(new Label(label));
        panel.addComponent(value);
    }

    private static Component hpBar(Entity e) {
        int hp = e.getStat(StatsType.HP);
        int max = e.getStat(StatsType.MAX_HP);
        int filled = (int) Math.round((hp / (double) max) * 10);

        /* ---------------- text parts ---------------- */
        String hpText = hp + "/" + max + " ";
        StringBuilder barSb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) barSb.append(i < filled ? "#" : "-");
        barSb.append(']');

        /* ---------------- choose bar colour ---------------- */
        double pct = hp / (double) max;
        TextColor barColour = (pct >= 0.6) ? TextColor.ANSI.GREEN
                : (pct >= 0.3) ? TextColor.ANSI.CYAN
                : TextColor.ANSI.RED;

        /* ---------------- assemble component ---------------- */
        Panel row = new Panel(new LinearLayout(Direction.HORIZONTAL));

        // 1️⃣ hp/max in default colour
        row.addComponent(new Label(hpText));

        // 2️⃣ coloured bar
        Label barLbl = new Label(barSb.toString());
        barLbl.setForegroundColor(barColour);
        row.addComponent(barLbl);

        return row;
    }
}
