package nl.lolmen.Skills;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import nl.lolmen.Skills.skills.*;
import nl.lolmen.Skillz.Skillz;
import nl.lolmen.Skillz.User;
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

public class SkillEntityListener implements Listener {

    private Skillz plugin;

    public SkillEntityListener(Skillz main) {
        this.plugin = main;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if (SkillsSettings.hasCitizens()) {
            if(e.hasMetadata("NPC")){
                return;
            }
        }
        if (e instanceof Player) {
            Player p = (Player) e;
            User u = this.plugin.getUserManager().getPlayer(p.getName());
            SkillBase s;
            switch (event.getCause()) {
                case FALL:
                    s = this.plugin.getSkillManager().skills.get("acrobatics");
                    if (s == null) {
                        return;
                    }
                    if (!s.isEnabled()) {
                        return;
                    }
                    Acrobatics a = (Acrobatics) s;
                    int damage = event.getDamage() * s.getMultiplier();
                    s.addXP(p, damage);
                    if (u.getLevel(s.getSkillName()) >= a.getLevelsTillLessDMG()) {
                        double deduct = u.getLevel(s.getSkillName()) / a.getLevelsTillLessDMG();
                        if (deduct >= event.getDamage()) {
                            event.setDamage(0);
                        } else {
                            event.setDamage((int) (event.getDamage() - deduct));
                        }
                        p.sendMessage(SkillsSettings.getFalldmg((int) deduct));
                    }
                    return;
                case DROWNING:
                    s = this.plugin.getSkillManager().skills.get("swimming");
                    if (s == null) {
                        return;
                    }
                    if (!s.isEnabled()) {
                        return;
                    }
                    Swimming sw = (Swimming) s;
                    if (sw.wontDrown(u.getLevel(s.getSkillName()))) {
                        event.setCancelled(true);
                    }
                    s.addXP(p, s.getMultiplier());
                    return;
            }
        }
        if (event instanceof EntityDamageByEntityEvent) {
            Entity att = ((EntityDamageByEntityEvent) event).getDamager();
            if (att instanceof Player) {
                Player p = (Player) att;
                if(SkillsSettings.hasWorldGuard()){
                    if(!SkillsSettings.getWorldGuard().getRegionManager(event.getEntity().getWorld()).getApplicableRegions(event.getEntity().getLocation()).allows(DefaultFlag.PVP, SkillsSettings.getWorldGuard().wrapPlayer(p))){
                        return;
                    }
                }
                User u = this.plugin.getUserManager().getPlayer(p.getName());
                Material m = p.getItemInHand().getType();
                if (m.equals(Material.WOOD_SWORD) || m.equals(Material.IRON_SWORD) || m.equals(Material.STONE_SWORD) || m.equals(Material.DIAMOND_SWORD) || m.equals(Material.GOLD_SWORD)) {
                    if (SkillsSettings.isDebug()) {
                        System.out.println("[Skillz][Debug] Adding some to Swords..");
                    }
                    SkillBase s = this.plugin.getSkillManager().skills.get("swords");
                    if (s == null) {
                        if (SkillsSettings.isDebug()) {
                            System.out.println("[Skillz][Debug] Skill is null for some reason O.o Tried to get swords");
                        }
                        return;
                    }
                    if (!s.isEnabled()) {
                        if (SkillsSettings.isDebug()) {
                            System.out.println("[Skillz][Debug] Skill " + s.getSkillName() + " not enabled, returning");
                        }
                        return;
                    }
                    Swords sw = (Swords) s;
                    if (SkillsSettings.isDebug()) {
                        this.plugin.getLogger().info("[Debug] Original damage: " + event.getDamage());
                    }
                    event.setDamage(event.getDamage() + sw.getExtraDamage(u.getLevel(s.getSkillName())));
                    if (SkillsSettings.isDebug()) {
                        this.plugin.getLogger().info("[Debug] Damage dealt after extra: " + event.getDamage());
                    }
                    if (sw.willCrit(u.getLevel(s.getSkillName()))) {
                        event.setDamage(event.getDamage() * 2);
                        p.sendMessage(SkillsSettings.getCritStrike());
                        if (SkillsSettings.isDebug()) {
                            this.plugin.getLogger().info("[Debug] Crit! Damage dealt: " + event.getDamage());
                        }
                    }
                    s.addXP(p, s.getMultiplier());
                    if (SkillsSettings.isDebug()) {
                        System.out.println("[Skillz][Debug] XP added.");
                    }
                    return;
                }
                if (m.equals(Material.WOOD_AXE) || m.equals(Material.IRON_AXE) || m.equals(Material.STONE_AXE) || m.equals(Material.DIAMOND_AXE) || m.equals(Material.GOLD_AXE)) {
                    if (SkillsSettings.isDebug()) {
                        System.out.println("[Skillz][Debug] Adding some to Axes..");
                    }
                    SkillBase s = this.plugin.getSkillManager().skills.get("axes");
                    if (s == null) {
                        if (SkillsSettings.isDebug()) {
                            System.out.println("[Skillz][Debug] Axes == null? That's weird..");
                        }
                        return;
                    }
                    if (!s.isEnabled()) {
                        if (SkillsSettings.isDebug()) {
                            System.out.println("[Skillz][Debug] Axes is not enabled, returning");
                        }
                        return;
                    }
                    Axes sw = (Axes) s;
                    if (SkillsSettings.isDebug()) {
                        this.plugin.getLogger().info("[Debug] Original damage: " + event.getDamage());
                    }
                    event.setDamage(event.getDamage() + sw.getExtraDamage(u.getLevel(s.getSkillName())));
                    if (SkillsSettings.isDebug()) {
                        this.plugin.getLogger().info("[Debug] Damage dealt after extra: " + event.getDamage());
                    }
                    if (sw.willCrit(u.getLevel(s.getSkillName()))) {
                        event.setDamage(event.getDamage() * 2);
                        p.sendMessage(SkillsSettings.getCritStrike());
                        if (SkillsSettings.isDebug()) {
                            this.plugin.getLogger().info("[Debug] Crit! Damage dealt: " + event.getDamage());
                        }
                    }
                    s.addXP(p, s.getMultiplier());
                    if (SkillsSettings.isDebug()) {
                        System.out.println("[Skillz][Debug] Done adding XP");
                    }
                    return;
                }
                if (p.getItemInHand().getType() == Material.AIR) {
                    if (SkillsSettings.isDebug()) {
                        System.out.println("[Skillz][Debug] Adding some to unarmed");
                    }
                    SkillBase s = this.plugin.getSkillManager().skills.get("unarmed");
                    if (s == null) {
                        if (SkillsSettings.isDebug()) {
                            System.out.println("[Skillz][Debug] unarmed == null? That's weird..");
                        }
                        return;
                    }
                    if (!s.isEnabled()) {
                        if (SkillsSettings.isDebug()) {
                            System.out.println("[Skillz][Debug] Unarmed is not enabled, returning");
                        }
                        return;
                    }
                    Unarmed sw = (Unarmed) s;
                    if (SkillsSettings.isDebug()) {
                        this.plugin.getLogger().info("[Debug] Original damage: " + event.getDamage());
                    }
                    event.setDamage(event.getDamage() + sw.getExtraDamage(u.getLevel(s.getSkillName())));
                    if (SkillsSettings.isDebug()) {
                        this.plugin.getLogger().info("[Debug] Damage dealt after extra: " + event.getDamage());
                    }
                    if (sw.willCrit(u.getLevel(s.getSkillName()))) {
                        event.setDamage(event.getDamage() * 2);
                        p.sendMessage(SkillsSettings.getCritStrike());
                        if (SkillsSettings.isDebug()) {
                            this.plugin.getLogger().info("[Debug] Crit! Damage dealt: " + event.getDamage());
                        }
                    }
                    s.addXP(p, s.getMultiplier());
                    if (SkillsSettings.isDebug()) {
                        System.out.println("[Skillz - Debug] Done adding XP.");
                    }
                    return;
                }
            }
            if (att instanceof Arrow) {
                LivingEntity ent = ((Arrow) att).getShooter();
                if (ent instanceof Player) {
                    if(SkillsSettings.hasWorldGuard()){
                        if(!SkillsSettings.getWorldGuard().getRegionManager(event.getEntity().getWorld()).getApplicableRegions(event.getEntity().getLocation()).allows(DefaultFlag.PVP, SkillsSettings.getWorldGuard().wrapPlayer((Player)ent))){
                            return;
                        }
                    }
                    Archery s = (Archery) this.plugin.getSkillManager().skills.get("archery");
                    User u = this.plugin.getUserManager().getPlayer(((Player)ent).getName());
                    if (s == null) {
                        return;
                    }
                    if (!s.isEnabled()) {
                        return;
                    }
                    double distance = ent.getLocation().distance(e.getLocation());
                    Player p = (Player) ent;
                    int added = (int) distance / s.getBlocks_till_XP() * s.getMultiplier();
                    if (added == 2) {
                        p.sendMessage("[Skillz] Double XP!");
                    }
                    if (added == 3) {
                        p.sendMessage("[Skillz] " + ChatColor.LIGHT_PURPLE + "TRIPLE XP!");
                    }
                    if (added == 4) {
                        p.sendMessage("[Skillz] " + ChatColor.RED + "QUADRA XP!");
                    }
                    if (added > 4) {
                        p.sendMessage("[Skillz] " + ChatColor.DARK_RED + "MULTI XP!");
                    }
                    if (s.willCrit(u.getLevel(s.getSkillName()))) {
                        event.setDamage(event.getDamage() * 2);
                        p.sendMessage("[Skillz] " + ChatColor.RED + "Critical hit!");
                    }
                    s.addXP(p, added);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(final EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player)event.getEntity();
        if (SkillsSettings.hasCitizens()) {
            if(event.getEntity().hasMetadata("NPC")){
                return;
            }
        }
        if(SkillsSettings.hasWorldGuard()){
            if(!SkillsSettings.getWorldGuard().getRegionManager(event.getEntity().getWorld()).getApplicableRegions(event.getEntity().getLocation()).allows(DefaultFlag.PVP, SkillsSettings.getWorldGuard().wrapPlayer(p))){
                return;
            }
        }
        if (SkillsSettings.isResetSkillsOnLevelup()) {
            for (SkillBase s : plugin.getSkillManager().getSkills()) {
                CPU.setLevelWithXP(p, s, 1, plugin);
            }
            p.sendMessage(SkillsSettings.getLevelsReset());
            return;
        }
        if (SkillsSettings.getLevelsDownOnDeath() != 0) {
            User u = this.plugin.getUserManager().getPlayer(p.getName());
            for (SkillBase s : plugin.getSkillManager().getSkills()) {
                if (!s.isEnabled()) {
                    continue;
                }
                if (u.getLevel(s.getSkillName()) <= SkillsSettings.getLevelsDownOnDeath()) {
                    CPU.setLevelWithXP(p, s, 1, plugin);
                } else {
                    CPU.setLevelWithXP(p, s, u.getLevel(s.getSkillName()) - SkillsSettings.getLevelsDownOnDeath(), plugin);
                }
            }
            p.sendMessage(SkillsSettings.getLevelsReset());
        }
    }
}
