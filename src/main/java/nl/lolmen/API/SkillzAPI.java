package nl.lolmen.API;

import org.bukkit.entity.Player;

import nl.lolmen.Skills.CPU;
import nl.lolmen.Skills.SkillBase;
import nl.lolmen.Skills.SkillManager;

public class SkillzAPI {
	
	public SkillzSettings getSettings(){
		return new SkillzSettings();
	}
	
	public boolean hasSkill(String name){
		return SkillManager.skills.containsKey(name);
	}
	
	public SkillBase getSkill(String name){
		return SkillManager.skills.get(name);
	}
	
	public void removeSkill(String name){
		SkillManager.skills.remove(name);
	}
	
	public void addXP(Player player, SkillBase skill, int amount){
		skill.addXP(player, amount);
	}
	
	public int getLevel(Player player, SkillBase skill){
		return CPU.getLevel(player, skill);
	}
		
}
