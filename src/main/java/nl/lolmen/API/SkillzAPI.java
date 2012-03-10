package nl.lolmen.API;

import org.bukkit.entity.Player;

import nl.lolmen.Skills.CPU;
import nl.lolmen.Skills.SkillBase;
import nl.lolmen.Skillz.Skillz;

public class SkillzAPI {
	
	private static Skillz plugin;
	public SkillzAPI(Skillz main){
		plugin = main;
	}
	
	public SkillzAPI(){}
	
	public SkillzSettings getSettings(){
		return new SkillzSettings();
	}
	
	public boolean hasSkill(String name){
		return plugin.skillManager.skills.containsKey(name);
	}
	
	public SkillBase getSkill(String name){
		return plugin.skillManager.skills.get(name);
	}
	
	public void removeSkill(String name){
		plugin.skillManager.skills.remove(name);
	}
	
	public void addXP(Player player, SkillBase skill, int amount){
		skill.addXP(player, amount);
	}
	
	public int getLevel(Player player, SkillBase skill){
		return CPU.getLevel(player, skill, plugin);
	}
		
}
