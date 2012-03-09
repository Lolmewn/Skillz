package nl.lolmen.Skills.skills;

import java.util.Random;

import org.bukkit.Bukkit;

import nl.lolmen.Skills.SkillBase;
import nl.lolmen.Skills.SkillsSettings;
import nl.lolmen.Skillz.MathProcessor;
import nl.lolmen.Skillz.Skillz;

public class Archery extends SkillBase {
	
	private String critCalc;
	private int blocks_till_XP;

	public Archery(Skillz plugin) {
		super(plugin);
	}

	public int getBlocks_till_XP() {
		return blocks_till_XP;
	}

	public void setBlocks_till_XP(int blocks_till_XP) {
		this.blocks_till_XP = blocks_till_XP;
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
			return (int)MathProcessor.processEquation(send);
		}else if(SkillsSettings.isDebug()){
			Bukkit.getLogger().info("[Skillz] Can't calculate crit chance, config is wrong for " + this.getSkillName() + ": " + this.critCalc);
		}
		return 0;
	}
	
	public boolean willCrit(int level){
		int chance = this.getCritChance(level);
		Random rant = new Random();
		int result = rant.nextInt(100);
		if(result < chance){
			return true;
		}
		return false;
	}
	

}
