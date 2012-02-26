package nl.lolmen.API;

import nl.lolmen.Skills.SkillBase;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkillzXPGainEvent extends Event implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private Player p;
	private SkillBase skill;
	private int xp;
	public SkillzXPGainEvent(Player p, SkillBase skill, int xp){
		this.setP(p);
		this.setSkill(skill);
		this.setXp(xp);
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}

	public Player getPlayer() {
		return p;
	}

	private void setP(Player p) {
		this.p = p;
	}

	public SkillBase getSkillName() {
		return skill;
	}

	private void setSkill(SkillBase skill2) {
		this.skill = skill2;
	}

	public int getXp() {
		return xp;
	}

	private void setXp(int xp) {
		this.xp = xp;
	}
	
	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
