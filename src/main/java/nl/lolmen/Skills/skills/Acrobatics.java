package nl.lolmen.Skills.skills;

import nl.lolmen.Skills.SkillBase;
import nl.lolmen.Skillz.Skillz;

public class Acrobatics extends SkillBase{
	
	public Acrobatics(Skillz plugin) {
		super(plugin);
	}

	private int levelsTillLessDMG;

	public int getLevelsTillLessDMG() {
		return levelsTillLessDMG;
	}

	public void setLevelsTillLessDMG(int levelsTillLessDMG) {
		this.levelsTillLessDMG = levelsTillLessDMG;
	}
	
}
