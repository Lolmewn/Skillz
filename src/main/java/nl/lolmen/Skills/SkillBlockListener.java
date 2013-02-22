package nl.lolmen.Skills;

import nl.lolmen.Skills.skills.CustomSkill;
import nl.lolmen.Skills.skills.Mining;
import nl.lolmen.Skillz.Skillz;
import nl.lolmen.Skillz.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SkillBlockListener implements Listener {

    private Skillz plugin;

    public SkillBlockListener(Skillz main) {
        this.plugin = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        double time = System.nanoTime();
        if(SkillsSettings.hasWorldGuard()){
            if(!SkillsSettings.getWorldGuard().canBuild(event.getPlayer(), event.getBlock())){
                return;
            }
        }
        User u = this.plugin.getUserManager().getPlayer(event.getPlayer().getName());
        for (SkillBase base : this.plugin.getSkillManager().getSkills()) {
            if (base instanceof SkillBlockBase) {
                this.plugin.debug("[Skillz - Debug] Skill " + base.getSkillName() + " = true", 2);
                if (!base.isEnabled()) {
                    continue;
                }
                handleSkill((SkillBlockBase) base, event, u);
            }
        }
        for (CustomSkill skill : this.plugin.getCustomSkillManager().getSkillsUsing("BLOCK_BREAK")) {
            if (!skill.isEnabled()) {
                continue;
            }
            if (skill.hasBlock(event.getBlock())) {
                if (u.getLevel(skill.getSkillName()) < skill.getLevelNeeded(event.getBlock())) {
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
        //this.plugin.fb.blockBreak(event.getPlayer());
        double end = System.nanoTime();
        double taken = (end - time) / 1000000;
        this.plugin.debug("[Skillz - Debug] BLOCK_BREAK done in " + taken + "ms", 2);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        double time = System.nanoTime();
        if(SkillsSettings.hasWorldGuard()){
            if(!SkillsSettings.getWorldGuard().canBuild(event.getPlayer(), event.getBlock())){
                return;
            }
        }
        User u = this.plugin.getUserManager().getPlayer(event.getPlayer().getName());
        for (CustomSkill skill : this.plugin.getCustomSkillManager().getSkillsUsing("BLOCK_PLACE")) {
            if (!skill.isEnabled()) {
                continue;
            }
            if (skill.hasBlock(event.getBlock())) {
                if (u.getLevel(skill.getSkillName()) < skill.getLevelNeeded(event.getBlock())) {
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
        if (SkillsSettings.isDebug()) {
            double end = System.nanoTime();
            double taken = (end - time) / 1000000;
            System.out.println("[Skillz - Debug] BLOCK_PLACE done in " + taken + "ms");
        }
    }

    private void handleSkill(SkillBlockBase s, BlockBreakEvent event, User u) {
        Player p = event.getPlayer();
        if (!s.hasBlock(event.getBlock())) {
            return;
        }
        int lvlneeded = s.getLevelNeeded(event.getBlock());
        if (!s.isAllFromFirstLevel() && u.getLevel(s.getSkillName()) < lvlneeded) {
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
            if (SkillsSettings.isDebug()) {
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
