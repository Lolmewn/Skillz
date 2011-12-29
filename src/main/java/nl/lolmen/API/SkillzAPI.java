package nl.lolmen.API;

import nl.lolmen.Skillz.Skillz;

public class SkillzAPI {
	
	public SkillzAPI() {
		//Constructor for Skillz
	}
	
	public SkillzSettings getSettings(){
		return new SkillzSettings(new Skillz());
	}
}
