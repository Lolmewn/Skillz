package nl.lolmen.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class SkillzLevelEvent extends Event implements Cancellable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean cancelled = false;
	private Player player;
	private String skill;
	private int level;

	//Gets called when I call it. Called from CPU.java
	public SkillzLevelEvent(String name, Player player, String skill, int level) {
		super(name);
		this.player = player;
		this.skill = skill;
		this.level = level;
	}
	
	/**
	 * @return Player that leveled up
	 */
	public Player getPlayer(){
		return player;
	}
	
	/**
	 * @return Level player went to
	 */
	public int getLevel(){
		return level;
	}
	
	/**
	 * @return the Skillname the player leveled in
	 */
	public String getSkill(){
		return skill;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}

}
