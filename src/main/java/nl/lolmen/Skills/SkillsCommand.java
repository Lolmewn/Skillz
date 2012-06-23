package nl.lolmen.Skills;

import java.util.HashMap;
import java.util.Map;
import nl.lolmen.Skillz.Skillz;
import nl.lolmen.Skillz.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillsCommand {

    public void sendSkills(Player p, Skillz plugin) {
        this.sendSkills(p, 1, plugin);
    }

    public void sendSkills(Player p, int page, Skillz plugin) {
        this.sendSkills(p, p.getName(), page, plugin);
    }

    public void sendSkills(CommandSender sender, Player p, Skillz plugin) {
        this.sendSkills(sender, p, 1, plugin);
    }

    public void sendSkills(CommandSender sender, String p, Skillz plugin) {
        this.sendSkills(sender, p, 1, plugin);
    }

    public void sendSkills(CommandSender sender, Player p, int page, Skillz plugin) {
        this.sendSkills(sender, p.getName(), page, plugin);
    }

    public void sendSkills(CommandSender sender, String p, int page, Skillz plugin) {
        new getSkills(sender, p, page, plugin);
    }
}

class getSkills extends Thread {

    private String p;
    private int page;
    private CommandSender sender;
    private Skillz plugin;

    @Override
    public void run() {
        Map<Integer, SkillData> data = new HashMap<Integer, SkillData>();
        if (SkillsSettings.isDebug()) {
            if (this.sender instanceof Player) {
                System.out.println("[Skillz - Debug] Fetching file from " + p + " to " + sender.getName());
            }
        }
        int count = 0, totalXP = 0, totalLVL = 0;
        User u = this.plugin.getUserManager().getPlayer(p);
        for(String skill : u.getSkills()){
            data.put(count, new SkillData(skill, u.getXP(skill), u.getLevel(skill), (int)Math.pow(u.getLevel(skill), 2) * 10 - u.getXP(skill)));
            totalXP += u.getXP(skill);
            totalLVL += u.getLevel(skill);
            count++; //Otherwise data gets overwritten
        }
        if (!(data.size() > this.page * 8 - 8)) {
            this.sender.sendMessage(ChatColor.RED + "There is no page " + this.page + " for " + u.getUsername() + "!");
            return;
        }
        int sent = 0;
        int getCounter = (this.page - 1) * 8;
        while (sent != 8) {
            if (data.containsKey(getCounter)) {
                SkillData d = data.get(getCounter);
                if ((this.plugin.getSkillManager().skills.containsKey(d.getSkill().toLowerCase()) && this.plugin.getSkillManager().skills.get(d.getSkill().toLowerCase()).isEnabled())
                        || (this.plugin.getCustomSkillManager().getSkill(d.getSkill().toLowerCase()) != null && this.plugin.getCustomSkillManager().getSkill(d.getSkill().toLowerCase()).isEnabled())) {
                    double percent = 100 - (d.getRem() / (Math.pow(d.getLVL(), 2) * 10 - Math.pow(d.getLVL() - 1, 2) * 10) * 100);
                    int stripes = (int) percent / (100 / 20); //Draws the red stripes
                    if (d.getLVL() == 0) {
                        stripes = 0;
                    }
                    if (SkillsSettings.isDebug()) {
                        System.out.println("[Skillz - Debug] Percent: " + percent + " stripes: " + stripes);
                    }
                    StringBuilder str = new StringBuilder();
                    str.append(ChatColor.WHITE).append("[");
                    for (int b = 0; b < stripes; b++) {
                        str.append(ChatColor.GREEN).append("|");
                    }
                    for (int a = 0; a < 20 - stripes; a++) {
                        str.append(ChatColor.RED).append("|");
                    }
                    str.append(ChatColor.WHITE).append("]");
                    String skill = d.getSkill();
                    if (skill.startsWith("axes") || skill.startsWith("swords") || skill.startsWith("unarmed")) {
                        skill += " Combat";
                    }
                    this.sender.sendMessage(ChatColor.RED + skill + ChatColor.WHITE + " Level: " + ChatColor.GREEN + d.getLVL() + ChatColor.WHITE + " XP: " + ChatColor.GREEN + d.getXP() + " " + str.toString());
                    sent++;
                } else {
                    if (SkillsSettings.isDebug()) {
                        this.plugin.getLogger().info("[Debug] Not showing disabled skill " + d.getSkill());
                    }
                    //The skill is not enabled, or doesn't exist in the managers.
                }
            } else {
                if (SkillsSettings.isDebug()) {
                    this.plugin.getLogger().info("[Debug] No value: " + getCounter + ", breaking");
                }
                break;
            }
            getCounter++;
        }
        this.sender.sendMessage(ChatColor.RED + "Total Level: " + ChatColor.GREEN + totalLVL + ChatColor.RED + " Total XP: " + ChatColor.GREEN + totalXP);
    }

    public getSkills(CommandSender sender, Player p, int page, Skillz plugin) {
        this(sender, p.getName(), page, plugin);
    }

    public getSkills(CommandSender sender, String name, int page, Skillz plugin) {
        this.page = page;
        this.sender = sender;
        this.plugin = plugin;
        this.p = name;
        this.start();
    }
}

class SkillData {

    private String s;
    private int x, l, r;

    public SkillData(String skill, int xp, int lvl, int rem) {
        this.setS(skill);
        this.setX(xp);
        this.setL(lvl);
        this.setR(rem);
    }

    public String getSkill() {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private void setS(String s) {
        this.s = s;
    }

    public int getXP() {
        return x;
    }

    private void setX(int x) {
        this.x = x;
    }

    public int getLVL() {
        return l;
    }

    private void setL(int l) {
        this.l = l;
    }

    public int getRem() {
        return r;
    }

    private void setR(int r) {
        this.r = r;
    }
}
