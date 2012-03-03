package nl.lolmen.Skills;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import nl.lolmen.Skills.skills.CustomSkill;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomSkillManager {
	
	private File customFile = new File("plugins" + File.separator + "Skillz" + File.separator + "custom.yml");
	
	private HashMap<String, CustomSkill> customs = new HashMap<String, CustomSkill>();
	
	public void loadCustomSkills(){
		if(!customFile.exists()){
			
		}
		YamlConfiguration c = YamlConfiguration.loadConfiguration(customFile);
		try{
			for(String key : c.getConfigurationSection("").getKeys(false)){
				if(SkillsSettings.isDebug()){
					Bukkit.getLogger().info("[Skillz - Debug] Starting to load " + key);
				}
				CustomSkill s = new CustomSkill();
				s.setSkillName(c.getString(key + ".name"));
				s.setEnabled(c.getBoolean(key + ".enabled", true));
				s.setMultiplier(c.getInt(key + ".XP-gain-multiplier", 1));
				s.setUses(c.getString(key + ".uses"));
				for(String use : s.getUsesArray()){
					if(c.contains(key + "." + use + ".block_level")){
						for(String block: c.getConfigurationSection(key + ".block_level").getKeys(false)){
							s.addBlockLevels(Integer.parseInt(block), c.getInt(key + "." + use + ".block_level." + block));
						}
					}
					if(c.contains(key + "." + use + ".block_xp")){
						for(String block: c.getConfigurationSection(key + ".block_xp").getKeys(false)){
							s.addBlockLevels(Integer.parseInt(block), c.getInt(key + "." + use + ".block_xp." + block));
						}
					}
				}
				s.setItemOnLevelup(c.getString(key + ".reward.item", SkillsSettings.getItemOnLevelup()));
				s.setMoneyOnLevelup(c.getInt(key + ".reward.money", SkillsSettings.getMoneyOnLevelup()));
				if(c.contains(key + ".reward.every_many_levels")){
					for(String lvl: c.getConfigurationSection(key + ".reward.every_many_levels").getKeys(false)){
						s.add_to_every_many_levels(Integer.parseInt(lvl), c.getString(key + ".reward.every_many_levels." + lvl));
					}
				}
				if(c.contains(key + ".reward.fixed_levels")){
					for(String lvl: c.getConfigurationSection(key + ".reward.fixed_levels").getKeys(false)){
						s.add_to_fixed_levels(Integer.parseInt(lvl), c.getString(key + ".reward.fixed_levels." + lvl));
					}
				}
				this.customs.put(key, s);
				if(SkillsSettings.isDebug()){
					Bukkit.getLogger().info("[Skillz - Debug] Finished loading " + key + " without errors");
				}
			}
			if(SkillsSettings.isDebug()){
				Bukkit.getLogger().info("[Skillz - Debug] Loaded " + this.customs.size() + " custom skills");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public HashSet<CustomSkill> getSkills(){
		HashSet<CustomSkill> set = new HashSet<CustomSkill>();
		for(String item : this.customs.keySet()){
			set.add(this.customs.get(item));
		}
		return set;
	}
	
	public HashSet<CustomSkill> getSkillsUsing(String eventType){
		HashSet<CustomSkill> set = new HashSet<CustomSkill>();
		for(CustomSkill skill : this.getSkills()){
			if(skill.hasUse(eventType)){
				set.add(skill);
			}
		}
		return set;
	}

}
