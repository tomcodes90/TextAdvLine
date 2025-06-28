package characters;

import items.consumables.Consumable;
import items.equip.Armor;
import items.equip.Weapon;
import scenes.missions.MissionType;
import spells.SpellFactory;
import spells.SpellType;
import state.GameState;
import util.ItemRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * Class: EnemyFactory
 * <p>
 * Purpose:
 * Provides static factory methods to create enemy instances with
 * specific names, AI behaviors, equipment, spells, consumables, and loot.
 * Used during mission generation and story battles.
 */
public class EnemyFactory {
    /**
     * Utility method to create a basic enemy with empty loot and scaled rewards.
     *
     * @param name     The name of the enemy
     * @param mainStat The stat that scales more strongly when leveling
     * @param level    Desired level of the enemy
     * @param role     The AI role that controls enemy behavior in battle
     * @return A new Enemy instance with base setup
     */

    private static Enemy baseEnemy(String name, StatsType mainStat, int level, AIRole role) {
        return new Enemy(name, mainStat, level, new ArrayList<>(), 40 * level, 40 * level, role);

    }

    // All other methods follow the same pattern:
    // - Call baseEnemy() with custom name/stats/role
    // - Equip weapon and armor
    // - Optionally equip spells or consumables
    // - Add loot reward(s)

    /* ------------ presets ------------ */
    public static Enemy createBandit(int level) {
        Enemy e = baseEnemy("Limoniris", StatsType.STRENGTH, level, AIRole.BERSERKER);
        e.setWeapon((Weapon) ItemRegistry.getItemById("iron_sword"));
        e.setArmor((Armor) ItemRegistry.getItemById("leather_armor"));
        e.getLootReward().add(ItemRegistry.getItemById("healing_potion"));
        return e;
    }

    public static Enemy createMage(int level) {
        Enemy e = baseEnemy("Rigatonius", StatsType.INTELLIGENCE, level, AIRole.MAGE);
        e.setWeapon((Weapon) ItemRegistry.getItemById("iron_sword"));
        e.setArmor((Armor) ItemRegistry.getItemById("leather_armor"));
        e.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        e.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FROSTBITE);
        e.getLootReward().add(ItemRegistry.getItemById("sage_elixir"));
        return e;
    }

    public static Enemy createParmesaniGoon(int level) {
        Enemy e = baseEnemy("Parmo", StatsType.STRENGTH, level, AIRole.FIGHTER_BOOSTER);
        e.setWeapon((Weapon) ItemRegistry.getItemById("steel_sword"));
        e.setArmor((Armor) ItemRegistry.getItemById("chainmail_armor"));
        e.getLootReward().add(ItemRegistry.getItemById("power_elixir"));
        e.getConsumablesEquipped()[0] = (Consumable) ItemRegistry.getItemById("power_elixir");
        return e;
    }

    public static Enemy createRicottelliScout(int level) {
        Enemy e = baseEnemy("Ricotta", StatsType.INTELLIGENCE, level, AIRole.MAGE_BOOSTER);
        e.setArmor((Armor) ItemRegistry.getItemById("leather_armor"));
        e.setWeapon((Weapon) ItemRegistry.getItemById("steel_sword"));
        e.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FROSTBITE);
        e.getSpellsEquipped()[1] = SpellFactory.create(SpellType.VINEWHIP);
        e.getLootReward().add(ItemRegistry.getItemById("swift_draught"));
        e.getConsumablesEquipped()[0] = (Consumable) ItemRegistry.getItemById("sage_elixir");
        return e;
    }

    public static Enemy createRicottelliPriest(int level) {
        Enemy e = baseEnemy("Father Mozzarellus", StatsType.INTELLIGENCE, level, AIRole.MAGE_HEALER);
        e.setArmor((Armor) ItemRegistry.getItemById("leather_armor"));
        e.setWeapon((Weapon) ItemRegistry.getItemById("iron_sword"));
        e.getSpellsEquipped()[0] = SpellFactory.create(SpellType.VINEWHIP);
        e.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FLARE);
        e.getLootReward().add(ItemRegistry.getItemById("healing_potion"));
        e.getConsumablesEquipped()[0] = (Consumable) ItemRegistry.getItemById("greater_healing_potion");
        return e;
    }

    public static Enemy createBasilCultist(int level) {
        Enemy e = baseEnemy("Basilico", StatsType.INTELLIGENCE, level, AIRole.MAGE);
        e.setWeapon((Weapon) ItemRegistry.getItemById("iron_sword"));
        e.setArmor((Armor) ItemRegistry.getItemById("chainmail_armor"));
        e.getSpellsEquipped()[0] = SpellFactory.create(SpellType.FIREBALL);
        e.getSpellsEquipped()[1] = SpellFactory.create(SpellType.THORNSURGE);
        e.getLootReward().add(ItemRegistry.getItemById("fortitude_tonic"));
        return e;
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
        e.getSpellsEquipped()[0] = SpellFactory.create(SpellType.GLACIALSPIKE);
        e.getSpellsEquipped()[1] = SpellFactory.create(SpellType.FLARE);
        e.getSpellsEquipped()[2] = SpellFactory.create(SpellType.INFERNO);
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
        e.getSpellsEquipped()[0] = SpellFactory.create(SpellType.GARLICNOVA);
        e.getSpellsEquipped()[1] = SpellFactory.create(SpellType.THORNSURGE);
        e.getSpellsEquipped()[2] = SpellFactory.create(SpellType.MEATBALLMETEOR);
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
        e.getLootReward().add(ItemRegistry.getItemById("mind_elixir"));
        e.getConsumablesEquipped()[0] = (Consumable) ItemRegistry.getItemById("rage_brew");
        return e;
    }

    /* ------------ Randomized Selection (for grinding) ------------ */
    private static final Random RNG = new Random();

    /**
     * Returns a randomly chosen generic enemy for use in random encounters.
     */
    public static Enemy createRandomEnemy(int level) {
        MissionType mission = GameState.get().getMissionFlag();
        int tier = mission.ordinal(); // 0 = Tutorial, 1 = MISSION_1, ..., 8 = MISSION_8

        // Master enemy list by tier
        List<Function<Integer, Enemy>> allEnemies = List.of(
                EnemyFactory::createBandit,               // 0
                EnemyFactory::createMage,                 // 1
                EnemyFactory::createParmesaniGoon,        // 2
                EnemyFactory::createRicottelliScout,      // 3
                EnemyFactory::createRicottelliPriest,     // 4
                EnemyFactory::createCheeseGuardian,       // 5
                EnemyFactory::createPestoMonkBoss,        // 6
                EnemyFactory::createLinguiniKnight,       // 7
                EnemyFactory::createLinguiniChampion,     // 8
                EnemyFactory::createParmesaniDon          // 9
        );

        // Clamp tier to list size
        int maxIndex = Math.min(tier + 2, allEnemies.size()); // +2 gives some buffer
        List<Function<Integer, Enemy>> pool = allEnemies.subList(0, maxIndex);

        return pool.get(RNG.nextInt(pool.size())).apply(level);
    }
}
