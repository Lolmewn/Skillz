package nl.lolmen.Skills;

import net.citizensnpcs.api.CitizensManager;
import nl.lolmen.Skills.CPU;
import nl.lolmen.Skills.skills.Acrobatics;
import nl.lolmen.Skills.skills.Archery;
import nl.lolmen.Skills.skills.Axes;
import nl.lolmen.Skills.skills.Swimming;
import nl.lolmen.Skills.skills.Swords;
import nl.lolmen.Skills.skills.Unarmed;
import nl.lolmen.Skillz.Skillz;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class SkillEntityListener implements Listener{
	
	private Skillz plugin;
	public SkillEntityListener(Skillz main){
		this.plugin = main;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.isCancelled()){
			return;
		}
		Entity e = event.getEntity();
		if(SkillsSettings.hasCitizens()){
			if(CitizensManager.isNPC(e)){
				//No need to give NPC's XP
				return;
			}
		}
		if(e instanceof Player){
			Player p = (Player)e;
			SkillBase s;
			switch(event.getCause()){
			case FALL:
				s = plugin.skillManager.skills.get("acrobatics");
				if(s == null){
					return;
				}
				if(!s.isEnabled()){
					return;
				}
				Acrobatics a = (Acrobatics)s;
				int damage = event.getDamage() * s.getMultiplier();
				s.addXP(p, damage);
				if(CPU.getLevel(p, s) >= a.getLevelsTillLessDMG()){
					double deduct = CPU.getLevel(p, s) / a.getLevelsTillLessDMG();
					if(deduct >= event.getDamage()){
						event.setDamage(0);
					}else{
						event.setDamage((int) (event.getDamage() - deduct));
					}
					p.sendMessage(SkillsSettings.getFalldmg((int)deduct));
				}
				return;
			case DROWNING:
				s = plugin.skillManager.skills.get("swimming");
				if(s == null){
					return;
				}
				if(!s.isEnabled()){
					return;
				}
				Swimming sw = (Swimming)s;
				if(sw.wontDrown(CPU.getLevel(p, s))){
					event.setCancelled(true);
				}
				s.addXP(p, s.getMultiplier());
				return;
			}
		}
		if(event instanceof EntityDamageByEntityEvent){
			Entity att = ((EntityDamageByEntityEvent)event).getDamager();
			if(att instanceof Player){
				Player p = (Player)att;
				Material m = p.getItemInHand().getType();
				if(m.equals(Material.WOOD_SWORD) || m.equals(Material.IRON_SWORD) || m.equals(Material.STONE_SWORD) || m.equals(Material.DIAMOND_SWORD) || m.equals(Material.GOLD_SWORD)){
					if(SkillsSettings.isDebug()){
						System.out.println("[Skillz - Debug] Adding some to Swords..");
					}
					SkillBase s = plugin.skillManager.skills.get("swords");
					if(s == null){
						if(SkillsSettings.isDebug()){
							System.out.println("[Skillz - Debug] Skill is null for some reason O.o Tried to get swords");
						}
						return;
					}
					if(!s.isEnabled()){
						if(SkillsSettings.isDebug()){
							System.out.println("[Skillz - Debug] Skill " + s.getSkillName() + " not enabled, returning");
						}
						return;
					}
					Swords sw = (Swords)s;
					if(SkillsSettings.isDebug()){
						plugin.log.info("[Skillz - Debug] Original damage: " + event.getDamage());
					}
					event.setDamage(event.getDamage() + sw.getExtraDamage(CPU.getLevel(p, s)));
					if(SkillsSettings.isDebug()){
						plugin.log.info("[Skillz - Debug] Damage dealt after extra: " + event.getDamage());
					}
					if(sw.willCrit(CPU.getLevel(p, s))){
						event.setDamage(event.getDamage() * 2);
						p.sendMessage("[Skillz] " + ChatColor.RED + "Critical strike!");
						if(SkillsSettings.isDebug()){
							plugin.log.info("[Skillz - Debug] Crit! Damage dealt: " + event.getDamage());
						}
					}
					s.addXP(p, s.getMultiplier());
					if(SkillsSettings.isDebug()){
						System.out.println("[Skillz - Debug] XP added.");
					}
					return;
				}
				if(m.equals(Material.WOOD_AXE) || m.equals(Material.IRON_AXE) || m.equals(Material.STONE_AXE) || m.equals(Material.DIAMOND_AXE) || m.equals(Material.GOLD_AXE)){
					if(SkillsSettings.isDebug()){
						System.out.println("[Skillz - Debug] Adding some to Axes..");
					}
					SkillBase s = plugin.skillManager.skills.get("axes");
					if(s == null){
						if(SkillsSettings.isDebug()){
							System.out.println("[Skillz - Debug] Axes == null? That's weird..");
						}
						return;
					}
					if(!s.isEnabled()){
						if(SkillsSettings.isDebug()){
							System.out.println("[Skillz - Debug] Axes is not enabled, returning");
						}
						return;
					}
					Axes sw = (Axes)s;
					if(SkillsSettings.isDebug()){
						plugin.log.info("Original damage: " + event.getDamage());
					}
					event.setDamage(event.getDamage() + sw.getExtraDamage(CPU.getLevel(p, s)));
					if(SkillsSettings.isDebug()){
						plugin.log.info("Damage dealt after extra: " + event.getDamage());
					}
					if(sw.willCrit(CPU.getLevel(p, s))){
						event.setDamage(event.getDamage() * 2);
						p.sendMessage("[Skillz] " + ChatColor.RED + "Critical strike!");
						if(SkillsSettings.isDebug()){
							plugin.log.info("Crit! Damage dealt: " + event.getDamage());
						}
					}
					s.addXP(p, s.getMultiplier());
					if(SkillsSettings.isDebug()){
						System.out.println("[Skillz - Debug] Done adding XP");
					}
					return;
				}
				if(p.getItemInHand().getType() == Material.AIR){
					if(SkillsSettings.isDebug()){
						System.out.println("[Skillz - Debug] Adding some to unarmed");
					}
					SkillBase s = plugin.skillManager.skills.get("unarmed");
					if(s == null){
						if(SkillsSettings.isDebug()){
							System.out.println("[Skillz - Debug] unarmed == null? That's weird..");
						}
						return;
					}
					if(!s.isEnabled()){
						if(SkillsSettings.isDebug()){
							System.out.println("[Skillz - Debug] Unarmed is not enabled, returning");
						}
						return;
					}
					Unarmed sw = (Unarmed)s;
					if(SkillsSettings.isDebug()){
						plugin.log.info("[Skillz - Debug] Original damage: " + event.getDamage());
					}
					event.setDamage(event.getDamage() + sw.getExtraDamage(CPU.getLevel(p, s)));
					if(SkillsSettings.isDebug()){
						plugin.log.info("Damage dealt after extra: " + event.getDamage());
					}
					if(sw.willCrit(CPU.getLevel(p, s))){
						event.setDamage(event.getDamage() * 2);
						p.sendMessage("[Skillz] " + ChatColor.RED + "Critical strike!");
						if(SkillsSettings.isDebug()){
							plugin.log.info("[Skillz - Debug] Crit! Damage dealt: " + event.getDamage());
						}
					}
					s.addXP(p, s.getMultiplier());
					if(SkillsSettings.isDebug()){
						System.out.println("[Skillz - Debug] Done adding XP.");
					}
					return;
				}
			}
			if(att instanceof Arrow){
				LivingEntity ent = ((Arrow)att).getShooter();
				if(ent instanceof Player){
					Archery s = (Archery) plugin.skillManager.skills.get("archery");
					if(s == null){
						return;
					}
					if(!s.isEnabled()){
						return;
					}
					double distance = ent.getLocation().distance(e.getLocation());
					Player p = (Player)ent;
					int added = (int)distance/s.getBlocks_till_XP() * s.getMultiplier();
					if(added == 2){
						p.sendMessage("[Skillz] Double XP!");
					}
					if(added == 3){
						p.sendMessage("[Skillz] " + ChatColor.LIGHT_PURPLE + "TRIPLE XP!");
					}
					if(added == 4){
						p.sendMessage("[Skillz] " + ChatColor.RED  +"QUADRA XP!");
					}
					if(added > 4){
						p.sendMessage("[Skillz] " + ChatColor.DARK_RED + "MULTI XP!");
					}
					if(s.willCrit(CPU.getLevel((Player)ent, s))){
						event.setDamage(event.getDamage() * 2);
						p.sendMessage("[Skillz] " + ChatColor.RED + "Critical hit!");
					}
					s.addXP(p, added);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		if(!(event.getEntity() instanceof Player)){
			return;
		}
		if(SkillsSettings.hasCitizens()){
			//It's an NPC, no need to do anything with it
			if(CitizensManager.isNPC(event.getEntity())){
				return;
			}
		}
		Player p = (Player)event.getEntity();
		if(SkillsSettings.isResetSkillsOnLevelup()){
			for(String s: plugin.skillManager.skills.keySet()){
				SkillBase skill = plugin.skillManager.skills.get(s);
				CPU.setLevelWithXP(p, skill, 1);
			}
			p.sendMessage(SkillsSettings.getLevelsReset());
			return;
		}
		if(SkillsSettings.getLevelsDownOnDeath() != 0){
			for(String s: plugin.skillManager.skills.keySet()){
				SkillBase skill = plugin.skillManager.skills.get(s);
				if(CPU.getLevel(p, skill) <= SkillsSettings.getLevelsDownOnDeath()){
					CPU.setLevelWithXP(p, skill, 1);
				}else{
					CPU.setLevelWithXP(p, skill, CPU.getLevel(p, skill)-SkillsSettings.getLevelsDownOnDeath());
				}
			}
			p.sendMessage(SkillsSettings.getLevelsReset());
			return;
		}
	}

}
