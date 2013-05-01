package nl.lolmen.Skills;

import net.milkbowl.vault.economy.Economy;
import nl.lolmen.API.SkillzLevelEvent;
import nl.lolmen.Skillz.Skillz;
import nl.lolmen.Skillz.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

public class CPU {
    
    public static int levelUps = 0;
    public static int xpUps = 0;

    public static void levelUp(User uu, SkillBase skill, Skillz main) {
        Player p = main.getServer().getPlayer(uu.getUsername());
        int lvl = uu.getLevel(skill.getSkillName()) + 1;
        SkillzLevelEvent event = new SkillzLevelEvent(p, skill, lvl);
        if (event.isCancelled()) {
            return;
        }
        levelUps++;
        uu.addLevel(skill.getSkillName());
        if (SkillsSettings.isBroadcastOnLevelup()) {
            main.getServer().broadcastMessage(
                    ChatColor.RED + p.getDisplayName() + ChatColor.WHITE
                    + " leveled up in " + ChatColor.RED + skill.getSkillName().toLowerCase()
                    + ChatColor.WHITE + " and is now level "
                    + ChatColor.RED + lvl + ChatColor.WHITE + "!");
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', SkillsSettings.getLvlup(skill.getSkillName(), lvl)));
        }
        if (SkillsSettings.isLightningOnLevelup()) {
            p.getWorld().strikeLightningEffect(p.getLocation());
        }
        giveReward(p, skill, lvl);
        giveItem(p, skill, lvl);
        main.high.checkScore(p, skill, lvl);
    }

    private static void giveItem(Player p, SkillBase skill, int lvl) {
        ItemStack get = skill.getItemOnLevelup(lvl);
        if (get == null) {
            return;
        }
        ItemHandler.addItems(p, get);
        p.sendMessage(ChatColor.GREEN + "You have been given " + ChatColor.RED + get.getAmount() + " " + get.getType().name().toLowerCase() + ChatColor.RED + " for leveling up!");
    }

    private static void giveReward(Player p, SkillBase skill, int lvl) {
        if (!SkillsSettings.HasVault()) {
            if (SkillsSettings.isDebug()) {
                System.out.println("[Skillz - Debug] Vault not found!");
            }
            return;
        }
        Economy e;
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            e = economyProvider.getProvider();
            int money = skill.getMoneyOnLevelup(lvl);
            if (money == 0) {
                if (SkillsSettings.isDebug()) {
                    System.out.println("[Skillz - Debug] No money given: 0 -> " + p.getName() + " " + skill.getSkillName() + " " + lvl);
                }
                return;
            }
            e.depositPlayer(p.getName(), money);
            p.sendMessage(ChatColor.GREEN + "You have been given " + e.format(money) + " for leveling up!");
        } else {
            System.out.println("[Skillz] Couldn't give money reward, Vault not found!");
        }
    }

    public static void setLevelWithXP(Player p, SkillBase skill, int level, Skillz main) {
        User u = main.getUserManager().getPlayer(p.getName());
        u.addData(skill.getSkillName(), (int)Math.pow(level - 1, 2) * 10, level);
    }

    public static void checkLeveling(User s, SkillBase skill, Skillz plugin) {
        String skillname = skill.getSkillName();
        if (s.getLevel(skillname) == 0) {
            levelUp(s, skill, plugin);
        } else {
            double result = s.getXP(skillname) / ((s.getLevel(skillname) * s.getLevel(skillname)) * 10);
            if (result >= 1) {
                levelUp(s, skill, plugin);
                checkLeveling(s, skill, plugin);
            }
        }
    }
}
