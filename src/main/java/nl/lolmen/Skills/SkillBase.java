package nl.lolmen.Skills;

import java.io.File;
import java.util.HashMap;
import nl.lolmen.API.SkillzXPGainEvent;
import nl.lolmen.Skillz.Skillz;
import nl.lolmen.Skillz.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SkillBase {
    
    private String skillName;
    private String itemOnLevelup;
    private int moneyOnLevelup;
    private int multiplier;
    private boolean enabled;
    private Skillz plugin;
    private HashMap<Integer, String> every_many_levels = new HashMap<Integer, String>();
    private HashMap<Integer, Integer> every_many_levels_money = new HashMap<Integer, Integer>();
    private HashMap<Integer, String> fixed_levels = new HashMap<Integer, String>();
    private HashMap<Integer, Integer> fixed_levels_money = new HashMap<Integer, Integer>();
    File folder = new File("plugins" + File.separator + "Skillz" + File.separator + "players" + File.separator);

    public SkillBase(Skillz plugin) {
        this.plugin = plugin;
    }

    public void addXP(final Player p, final int XP) {
        if (SkillsSettings.isDebug()) {
            System.out.println("[Skillz - Debug] Adding " + XP + " xp to " + this.getSkillName() + " from " + p.getDisplayName());
        }
        if (SkillsSettings.isUsePerSkillPerms()) {
            if (SkillsSettings.isDebug()) {
                System.out.println("[Skillz - Debug] Using per-Skill-Perms");
            }
            if (!p.hasPermission("skillz.skill." + this.getSkillName())) {
                if (SkillsSettings.isDebug()) {
                    System.out.println(p.getDisplayName() + " doesn't have perms to gain XP");
                }
                return;
            }
        }
        SkillzXPGainEvent event = new SkillzXPGainEvent(p, this, XP);
        this.plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        CPU.xpUps += XP;
        User s = this.plugin.getUserManager().getPlayer(p.getName());
        s.addXP(this.getSkillName(), XP);
        CPU.checkLeveling(s, this, this.plugin);
    }

    public String getSkillName() {
        return skillName.toLowerCase();
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public ItemStack getItemOnLevelup(int level) {
        String obv = this.itemOnLevelup;
        for (int i : this.every_many_levels.keySet()) {
            if (level%i==0) {
                if (SkillsSettings.isDebug()) {
                    System.out.println("[Skillz - Debug] Found match (item) -> " + level + " " + i);
                }
                obv = this.every_many_levels.get(i);
            }
        }
        if (this.fixed_levels.containsKey(level)) {
            obv = this.fixed_levels.get(level).toLowerCase();
        }
        if (obv == null) {
            return null;
        }
        if(!obv.contains(",")){
            return null;
        }
        if (SkillsSettings.isDebug()) {
            System.out.println("[Skillz - Debug] Splitting " + obv);
        }
        String[] arg = obv.split(",");
        if (arg[0].equals("0") || arg[1].equals("0")) {
            return null;
        }
        return new ItemStack(Integer.parseInt(arg[0]), Integer.parseInt(arg[1]));
    }

    public void setItemOnLevelup(String itemOnLevelup) {
        this.itemOnLevelup = itemOnLevelup;
    }

    public int getMoneyOnLevelup(int level) {
        if (this.fixed_levels_money.containsKey(level)) {
            return this.fixed_levels_money.get(level);
        }
        int money = this.moneyOnLevelup;
        for (int i : this.every_many_levels_money.keySet()) {
            if (level%i == 0) {
                if (SkillsSettings.isDebug()) {
                    System.out.println("[Skillz - Debug] Found match (money) -> " + level + " " + i);
                }
                money = this.every_many_levels_money.get(i);
            }
        }
        return money;
    }

    public void setMoneyOnLevelup(int moneyOnLevelup) {
        this.moneyOnLevelup = moneyOnLevelup;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void add_to_every_many_levels(int level, String det) {
        if (SkillsSettings.isDebug()) {
            System.out.println("[Skillz - Debug] many: Starting to add " + det + " to " + this.getSkillName() + " " + level);
        }
        if (det.contains(":")) {
            String[] both = det.split(":");
            for (String s : both) {
                if (s.contains("ITEM;")) {
                    String[] item = s.split(";");
                    if (item[1].contains(",")) {
                        this.every_many_levels.put(level, item[1]);
                        if (SkillsSettings.isDebug()) {
                            System.out.println("[Skillz - Debug] Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
                        }
                    }
                }
                if (s.contains("MONEY;")) {
                    String[] item = s.split(";");
                    this.every_many_levels_money.put(level, Integer.parseInt(item[1]));
                    if (SkillsSettings.isDebug()) {
                        System.out.println("[Skillz - Debug] Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
                    }


                }
            }
            return;
        } else if (det.contains("ITEM;")) {
            String[] item = det.split(";");
            if (item[1].contains(",")) {
                this.every_many_levels.put(level, item[1]);
                if (SkillsSettings.isDebug()) {
                    System.out.println("[Skillz - Debug] Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
                }
            }
            return;
        } else if (det.contains("MONEY;")) {
            String[] m = det.split(";");
            try {
                this.every_many_levels_money.put(level, Integer.parseInt(m[1]));
                if (SkillsSettings.isDebug()) {
                    System.out.println("[Skillz - Debug] Added " + m[1] + " for lvl " + level + " " + this.getSkillName());
                }
            } catch (Exception e) {
                System.out.println("[Skillz] ERROR you have an error in the config, " + m[1] + " could not be converted to int at every_many_levels in " + this.getSkillName() + "!");
            }
            return;
        }
        System.out.println("[Skillz] ERROR when trying to add every_many_levels: lvl " + level + " in " + this.getSkillName() + ", " + det + " is not valid");
    }

    public void add_to_fixed_levels(int level, String det) {
        if (SkillsSettings.isDebug()) {
            System.out.println("[Skillz - Debug] fixed: Starting to add " + det + " to " + this.getSkillName() + " " + level);
        }
        if (det.contains(":")) {
            //Both money and Item
            String[] both = det.split(":");
            for (String s : both) {
                if (s.contains("ITEM;")) {
                    String[] item = s.split(";");
                    if (item[1].contains(",")) {
                        this.fixed_levels.put(level, item[1]);
                        if (SkillsSettings.isDebug()) {
                            System.out.println("[Skillz - Debug] Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
                        }
                    }
                }
                if (s.contains("MONEY;")) {
                    String[] item = s.split(";");
                    this.fixed_levels_money.put(level, Integer.parseInt(item[1]));
                    if (SkillsSettings.isDebug()) {
                        System.out.println("[Skillz - Debug] Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
                    }


                }
            }
            return;
        } else if (det.contains("ITEM;")) {
            String[] item = det.split(";");
            if (item[1].contains(",")) {
                this.fixed_levels.put(level, item[1]);
                if (SkillsSettings.isDebug()) {
                    System.out.println("[Skillz - Debug] Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
                }
            }
            return;
        } else if (det.contains("MONEY;")) {
            String[] m = det.split(";");
            try {
                this.fixed_levels_money.put(level, Integer.parseInt(m[1]));
                if (SkillsSettings.isDebug()) {
                    System.out.println("[Skillz - Debug] Added " + m[1] + " for lvl " + level + " " + this.getSkillName());
                }
            } catch (Exception e) {
                System.out.println("[Skillz] ERROR you have an error in the config, " + m[1] + " could not be converted to int at fixed_levels in " + this.getSkillName() + "!");
            }
            return;
        }
        System.out.println("[Skillz] ERROR when trying to add fixed_levels: lvl " + level + " in " + this.getSkillName() + ", " + det + " is not valid");
    }
}