package nl.lolmen.Skills.skills;

import nl.lolmen.Skills.CPU;
import nl.lolmen.Skills.SkillBlockBase;

import org.bukkit.entity.Player;


//Mining one of the skills.

public class Mining extends SkillBlockBase{
		private int speed;
	private int doubleDropChange;

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean getWillDoubleDrop(Player p) {
		if(CPU.getLevel(p, this) > Math.random() * doubleDropChange){
			return true;
		}else{
			return false;
		}
	}

	public void setDoubleDropChange(int doubleDropChange) {
		this.doubleDropChange = doubleDropChange;
	}
	

}
