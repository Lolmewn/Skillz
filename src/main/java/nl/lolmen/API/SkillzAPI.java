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
}
