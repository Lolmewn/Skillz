package nl.lolmen.Skills;

import nl.lolmen.Skills.CPU;
import nl.lolmen.Skills.skills.Acrobatics;
import nl.lolmen.Skills.skills.Archery;
import nl.lolmen.Skillz.Skillz;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class SkillEntityListener extends EntityListener{

	public void onEntityDamage(EntityDamageEvent event) {
		if(event.isCancelled()){
			return;
		}
		Entity e = event.getEntity();
		if(e instanceof Player){
			Player p = (Player)e;
			SkillBase s;
			switch(event.getCause()){
			case FALL:
				s = SkillManager.skills.get("acrobatics");
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
					int less = event.getDamage() - (int)deduct;
					if(less > event.getDamage()){
						event.setDamage(0);
					}else{
						event.setDamage(less);
					}
					p.sendMessage(SkillsSettings.getFalldmg(less));
				}
				return;
			case DROWNING:
				s = SkillManager.skills.get("swimming");
				if(s == null){
					return;
				}
				if(!s.isEnabled()){
					return;
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
				if(m.equals(Material.WOOD_SWORD) || m.equals(Material.IRON_SWORD) || m.equals(Material.STONE_SWORD) || m.equals(Material.DIAMOND_SWORD)){
					SkillBase s = SkillManager.skills.get("swords");
					if(s == null){
						return;
					}
					if(!s.isEnabled()){
						return;
					}
					s.addXP(p, s.getMultiplier());
					return;
				}
				if(m.equals(Material.WOOD_AXE) || m.equals(Material.IRON_AXE) || m.equals(Material.STONE_AXE) || m.equals(Material.DIAMOND_AXE)){
					SkillBase s = SkillManager.skills.get("axes");
					if(s == null){
						return;
					}
					if(!s.isEnabled()){
						return;
					}
					s.addXP(p, s.getMultiplier());
				}
				if(p.getItemInHand().getType() == Material.AIR){
					SkillBase s = SkillManager.skills.get("unarmed");
					if(s == null){
						return;
					}
					if(!s.isEnabled()){
						return;
					}
					s.addXP(p, s.getMultiplier());
					return;
				}
			}
			if(att instanceof Arrow){
				LivingEntity ent = ((Arrow)att).getShooter();
				if(ent instanceof Player){
					Archery s = SkillManager.ar;
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
						p.sendMessage("Double XP!");
					}
					if(added == 3){
						p.sendMessage(ChatColor.LIGHT_PURPLE + "TRIPLE XP!");
					}
					if(added == 4){
						p.sendMessage(ChatColor.RED  +"QUADRA XP!");
					}
					if(added > 4){
						p.sendMessage(ChatColor.DARK_RED + "MULTI XP!");
					}
					s.addXP(p, added);
				}
			}
		}
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		new SkillPlayerListener(Skillz.p).onPlayerDeath(event);
	}

}
