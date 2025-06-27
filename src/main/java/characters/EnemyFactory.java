package characters;

import items.*;
import items.consumables.Consumable;
import items.equip.Armor;
import items.equip.Weapon;
import spells.SpellFactory;
import spells.SpellType;
import util.ItemRegistry;

import java.util.ArrayList;
import java.util.Random;

public class EnemyFactory {
    // === Utility Method ===
    private static Enemy baseEnemy(String name, StatsType mainStat, int level, AIRole role) {
        Enemy enemy = new Enemy(name, mainStat, level, new ArrayList<>(), 40, 40, role);
        return enemy;
    }

    /* ------------ presets ------------ */
    public static Enemy createBandit(int level) {
        Enemy bandit = new Enemy(
                "Limoniris",
                StatsType.STRENGTH,
                level,
                new ArrayList<>(),
                40,
                40,
                AIRole.BERSERKER
        );
        bandit.setWeapon((Weapon) ItemRegistry.getItemById("iron_sword"));
        bandit.setArmor((Armor) ItemRegistry.getItemById("leather_armor"));
        bandit.getLootReward().add(ItemRegistry.getItemById("healing_potion"));
        return bandit;
    }

    public static Enemy createMage(int level) {
        Enemy mage = new Enemy(
                "Rigatonius",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                45,
                45,
                AIRole.MAGE
        );
        mage.setWeapon((Weapon) ItemRegistry.getItemById("iron_sword"));
        mage.setArmor((Armor) ItemRegistry.getItemById("leather_armor"));
        mage.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        mage.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);
        mage.getLootReward().add(ItemRegistry.getItemById("sage_elixir"));
        return mage;
    }

    public static Enemy createParmesaniGoon(int level) {
        Enemy goon = new Enemy(
                "Parmo",
                StatsType.STRENGTH,
                level,
                new ArrayList<>(),
                50,
                50,
                AIRole.FIGHTER_BOOSTER
        );
        goon.setWeapon((Weapon) ItemRegistry.getItemById("steel_sword"));
        goon.setArmor((Armor) ItemRegistry.getItemById("chainmail_armor"));
        goon.getLootReward().add(ItemRegistry.getItemById("power_elixir"));
        goon.getConsumablesEquipped()[0] = (Consumable) ItemRegistry.getItemById("power_elixir");
        return goon;
    }

    public static Enemy createRicottelliScout(int level) {
        Enemy scout = new Enemy(
                "Ricotta",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                35,
                35,
                AIRole.MAGE_BOOSTER
        );
        scout.setArmor((Armor) ItemRegistry.getItemById("leather_armor"));
        scout.setWeapon((Weapon) ItemRegistry.getItemById("steel_sword"));
        scout.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FROSTBITE);
        scout.getSpellsEquipped()[1] = SpellFactory.create(SpellType.VINEWHIP);
        scout.getLootReward().add(ItemRegistry.getItemById("swift_draught"));
        scout.getConsumablesEquipped()[0] = (Consumable) ItemRegistry.getItemById("sage_elixir");
        return scout;
    }

    public static Enemy createRicottelliPriest(int level) {
        Enemy priest = new Enemy(
                "Father Mozzarellus",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                38,
                38,
                AIRole.MAGE_HEALER
        );
        priest.setArmor((Armor) ItemRegistry.getItemById("leather_armor"));
        priest.setWeapon((Weapon) ItemRegistry.getItemById("iron_sword"));
        priest.getSpellsEquipped()[0] = SpellFactory.create(SpellType.VINEWHIP);
        priest.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FLARE);
        priest.getLootReward().add(ItemRegistry.getItemById("healing_potion"));
        priest.getConsumablesEquipped()[0] = (Consumable) ItemRegistry.getItemById("greater_healing_potion");
        return priest;
    }


    public static Enemy createBasilCultist(int level) {
        Enemy cultist = new Enemy(
                "Basilico",
                StatsType.INTELLIGENCE,
                level,
                new ArrayList<>(),
                42,
                42,
                AIRole.MAGE
        );
        cultist.setWeapon((Weapon) ItemRegistry.getItemById("iron_sword"));
        cultist.setArmor((Armor) ItemRegistry.getItemById("chainmail_armor"));
        cultist.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        cultist.getSpellsEquipped()[1] = SpellFactory.create(SpellType.THORNSURGE);
        cultist.getLootReward().add(ItemRegistry.getItemById("fortitude_tonic"));
        return cultist;
    }


    public static Enemy createParmesaniCaptain(int level) {
        Enemy e = baseEnemy("Parmesani Captain", StatsType.STRENGTH, level + 1, AIRole.FIGHTER_BOOSTER);
        e.setWeapon((Weapon) ItemRegistry.getItemById("steel_sword"));
        e.setArmor((Armor) ItemRegistry.getItemById("chainmail_armor"));
        e.getConsumablesEquipped()[0] = (Consumable) ItemRegistry.getItemById("swift_draught");
        e.getLootReward().add(ItemRegistry.getItemById("fortitude_tonic"));
        return e;
    }

    public static Enemy createPestoMonkBoss(int level) {

        Enemy e = baseEnemy("Pesto Monk", StatsType.INTELLIGENCE, level + 2, AIRole.MAGE_HEALER);
        e.setWeapon((Weapon) ItemRegistry.getItemById("steel_sword"));
        e.setArmor((Armor) ItemRegistry.getItemById("chainmail_armor"));
        e.getSpellsEquipped()[1] = SpellFactory.create(SpellType.INFERNO);
        e.getSpellsEquipped()[2] = SpellFactory.create(SpellType.FLARE);
        e.getSpellsEquipped()[1] = SpellFactory.create(SpellType.GLACIALSPIKE);
        e.getConsumablesEquipped()[0] = (Consumable) ItemRegistry.getItemById("greater_healing_potion");
        return e;
    }

    public static Enemy createCheeseGuardian(int level) {
        Enemy e = baseEnemy("Cheese Guardian", StatsType.DEFENSE, level + 2, AIRole.FIGHTER_HEALER);
        e.setWeapon((Weapon) ItemRegistry.getItemById("crimson_blade"));
        e.setArmor((Armor) ItemRegistry.getItemById("plate_armor"));
        e.getLootReward().add(ItemRegistry.getItemById("elixir_of_life"));
        e.getConsumablesEquipped()[0] = (Consumable) ItemRegistry.getItemById("rage_brew");
        return e;
    }

    public static Enemy createLinguiniGoon(int level) {
        Enemy e = baseEnemy("Linguini Goon", StatsType.STRENGTH, level, AIRole.BERSERKER);
        e.setWeapon((Weapon) ItemRegistry.getItemById("steel_Sword"));
        e.setArmor((Armor) ItemRegistry.getItemById("chainmail_armor"));
        e.getLootReward().add(ItemRegistry.getItemById("greater_healing_potion"));
        return e;
    }

    public static Enemy createRicottelliChef(int level) {
        Enemy e = baseEnemy("Ricottelli Chef", StatsType.INTELLIGENCE, level + 1, AIRole.MAGE);
        e.setWeapon((Weapon) ItemRegistry.getItemById("steel_Sword"));
        e.setArmor((Armor) ItemRegistry.getItemById("plate_armor"));
        e.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        e.getSpellsEquipped()[1] = SpellFactory.create(SpellType.INFERNO);
        e.getSpellsEquipped()[2] = SpellFactory.create(SpellType.FROSTBITE);
        e.getLootReward().add(ItemRegistry.getItemById("mind_elixir"));
        return e;
    }

    public static Enemy createLinguiniKnight(int level) {
        Enemy e = baseEnemy("Linguini Knight", StatsType.STRENGTH, level + 1, AIRole.FIGHTER_BOOSTER);
        e.setWeapon((Weapon) ItemRegistry.getItemById("crimson_blade"));
        e.setArmor((Armor) ItemRegistry.getItemById("plate_armor"));
        e.getConsumablesEquipped()[0] = (Consumable) ItemRegistry.getItemById("rage_brew");
        return e;
    }

    public static Enemy createLinguiniChampion(int level) {
        Enemy e = baseEnemy("Linguini Champion", StatsType.DEFENSE, level + 2, AIRole.BERSERKER);
        e.setWeapon((Weapon) ItemRegistry.getItemById("steel_sword"));
        e.setArmor((Armor) ItemRegistry.getItemById("plate_armor"));
        e.getLootReward().add(ItemRegistry.getItemById("plate_armor"));
        return e;
    }

    public static Enemy createBoarHunter(int level) {
        Enemy e = baseEnemy("Boar Hunter", StatsType.STRENGTH, level, AIRole.BERSERKER);
        e.setArmor((Armor) ItemRegistry.getItemById("leather_armor"));
        e.setWeapon((Weapon) ItemRegistry.getItemById("steel_sword"));
        return e;
    }

    public static Enemy createLinguiniMatriarch(int level) {
        Enemy e = baseEnemy("Linguini Matriarch", StatsType.INTELLIGENCE, level + 2, AIRole.MAGE_BOOSTER);
        e.getSpellsEquipped()[1] = SpellFactory.create(SpellType.GARLICNOVA);
        e.getSpellsEquipped()[2] = SpellFactory.create(SpellType.THORNSURGE);
        e.getSpellsEquipped()[3] = SpellFactory.create(SpellType.MEATBALLMETEOR);
        e.setArmor((Armor) ItemRegistry.getItemById("dragon_scale_armor"));
        e.setWeapon((Weapon) ItemRegistry.getItemById("dragonfang_sword"));
        e.getConsumablesEquipped()[0] = (Consumable) ItemRegistry.getItemById("mind_elixir");
        e.getLootReward().add(ItemRegistry.getItemById("rage_brew"));

        return e;
    }

    public static Enemy createRicottelliPatriarch(int level) {
        Enemy e = baseEnemy("Ricottelli Patriarch", StatsType.INTELLIGENCE, level + 2, AIRole.MAGE);
        e.getSpellsEquipped()[0] = SpellFactory.create(SpellType.GLACIALSPIKE);
        e.getSpellsEquipped()[1] = SpellFactory.create(SpellType.INFERNO);
        e.getSpellsEquipped()[2] = SpellFactory.create(SpellType.FLARE);
        e.setArmor((Armor) ItemRegistry.getItemById("dragon_scale_armor"));
        e.setWeapon((Weapon) ItemRegistry.getItemById("dragonfang_sword"));
        e.getLootReward().add(ItemRegistry.getItemById("dragonfang_sword"));
        return e;
    }

    public static Enemy createParmesaniDon(int level) {
        Enemy e = baseEnemy("Parmesani Don", StatsType.STRENGTH, level + 3, AIRole.BERSERKER);
        e.setArmor((Armor) ItemRegistry.getItemById("dragon_scale_armor"));
        e.setWeapon((Weapon) ItemRegistry.getItemById("dragonfang_sword"));
        e.getLootReward().add(ItemRegistry.getItemById("mind_elixir"));
        return e;
    }

    public static Enemy createPigGuardian(int level) {
        Enemy e = baseEnemy("Prosciutthulhu", StatsType.STRENGTH, level + 2, AIRole.FIGHTER_BOOSTER);
        e.setWeapon((Weapon) ItemRegistry.getItemById("crimson_blade"));
        e.setArmor((Armor) ItemRegistry.getItemById("dragon_scale_armor"));
        return e;
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
