package nl.lolmen.Skills.skills;

import nl.lolmen.Skills.SkillBase;

public class Archery extends SkillBase {

	public Archery() {
	}

	private int blocks_till_XP;

	public int getBlocks_till_XP() {
		return blocks_till_XP;
	}

	public void setBlocks_till_XP(int blocks_till_XP) {
		this.blocks_till_XP = blocks_till_XP;
	}

}
