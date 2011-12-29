package nl.lolmen.Skills;

public class SkillsSettings {

	private static boolean lightningOnLevelup;
	private static boolean broadcastOnLevelup;
	private static boolean debug;
	private static int moneyOnLevelup;
	private static String itemOnLevelup;
	private static String falldmg;
	private static String lvlup;
	private static boolean resetSkillsOnDeath;
	private static int levelsDownOnDeath;
	private static String levelsReset;
	private static boolean hasVault;
	private static boolean hasSpout;

	public static boolean isLightningOnLevelup() {
		return lightningOnLevelup;
	}

	public static void setLightningOnLevelup(boolean lightningOnLevelup) {
		SkillsSettings.lightningOnLevelup = lightningOnLevelup;
	}

	public static boolean isBroadcastOnLevelup() {
		return broadcastOnLevelup;
	}

	public static void setBroadcastOnLevelup(boolean broadcastOnLevelup) {
		SkillsSettings.broadcastOnLevelup = broadcastOnLevelup;
	}

	public static int getMoneyOnLevelup() {
		return moneyOnLevelup;
	}

	public static void setMoneyOnLevelup(int moneyOnLevelup) {
		SkillsSettings.moneyOnLevelup = moneyOnLevelup;
	}

	public static String getItemOnLevelup() {
		return itemOnLevelup;
	}

	public static void setItemOnLevelup(String itemOnLevelup) {
		SkillsSettings.itemOnLevelup = itemOnLevelup;
	}

	public static boolean isDebug() {
		return debug;
	}	
	
	public static void setDebug(boolean debug) {
		SkillsSettings.debug = debug;
	}

	public static String getFalldmg(int dmg) {
		String mes = falldmg.replace("DAMAGE", Integer.toString(dmg));
		return mes;
	}

	public static void setFalldmg(String falldmg) {
		SkillsSettings.falldmg = falldmg;
	}

	public static String getLvlup(String skill, int level) {
		return lvlup.replace("SKILL", skill).replace("LEVEL",
				Integer.toString(level));
	}

	public static void setLvlup(String lvlup) {
		SkillsSettings.lvlup = lvlup;
	}

	public static boolean isResetSkillsOnLevelup() {
		return resetSkillsOnDeath;
	}

	public static void setResetSkillsOnLevelup(boolean resetSkillsOnLevelup) {
		SkillsSettings.resetSkillsOnDeath = resetSkillsOnLevelup;
	}

	public static int getLevelsDownOnDeath() {
		return levelsDownOnDeath;
	}

	public static void setLevelsDownOnDeath(int levelsDownOnDeath) {
		SkillsSettings.levelsDownOnDeath = levelsDownOnDeath;
	}

	public static String getLevelsReset() {
		return levelsReset;
	}

	public static void setLevelsReset(String levelsReset) {
		SkillsSettings.levelsReset = levelsReset;
	}

	public static boolean HasVault() {
		return hasVault;
	}

	public static boolean HasSpout() {
		return hasSpout;
	}

}
