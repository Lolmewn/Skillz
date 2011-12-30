package nl.lolmen.Skills;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

import nl.lolmen.Skills.skills.Acrobatics;
import nl.lolmen.Skills.skills.Archery;
import nl.lolmen.Skills.skills.Axes;
import nl.lolmen.Skills.skills.Digging;
import nl.lolmen.Skills.skills.Mining;
import nl.lolmen.Skills.skills.Swimming;
import nl.lolmen.Skills.skills.Swords;
import nl.lolmen.Skills.skills.Unarmed;
import nl.lolmen.Skills.skills.Woodcutting;


public class SkillManager {
	private File settings = new File("plugins" + File.separator
			+ "Skillz" + File.separator + "skills.yml");
	private Logger log = Logger.getLogger("Minecraft");
	private String logPrefix = "[LittleBigPlugin] ";
	public static HashMap<String, SkillBase> skills = new HashMap<String, SkillBase>();
	public static Archery ar = new Archery();
	public static Acrobatics ac = new Acrobatics();

	public SkillManager() {

	}

	public void loadSkillsSettings() {
		if (!settings.exists()) {
			createSkillsSettings();
		}
		YamlConfiguration c = YamlConfiguration.loadConfiguration(settings);
		try {
			if(!c.contains("moneyOnLevelup")){
				createSkillsSettings();
			}
			SkillsSettings.setBroadcastOnLevelup(c.getBoolean("broadcastOnLevelup", true));
			SkillsSettings.setDebug(c.getBoolean("debug", false));
			SkillsSettings.setItemOnLevelup(c.getString("itemOnLevelup", "89,1"));
			SkillsSettings.setLightningOnLevelup(c.getBoolean("lightningOnLevelup", false));
			SkillsSettings.setMoneyOnLevelup(c.getInt("moneyOnLevelup", 20));
			SkillsSettings.setResetSkillsOnLevelup(c.getBoolean("resetAllSkillsOnDeath", false));
			SkillsSettings.setLevelsDownOnDeath(c.getInt("loseLevelsOnDeath", 0));
			SkillsSettings.setLevelsReset(c.getString("lostLevels", "You lost levels because you died."));
			SkillsSettings.setFalldmg(c.getString("fallDamageMessage"));
			SkillsSettings.setLvlup(c.getString("levelupMessage"));
			// Now the actual skills
			for (String key : c.getConfigurationSection("skills")
					.getKeys(false)) {
				String keys = key.toLowerCase();
				boolean enabled = c.getBoolean("skills." + key + ".enabled", true);
				int multiplier = c.getInt("skills." + key + ".XP-gain-multiplier", 1);
				String item = null;
				int money = -1;
				if (c.contains("skills" + key + ".itemOnLevelUp")) {
					item = c.getString("skills" + key + ".itemOnLevelUp");
				}
				if (c.contains("skills" + key + ".moneyOnLevelUp")) {
					money = c.getInt("skills" + key + ".moneyOnLevelUp");
				}
				if (key.equalsIgnoreCase("archery")) {
					Archery a = new Archery();
					a.setBlocks_till_XP(c.getInt("skills." + key + ".blocks-till-1XP-add", 10));
					if (item == null) {
						a.setItemOnLevelup(SkillsSettings.getItemOnLevelup());
					} else {
						a.setItemOnLevelup(item);
					}
					if (money == -1) {
						a.setMoneyOnLevelup(SkillsSettings.getMoneyOnLevelup());
					} else {
						a.setMoneyOnLevelup(money);
					}
					a.setSkillName(key);
					a.setEnabled(enabled);
					a.setMultiplier(multiplier);
					skills.put(keys, a);
					ar = a;
				}
				if(keys.equalsIgnoreCase("acrobatics")){
					Acrobatics a = new Acrobatics();
					if (item == null) {
						a.setItemOnLevelup(SkillsSettings.getItemOnLevelup());
					} else {
						a.setItemOnLevelup(item);
					}
					if (money == -1) {
						a.setMoneyOnLevelup(SkillsSettings.getMoneyOnLevelup());
					} else {
						a.setMoneyOnLevelup(money);
					}
					a.setSkillName(keys);
					a.setEnabled(enabled);
					a.setMultiplier(multiplier);
					a.setLevelsTillLessDMG(c.getInt("skills." + key + ".levels-per-reducted-damage", 5));
					ac = a;
					skills.put(keys, a);
				}
				if(key.equalsIgnoreCase("swimming")){
					Swimming a = new Swimming();
					if (item == null) {
						a.setItemOnLevelup(SkillsSettings.getItemOnLevelup());
					} else {
						a.setItemOnLevelup(item);
					}
					if (money == -1) {
						a.setMoneyOnLevelup(SkillsSettings.getMoneyOnLevelup());
					} else {
						a.setMoneyOnLevelup(money);
					}
					a.setSkillName(key);
					a.setEnabled(enabled);
					a.setMultiplier(multiplier);
					skills.put(keys, a);
				}
				if (key.equalsIgnoreCase("mining")) {
					Mining a = new Mining();
					if (item == null) {
						a.setItemOnLevelup(SkillsSettings.getItemOnLevelup());
					} else {
						a.setItemOnLevelup(item);
					}
					if (money == -1) {
						a.setMoneyOnLevelup(SkillsSettings.getMoneyOnLevelup());
					} else {
						a.setMoneyOnLevelup(money);
					}
					a.setSkillName(key);
					a.setEnabled(enabled);
					a.setMultiplier(multiplier);
					a.setAllFromFirstLevel(c.getBoolean("skills." + key + ".MineAllBlocksFromFirstLevel"));
					for(String s: c.getConfigurationSection("skills." + key + ".block_level").getKeys(false)){
						a.addBlockLevels(Integer.parseInt(s), c.getInt("skills." + key + ".block_level." + s));
					}
					for(String s: c.getConfigurationSection("skills." + key + ".block_XP").getKeys(false)){
						a.addBlock(Integer.parseInt(s), c.getInt("skills." + key + ".block_XP." + s));
					}
					a.setSpeed(c.getInt("miningspeed", 1));
					a.setDoubleDropChange(c.getInt("change", 5000));
					skills.put(keys, a);
				}
				
				if(keys.startsWith("axes")){
					Axes a = new Axes();
					if (item == null) {
						a.setItemOnLevelup(SkillsSettings.getItemOnLevelup());
					} else {
						a.setItemOnLevelup(item);
					}
					if (money == -1) {
						a.setMoneyOnLevelup(SkillsSettings.getMoneyOnLevelup());
					} else {
						a.setMoneyOnLevelup(money);
					}
					a.setSkillName("axes");
					a.setEnabled(enabled);
					a.setMultiplier(multiplier);
					skills.put("axes", a);
				}
				if(keys.toLowerCase().startsWith("swords")){
					Swords a = new Swords();
					if (item == null) {
						a.setItemOnLevelup(SkillsSettings.getItemOnLevelup());
					} else {
						a.setItemOnLevelup(item);
					}
					if (money == -1) {
						a.setMoneyOnLevelup(SkillsSettings.getMoneyOnLevelup());
					} else {
						a.setMoneyOnLevelup(money);
					}
					a.setSkillName("swords");
					a.setEnabled(enabled);
					a.setMultiplier(multiplier);
					skills.put("swords", a);
				}
				if(keys.toLowerCase().startsWith("unarmed")){
					Unarmed a = new Unarmed();
					if (item == null) {
						a.setItemOnLevelup(SkillsSettings.getItemOnLevelup());
					} else {
						a.setItemOnLevelup(item);
					}
					if (money == -1) {
						a.setMoneyOnLevelup(SkillsSettings.getMoneyOnLevelup());
					} else {
						a.setMoneyOnLevelup(money);
					}
					a.setSkillName("unarmed");
					a.setEnabled(enabled);
					a.setMultiplier(multiplier);
					skills.put("unarmed", a);
				}
				if(key.equalsIgnoreCase("woodcutting")){
					Woodcutting a = new Woodcutting();
					if (item == null) {
						a.setItemOnLevelup(SkillsSettings.getItemOnLevelup());
					} else {
						a.setItemOnLevelup(item);
					}
					if (money == -1) {
						a.setMoneyOnLevelup(SkillsSettings.getMoneyOnLevelup());
					} else {
						a.setMoneyOnLevelup(money);
					}
					a.setSkillName(key);
					a.setEnabled(enabled);
					a.setMultiplier(multiplier);
					a.setAllFromFirstLevel(c.getBoolean("skills." + key + ".MineAllBlocksFromFirstLevel"));
					for(String s: c.getConfigurationSection("skills." + key + ".block_level").getKeys(false)){
						a.addBlockLevels(Integer.parseInt(s), c.getInt("skills." + key + ".block_level." + s));
					}
					for(String s: c.getConfigurationSection("skills." + key + ".block_XP").getKeys(false)){
						a.addBlock(Integer.parseInt(s), c.getInt("skills." + key + ".block_XP." + s));
					}
					skills.put(keys, a);
				}
				if(key.equalsIgnoreCase("digging")){
					Digging a = new Digging();
					if (item == null) {
						a.setItemOnLevelup(SkillsSettings.getItemOnLevelup());
					} else {
						a.setItemOnLevelup(item);
					}
					if (money == -1) {
						a.setMoneyOnLevelup(SkillsSettings.getMoneyOnLevelup());
					} else {
						a.setMoneyOnLevelup(money);
					}
					a.setSkillName(key);
					a.setEnabled(enabled);
					a.setMultiplier(multiplier);
					a.setAllFromFirstLevel(c.getBoolean("skills." + key + ".MineAllBlocksFromFirstLevel"));
					for(String s: c.getConfigurationSection("skills." + key + ".block_level").getKeys(false)){
						a.addBlockLevels(Integer.parseInt(s), c.getInt("skills." + key + ".block_level." + s));
					}
					for(String s: c.getConfigurationSection("skills." + key + ".block_XP").getKeys(false)){
						a.addBlock(Integer.parseInt(s), c.getInt("skills." + key + ".block_XP." + s));
					}
					skills.put(keys, a);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HashSet<SkillBase> getSkills() {
		HashSet<SkillBase> set = new HashSet<SkillBase>();
		for (String s : skills.keySet()) {
			set.add(skills.get(s));
		}
		return set;
	}

	public void createSkillsSettings() {
		log.info(logPrefix + "Trying to create default skills...");
		try {
			new File("plugins/Skillz/").mkdir();
			File efile = new File("plugins" + File.separator + "Skillz" + File.separator + "skills.yml");
			InputStream in = this.getClass().getClassLoader()
					.getResourceAsStream("skills.yml");
			OutputStream out = new BufferedOutputStream(new FileOutputStream(
					efile));
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			out.flush();
			out.close();
			in.close();
			log.info(logPrefix + "Default skills created succesfully!");
		} catch (Exception e) {
			e.printStackTrace();
			log.warning(logPrefix
					+ "Error creating Skills file! Using default Skills!");
		}
	}

}
