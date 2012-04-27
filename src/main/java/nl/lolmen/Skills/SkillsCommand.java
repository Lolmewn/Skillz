package nl.lolmen.Skills;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            count++;
        }
        /*
        if (this.plugin.useMySQL) {
            ResultSet set = this.plugin.getMySQL().executeQuery("SELECT * FROM " + this.plugin.dbTable + " WHERE player='" + p + "' ORDER BY skill DESC");
            if (set == null) {
                System.out.println("[Skillz] Something went wrong while reading the MySQL database.");
                return;
            }
            try {
                while (set.next()) {
                    String skill = set.getString("skill").toLowerCase();
                    data.put(count, new SkillData(skill, set.getInt("xp"), set.getInt("level"), (int) Math.pow(set.getInt("level"), 2) * 10 - set.getInt("xp")));
                    totalXP += set.getInt("xp");
                    totalLVL += set.getInt("level");
                    if (SkillsSettings.isDebug()) {
                        System.out.println("[Skillz - Debug] Added " + skill + " to data set");
                    }
                    count++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                File f = new File("plugins" + File.separator + "Skillz" + File.separator + "players" + File.separator + p.toLowerCase() + ".txt");
                if (!f.exists()) {
                    this.sender.sendMessage("Something went wrong while trying to fetch your personal file!");
                    this.sender.sendMessage("Tell an admin to take a look at his server.log , he'll know what to do ;)");
                    System.out.println("[Skillz] File for player " + this.p + " not found at " + f.getAbsolutePath());
                    return;
                }
                DataInputStream dis;
                BufferedReader br;
                try {
                    FileInputStream in = new FileInputStream(f);
                    dis = new DataInputStream(in);
                    br = new BufferedReader(new InputStreamReader(dis));
                    String strLine;
                    while ((strLine = br.readLine()) != null) {
                        if (strLine.contains("#")) {
                            continue;
                        }
                        if (!strLine.contains("=") || !strLine.contains(";")) {
                            System.out.println("[Skillz] Don't know what to do with '" + strLine + "' in " + f.getAbsolutePath());
                            continue;
                        }
                        String[] first = strLine.split("=");
                        String skill = first[0];
                        String[] second = first[1].split(";");
                        int xp = Integer.parseInt(second[0]);
                        int lvl = Integer.parseInt(second[1]);
                        int remaining = ((lvl) * (lvl) * 10) - xp;
                        totalXP += xp;
                        totalLVL += lvl;
                        data.put(count, new SkillData(skill, xp, lvl, remaining));
                        count++;
                    }
                    dis.close();
                    br.close();
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        if (!(data.size() > this.page * 8 - 8)) {
            this.sender.sendMessage(ChatColor.RED + "There is no page " + this.page + "!");
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
