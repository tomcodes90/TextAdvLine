package items;

import characters.Entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import spells.Spell;
import spells.SpellFactory;
import spells.SpellType;

/**
 * Item: Book
 * <p>
 * A spell book that teaches the user a specific spell when used.
 * This class is used to equip spells on an entity through inventory interaction.
 */
public class Book extends Item {
    @Getter
    private final Spell spell;
    private final SpellType spellType;

    /**
     * Jackson constructor for deserialization.
     *
     * @param id          Unique item ID
     * @param name        Display name
     * @param description Tooltip or short explanation
     * @param spellType   The type of spell this book grants
     */
    @JsonCreator
    public Book(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("spellType") SpellType spellType
    ) {
        super(id, name, description, 0);
        this.spellType = spellType;
        this.spell = SpellFactory.create(spellType);
    }

    /**
     * Teaches the spell to the target entity, equipping it into a given slot.
     *
     * @param entity The entity learning the spell
     * @param slot   The slot index (0–2) to equip the spell
     */
    public void use(Entity entity, int slot) {
        entity.equipSpell(slot, spellType);
    }

}
