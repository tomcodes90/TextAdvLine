package characters;

import items.*;
import items.equip.Armor;
import items.equip.Weapon;
import spells.SpellFactory;
import spells.SpellType;
import util.ItemRegistry;

import java.util.ArrayList;
import java.util.Random;

public class EnemyFactory {

    /* ------------ presets ------------ */
    public static Enemy createBandit(int level) {
        Enemy bandit = new Enemy(
                "Limoniris",
                StatsType.INTELLIGENCE,
                0,
                new ArrayList<>(),
                40,
                40,
                AIRole.BERSERKER
        );

        bandit.getLootReward().add(ItemRegistry.getByName("Healing Potion"));
        bandit.getLootReward().add(ItemRegistry.getByName("Book of Energyblast"));
        bandit.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        bandit.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));

        return bandit;
    }

    public static Enemy createMage(int level) {
        Enemy enemy = new Enemy(
                "Rigatonius",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createParmesaniGoon(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createRicottelliScout(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createRicottelliPriest(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createBasilCultist(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createParmesaniCaptain(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createPestoMonkBoss(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createCheeseGuardian(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createLinguiniGoon(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createRicottelliChef(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createLinguiniKnight(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createLinguiniChampion(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createBoarHunter(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createLinguiniMatriarch(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createRicottelliPatriarch(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createParmesaniDon(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }

    public static Enemy createPigGuardian(int level) {
        Enemy enemy = new Enemy(
                "Padanis",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.MAGE
        );

        Item item = ItemRegistry.getByName("Healing Potion");
        enemy.getLootReward().add(item);
        enemy.setWeapon((Weapon) ItemRegistry.getByName("Iron Sword"));
        enemy.setArmor((Armor) ItemRegistry.getByName("Leather Armor"));
        enemy.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        enemy.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);

        return enemy;
    }



    /* ------------ random helper ------------ */

    private static final Random RNG = new Random();

    public static Enemy createRandomEnemy(int level) {
        return switch (RNG.nextInt(2)) {
            case 1 -> createBandit(level);
            default -> createMage(level);
        };
    }
}
