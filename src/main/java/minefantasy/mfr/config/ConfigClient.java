package minefantasy.mfr.config;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConfigClient extends ConfigurationBaseMF {
	public static final String CATEGORY_GUI = "Gui/Hud Features";

	public static final String CATEGORY_AESTHETIC = "Aesthetics";
	public static final String GUI_STAMINA = "Stamina Bar Positioning";
	public static final String GUI_ARATING = "Armour Rating Positioning";
	public static final String GUI_ACOUNT = "Arrow Count Positioning";
	public static final String GUI_CAFUEL = "Clockwork Armour Fuel Positioning";
	public static final String CATEGORY_BLOCK = "Block Render Ids";
	public static final String CATEGORY_DEBUG = "Debug Info";
	public static boolean playBreath;
	public static boolean playHitsound;
	public static boolean customModel;
	public static int stam_xOrient;
	public static int stam_yOrient;
	public static int stam_xPos;
	public static int stam_yPos;
	public static int stam_direction;
	public static int AR_xOrient;
	public static int AR_yOrient;
	public static int AR_xPos;
	public static int AR_yPos;
	public static int AC_xOrient;
	public static int AC_yOrient;
	public static int AC_xPos;
	public static int AC_yPos;
	public static int CF_xOrient;
	public static int CF_yOrient;
	public static int CF_xPos;
	public static int CF_yPos;
	public static boolean displayOreDict;

	@Override
	protected void loadConfig() {
		playBreath = Boolean.parseBoolean(config.get(CATEGORY_AESTHETIC, "Make Breathe Sound", true,
				"[With Stamina System] Plays breath sounds when low on energy(sound may be annoying to some...)")
				.getString());
		playHitsound = Boolean.parseBoolean(config.get(CATEGORY_AESTHETIC, "Make Hit Sound", true,
				"Plays sounds when hitting entities with different items").getString());
		customModel = Boolean
				.parseBoolean(config
						.get(CATEGORY_AESTHETIC, "Custom Apparel Model", true,
								"Determines if some work apparel (like aprons and clothing) use special models")
						.getString());

		stam_xOrient = Integer.parseInt(config.get(GUI_STAMINA, "X Orient", 1,
				"The orientation for the X axis (-1 = left, 0 = middle, 1 = right). Determines what point in the axis to snap to")
				.getString());
		stam_yOrient = Integer.parseInt(config.get(GUI_STAMINA, "Y Orient", 1,
				"The orientation for the Y axis (-1 = top, 0 = middle, 1 = bottom). Determines what point in the axis to snap to")
				.getString());
		stam_xPos = Integer.parseInt(
				config.get(GUI_STAMINA, "X Position", -82, "The Offset value away from the orient (-)left, (+)right")
						.getString());
		stam_yPos = Integer.parseInt(
				config.get(GUI_STAMINA, "Y Position", -7, "The Offset value away from the orient (-)up, (+)down")
						.getString());
		stam_direction = Integer.parseInt(config.get(GUI_STAMINA, "Metre Direction", 1,
				"The direction the metre goes down: -1 = left to right, 0 = middle, 1 = right to left (May have subtle flaws in altered directions 1 and 0)")
				.getString());

		AR_xOrient = Integer.parseInt(config.get(GUI_ARATING, "X Orient", -1,
				"The orientation for the X axis (-1 = left, 0 = middle, 1 = right). Determines what point in the axis to snap to")
				.getString());
		AR_yOrient = Integer.parseInt(config.get(GUI_ARATING, "Y Orient", -1,
				"The orientation for the Y axis (-1 = top, 0 = middle, 1 = bottom). Determines what point in the axis to snap to")
				.getString());
		AR_xPos = Integer.parseInt(
				config.get(GUI_ARATING, "X Position", 4, "The Offset value away from the orient (-)left, (+)right")
						.getString());
		AR_yPos = Integer.parseInt(config
				.get(GUI_ARATING, "Y Position", 4, "The Offset value away from the orient (-)up, (+)down").getString());

		AC_xOrient = Integer.parseInt(config.get(GUI_ACOUNT, "X Orient", -1,
				"The orientation for the X axis (-1 = left, 0 = middle, 1 = right). Determines what point in the axis to snap to")
				.getString());
		AC_yOrient = Integer.parseInt(config.get(GUI_ACOUNT, "Y Orient", -1,
				"The orientation for the Y axis (-1 = top, 0 = middle, 1 = bottom). Determines what point in the axis to snap to")
				.getString());
		AC_xPos = Integer.parseInt(
				config.get(GUI_ACOUNT, "X Position", 4, "The Offset value away from the orient (-)left, (+)right")
						.getString());
		AC_yPos = Integer.parseInt(config
				.get(GUI_ACOUNT, "Y Position", 4, "The Offset value away from the orient (-)up, (+)down").getString());

		CF_xOrient = Integer.parseInt(config.get(GUI_CAFUEL, "X Orient", 1,
				"The orientation for the X axis (-1 = left, 0 = middle, 1 = right). Determines what point in the axis to snap to")
				.getString());
		CF_yOrient = Integer.parseInt(config.get(GUI_CAFUEL, "Y Orient", -1,
				"The orientation for the Y axis (-1 = top, 0 = middle, 1 = bottom). Determines what point in the axis to snap to")
				.getString());
		CF_xPos = Integer.parseInt(
				config.get(GUI_CAFUEL, "X Position", -164, "The Offset value away from the orient (-)left, (+)right")
						.getString());
		CF_yPos = Integer.parseInt(config
				.get(GUI_CAFUEL, "Y Position", -4, "The Offset value away from the orient (-)up, (+)down").getString());

		displayOreDict = Boolean.parseBoolean(config.get(CATEGORY_DEBUG, "Show Debug OreDict", false,
				"Displays a list of Ore Dictionary entries to tooltips").getString());
	}

}
