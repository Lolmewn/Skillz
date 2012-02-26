package nl.lolmen.API;

import nl.lolmen.Skills.SkillBase;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkillzLevelEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Player player;
	private SkillBase skill;
	private int level;

	//Gets called when I call it. Called from CPU.java
	public SkillzLevelEvent(Player player, SkillBase skill, int level) {
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
	public SkillBase getSkill(){
		return skill;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}
	
	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
