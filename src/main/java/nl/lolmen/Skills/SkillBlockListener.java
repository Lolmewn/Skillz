package nl.lolmen.Skills;

import nl.lolmen.Skills.skills.CustomSkill;
import nl.lolmen.Skills.skills.Mining;
import nl.lolmen.Skillz.Skillz;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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
		double time = System.nanoTime();
		for (SkillBase base : this.plugin.getSkillManager().getSkills()) {
			if (base instanceof SkillBlockBase) {
				if(SkillsSettings.isDebug()){System.out.println("[Skillz - Debug] Skill " + base.getSkillName() + " = true");}
				SkillBlockBase s = (SkillBlockBase) base;
				if (!s.isEnabled()) {
					continue;
				}
				handleSkill(s, event);
			}
		}
		for(CustomSkill skill : this.plugin.getCustomSkillManager().getSkillsUsing("BLOCK_BREAK")){
			if(!skill.isEnabled()){
				continue;
			}
			if(skill.hasBlock(event.getBlock())){
				if(CPU.getLevel(event.getPlayer(), skill, this.plugin) < skill.getLevelNeeded(event.getBlock())){
					event.getPlayer().sendMessage("You are not allowed to break this block! "
							+ skill.getSkillName().substring(0, 1).toUpperCase()
							+ skill.getSkillName().substring(1).toLowerCase()
							+ " level needed:" + skill.getLevelNeeded(event.getBlock()));
					event.setCancelled(true);
					return;
				}
				int xpget = skill.getXP(event.getBlock()) * skill.getMultiplier();
				skill.addXP(event.getPlayer(), xpget);
			}
		}
		this.plugin.fb.blockBreak(event.getPlayer());
		if(SkillsSettings.isDebug()){double end = System.nanoTime();double taken = (end - time) / 1000000; System.out.println("[Skillz - Debug] BLOCK_BREAK done in " + taken + "ms");}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.isCancelled()){
			return;
		}
		double time = System.nanoTime();
		for(CustomSkill skill : this.plugin.getCustomSkillManager().getSkillsUsing("BLOCK_PLACE")){
			if(!skill.isEnabled()){
				continue;
			}
			if(skill.hasBlock(event.getBlock())){
				if(CPU.getLevel(event.getPlayer(), skill, this.plugin) < skill.getLevelNeeded(event.getBlock())){
					event.getPlayer().sendMessage("You are not allowed to break this block! "
							+ skill.getSkillName().substring(0, 1).toUpperCase()
							+ skill.getSkillName().substring(1).toLowerCase()
							+ " level needed:" + skill.getLevelNeeded(event.getBlock()));
					event.setCancelled(true);
					return;
				}
				int xpget = skill.getXP(event.getBlock()) * skill.getMultiplier();
				skill.addXP(event.getPlayer(), xpget);
			}
		}
		if(SkillsSettings.isDebug()){double end = System.nanoTime();double taken = (end - time) / 1000000; System.out.println("[Skillz - Debug] BLOCK_PLACE done in " + taken + "ms");}
	}

	private void handleSkill(SkillBlockBase s, BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (s.hasBlock(event.getBlock())) {
			int lvlneeded = s.getLevelNeeded(event.getBlock());
			if (!s.isAllFromFirstLevel() && CPU.getLevel(p, s, this.plugin) < lvlneeded) {
				p.sendMessage("You are not allowed to mine this block! "
						+ s.getSkillName().substring(0, 1).toUpperCase()
						+ s.getSkillName().substring(1).toLowerCase()
						+ " level needed:" + lvlneeded);
				event.setCancelled(true);
				return;
			}
			int xpget = s.getXP(event.getBlock().getTypeId())
					* s.getMultiplier();
			s.addXP(p, xpget);
			if (s.getSkillName().equalsIgnoreCase("mining")) {
				if(SkillsSettings.isDebug()){
					System.out.println("It's mining, checking doubledrop");
				}
				// Change calculating here
				Mining m = (Mining) s;
				if (m.getWillDoubleDrop(p)) {
					event.getBlock().breakNaturally();
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event){
		if(event.isCancelled()){return;}
		int ticks = 10; //TODO calculate
		this.plugin.fb.blockDamage(event.getPlayer(), event.getBlock(), ticks);
	}

}
