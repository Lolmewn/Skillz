package nl.lolmen.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class SkillzXPGainEvent extends Event implements Cancellable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean cancelled;
	private Player p;
	private String skill;
	private int xp;
	private boolean levelup;
	public SkillzXPGainEvent(String name, Player p, String skill, int xp, boolean levelup){
		super(name);
		this.setP(p);
		this.setSkill(skill);
		this.setXp(xp);
		this.setLevelup(levelup);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}

	public Player getPlayer() {
		return p;
	}

	private void setP(Player p) {
		this.p = p;
	}

	public String getSkillName() {
		return skill;
	}

	private void setSkill(String skill) {
		this.skill = skill;
	}

	public int getXp() {
		return xp;
	}

	private void setXp(int xp) {
		this.xp = xp;
	}

	public boolean isLevelup() {
		return levelup;
	}

	private void setLevelup(boolean levelup) {
		this.levelup = levelup;
	}

}
