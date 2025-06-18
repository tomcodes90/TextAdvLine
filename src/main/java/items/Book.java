package items;

import characters.Entity;
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

    public void use(Entity entity) {
        entity.getSpells().put(spellType, spell);
    }

    public Spell getSpell() {
        return spell;
    }

    public SpellType getSpellType() {
        return spellType;
    }
}
