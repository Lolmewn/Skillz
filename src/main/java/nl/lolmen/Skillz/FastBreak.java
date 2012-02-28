package nl.lolmen.Skillz;

import java.util.HashMap;

import nl.lolmen.Skills.SkillsSettings;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
public class FastBreak {
	
	protected HashMap<String, Integer> timed = new HashMap<String, Integer>();
	protected HashMap<String, Integer> FCount = new HashMap<String, Integer>();
	protected HashMap<String, Block> FBlock = new HashMap<String, Block>();

	public void blockBreak(Player p) {
		if(this.FBlock.containsKey(p.getName())){
			this.FBlock.remove(p.getName());
		}
		if(this.FCount.containsKey(p.getName())){
			if(SkillsSettings.isDebug()){
				p.sendMessage("Ticks: " + this.FCount.get(p.getName()));
			}
			this.FCount.remove(p.getName());
		}
		if(!this.timed.containsKey(p.getName())){
			return;
		}
	}
	
	public void blockDamage(Player p, Block b, int ticks){
		if(this.FBlock.containsKey(p.getName())){
			this.FBlock.remove(p.getName());
		}
		if (this.FCount.containsKey(p.getName())) {
			this.FCount.remove(p.getName());
		}
		if(this.timed.containsKey(p.getName())){
			this.timed.remove(p.getName());
		}
		this.FBlock.put(p.getName(), b);
		this.FCount.put(p.getName(), 0);
		this.timed.put(p.getName(), ticks); 
		if(SkillsSettings.isDebug()){
			p.sendMessage("Block damaged, added to Count and Block");
		}
	}
	
	public void playerAnimate(Player p){
		if(!this.FCount.containsKey(p.getName())){
			return;
		}
		int totalTime = this.timed.get(p.getName());
		int done = this.FCount.get(p.getName());
		if(SkillsSettings.isDebug()){
			p.sendMessage("debug: " + totalTime  +":" + done);
		}
		if(totalTime < 1){
			totalTime = 1;
		}
		if(totalTime == 0 || totalTime <= done){
			BlockBreakEvent ev = new BlockBreakEvent(this.FBlock.get(p.getName()), p);
			Bukkit.getServer().getPluginManager().callEvent(ev);
			if(ev.isCancelled()){
				return;
			}
			ev.getBlock().breakNaturally();
			ev.getBlock().setType(Material.AIR);
			this.FBlock.remove(p.getName());
			this.FCount.remove(p.getName());
			this.timed.remove(p.getName());
		}else{
			this.FCount.remove(p.getName());
			this.FCount.put(p.getName(), done++);
		}
		if(SkillsSettings.isDebug()){
			p.sendMessage("Debug:" + totalTime + ":" + this.FCount.get(p.getName()));
		}
	}

}
