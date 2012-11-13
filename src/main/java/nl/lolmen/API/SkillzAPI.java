package nl.lolmen.API;

import nl.lolmen.Skills.SkillBase;
import nl.lolmen.Skillz.Skillz;
import org.bukkit.entity.Player;

public class SkillzAPI {

    private static Skillz plugin;

    public SkillzAPI(Skillz main) {
        plugin = main;
    }

    public SkillzAPI() {
    }

    public SkillzSettings getSettings() {
        return new SkillzSettings();
    }

    public boolean hasSkill(String name) {
        return plugin.getSkillManager().skills.containsKey(name);
    }

    public SkillBase getSkill(String name) {
        return plugin.getSkillManager().skills.get(name);
    }

    public void removeSkill(String name) {
        plugin.getSkillManager().skills.remove(name);
    }

    public void addXP(Player player, SkillBase skill, int amount) {
        skill.addXP(player, amount);
    }

    public int getLevel(Player player, SkillBase skill) {
        return plugin.getUserManager().getPlayer(player.getName()).getLevel(skill.getSkillName());
    }

    public void addXP(Player p, String skills) {
        addXP(p,skills,1);
    }

    public void addXP(Player p, String skills, int amount) {
        SkillBase s = plugin.getSkillManager().skills.get(skills);
        if(s == null){
            return;
        }
        s.addXP(p, amount);
    }

    public int getLevel(Player p, String skills) {
        SkillBase s = plugin.getSkillManager().skills.get(skills);
        if(s == null){
            return -1;
        }
        return this.getLevel(p, s);
    }
}
