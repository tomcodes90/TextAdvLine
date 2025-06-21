package items;

import characters.Entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import spells.Spell;
import spells.SpellFactory;
import spells.SpellType;

public class Book extends Item {
    private final Spell spell;
    private final SpellType spellType; // Useful to store what was used to create the spell

    @JsonCreator
    public Book(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("spellType") SpellType spellType
    ) {
        super(id, name, description);
        this.spellType = spellType;
        this.spell = SpellFactory.create(spellType);
    }

    public void use(Entity entity, int slot) {
        // You can improve this logic later to assign to next available slot
        entity.getSpellsEquipped()[slot] = spell;
    }

    public Spell getSpell() {
        return spell;
    }

    public SpellType getSpellType() {
        return spellType;
    }
}
