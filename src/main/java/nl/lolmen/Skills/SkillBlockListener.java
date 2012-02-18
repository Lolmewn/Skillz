package nl.lolmen.Skills;

import nl.lolmen.Skills.skills.Mining;
import nl.lolmen.Skillz.Skillz;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class SkillBlockListener implements Listener{
	
	private Skillz plugin;
	public SkillBlockListener(Skillz main){
		this.plugin = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.isCancelled()){
			return;
		}
		for (SkillBase base : plugin.skillManager.getSkills()) {
			if (base instanceof SkillBlockBase) {
				if(SkillsSettings.isDebug()){System.out.println("Skill " + base.getSkillName() + " = true");}
				SkillBlockBase s = (SkillBlockBase) base;
				if (!s.isEnabled()) {
					continue;
				}
				handleSkill(s, event);
			}
			if(SkillsSettings.isDebug()){System.out.println("Dont checking skills for Block");}
		}
	}

	private void handleSkill(SkillBlockBase s, BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (s.hasBlock(event.getBlock())) {
			int lvlneeded = s.getLevelNeeded(event.getBlock());
			if (!s.isAllFromFirstLevel() && CPU.getLevel(p, s) < lvlneeded) {
				p.sendMessage("You are not allowed to mine this block! "
						+ s.getSkillName().substring(0, 1).toUpperCase()
						+ s.getSkillName().substring(1).toLowerCase()
						+ " level needed:" + lvlneeded);
				event.setCancelled(true);
			}
			int xpget = s.getXP(event.getBlock().getTypeId())
					* s.getMultiplier();
			CPU.addXP(p, s, xpget);
			if (s instanceof Mining) {
				if(SkillsSettings.isDebug()){
					System.out.println("It's mining, checking doubledrop");
				}
				// Change calculating here
				Mining m = (Mining) s;
				if (m.getWillDoubleDrop(p)) {
					event.getBlock().breakNaturally();
					//p.getWorld().dropItemNaturally(
							//event.getBlock().getLocation(), stack);
				}
			}
		}
	}

}
