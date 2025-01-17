package minefantasy.mfr.config;

import minefantasy.mfr.entity.EntityCogwork;
import minefantasy.mfr.util.ArmourCalculator;
import minefantasy.mfr.util.TacticalManager;

public class ConfigArmour extends ConfigurationBaseMF {
	public static final String CATEGORY_GENERAL = "General";
	public static final String CATEGORY_BONUS = "Bonuses";
	public static final String CATEGORY_PENALTIES = "Penalties";
	public static final String CATEGORY_COGWORK = "Cogwork Features";
	public static boolean armorSounds;
	public static boolean resistArrow;
	public static boolean cogworkGrief;
	public static boolean cogworkJump;
	public static float cogworkFuelUnits;

	@Override
	protected void loadConfig() {
		armorSounds = Boolean.parseBoolean(config.get(CATEGORY_GENERAL, "Armor Sounds", true,
						"Should armor make sounds when over 50kg in weight")
				.getString());
		resistArrow = Boolean.parseBoolean(config.get(CATEGORY_BONUS, "Deflect Arrows", true,
				"With this option; arrows have a chance to deflect off armour. It compares the arrow damage and armour rating. Chain and plate have higher rates. Arrows bounced off can hurt enemies")
				.getString());
		TacticalManager.arrowDeflectChance = Float.parseFloat(config.get(CATEGORY_BONUS, "Deflect Arrows Modifier",
				1.0F,
				"This modifies the chance for arrows to deflect off armour: Increasing this increases the deflect chance, decreasing decreases the chance, 0 means it's impossible")
				.getString());

		TacticalManager.shouldSlow = Boolean.parseBoolean(config.get(CATEGORY_PENALTIES, "Armour Slow Movement", true,
				"With this: Heavier armours slow your movement down acrosss both walking and sprinting. Speed is affected by design and material")
				.getString());
		TacticalManager.minWeightSpeed = Float.parseFloat(config.get(CATEGORY_PENALTIES, "Min armour weigh speed", 10F,
				"The minimal speed percent capable from weight (10 = 10%), MF armours don't go lower than 75% anyway")
				.getString());
		ArmourCalculator.slowRate = Float.parseFloat(config.get(CATEGORY_PENALTIES, "Slowdown Rate", 1.0F,
				"A modifier for the rate armours slow down movement, increasing this slows you more, decreasing it slows less, just keep it above 0")
				.getString());

		ArmourCalculator.useConfigIndirectDmg = Boolean.parseBoolean(config.get("Misc", "Use config indirect damage",
				true,
				"Allows config for indirect projectiles, disable if you get problems when getting hit by modded projectiles")
				.getString());

		cogworkGrief = Boolean.parseBoolean(config.get(CATEGORY_COGWORK, "Cogwork Grief", true,
				"Should cogwork armour cause environmental damage when impact landing").getString());
		cogworkJump = Boolean.parseBoolean(config.get(CATEGORY_COGWORK, "Cogwork Jump", true,
				"Should cogwork armour be able to jump").getString());
		cogworkFuelUnits = Float.parseFloat(
				config.get(CATEGORY_COGWORK, "Cogwork Fuel Modifier", 1F, "Modify the amount of fuel added to cogworks")
						.getString());
		EntityCogwork.health_modifier = Float.parseFloat(config.get(CATEGORY_COGWORK, "Cogwork Durability Modifier",
				1.0F, "Modify the relative durability of cogwork armour").getString());
		EntityCogwork.rating_modifier = Float.parseFloat(config.get(CATEGORY_COGWORK, "Cogwork Armour Modifier", 1.0F,
				"Modify the relative armour rating of cogwork armour").getString());
		EntityCogwork.allowedBulk = Integer.parseInt(config.get(CATEGORY_COGWORK, "Worn Apparel Restrict", 0,
				"-1: can't wear armour with cogwork, 0=Can enter cogwork with light armour, 1=can enter with medium, 2=can enter in any armour")
				.getString());
	}
}
