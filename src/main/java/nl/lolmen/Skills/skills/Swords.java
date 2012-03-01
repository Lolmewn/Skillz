package nl.lolmen.Skills.skills;

import org.bukkit.Bukkit;

import nl.lolmen.Skills.SkillBase;
import nl.lolmen.Skills.SkillsSettings;
import nl.lolmen.Skillz.MathProcessor;

public class Swords extends SkillBase{
	
	private int levelsPerExtraDamage;
	private String critCalc;

	public int getLevelsPerExtraDamage() {
		return levelsPerExtraDamage;
	}

	public void setLevelsPerExtraDamage(int levelsPerExtraDamage) {
		this.levelsPerExtraDamage = levelsPerExtraDamage;
	}

	public String getCritCalc() {
		return critCalc;
	}

	public void setCritCalc(String critCalc) {
		this.critCalc = critCalc;
	}
	
	public int getCritChance(int level){
		if(critCalc != null && critCalc != "" && critCalc.contains("$LEVEL")){
			String send = critCalc.replace("$LEVEL", Integer.toString(level));
			return MathProcessor.processEquation(send);
		}else if(SkillsSettings.isDebug()){
			Bukkit.getLogger().info("Can't calculate crit chance, config is wrong: " + this.critCalc);
		}
		return 0;
	}

}
