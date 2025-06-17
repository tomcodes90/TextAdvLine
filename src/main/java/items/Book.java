package items;

import characters.Player;
import spells.Spell;
import spells.SpellType;

public class Book extends Item {
private final Spell spell;
private final SpellType spellType;

    Book(String id, String name, String description, Spell spell, SpellType spellType) {
        super(id, name, description);
        this.spell = spell;
        this.spellType = spellType;
    }

    public void use(Player player) {
        player.addSpell(spellType, spell);
    }

    public Spell getSpell() {
        return spell;
    }

    public SpellType getSpellType() {
        return spellType;
    }
}
