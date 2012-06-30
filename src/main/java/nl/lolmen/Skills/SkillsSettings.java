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
	private static boolean hasCitizens;
	private static boolean usePerSkillPerms;

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
		return falldmg.replace("$DMG", Integer.toString(dmg));
	}

	public static void setFalldmg(String falldmg) {
		SkillsSettings.falldmg = falldmg;
	}

	public static String getLvlup(String skill, int level) {
		return lvlup.replace("$SKILLNAME", skill).replace("$NEWLEVEL", Integer.toString(level));
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
	
	public static void setHasVault(boolean value){
		SkillsSettings.hasVault = value;
	}
	
	public static void setHasSpout(boolean value){
		SkillsSettings.hasSpout = value;
	}

	public static boolean isUsePerSkillPerms() {
		return usePerSkillPerms;
	}

	public static void setUsePerSkillPerms(boolean usePerSkillPerms) {
		SkillsSettings.usePerSkillPerms = usePerSkillPerms;
	}

	public static boolean hasCitizens() {
		return hasCitizens;
	}

	public static void setHasCitizens(boolean hasCitizens) {
		SkillsSettings.hasCitizens = hasCitizens;
	}

}
