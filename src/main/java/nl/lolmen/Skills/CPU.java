package nl.lolmen.Skills;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

import net.milkbowl.vault.economy.Economy;
import nl.lolmen.API.SkillzLevelEvent;
import nl.lolmen.Skillz.Skillz;
import nl.lolmen.database.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

public class CPU {

	static File folder = new File("plugins" + File.separator + "Skillz" + File.separator + "players" + File.separator);
	static HashSet<String> list = new HashSet<String>();

	public static void addXP(final Player p, SkillBase skilled, final int XP) {
		if(!p.hasPermission("skillz.skill." + skilled.getSkillName()) && SkillsSettings.isUsePerSkillPerms()){
			return;
		}
		skilled.addXP(p, XP);
	}

	public static void checkLeveling(Player p, SkillBase skill, int lvl,
			int newXP) {
		if (lvl == 0) {
			levelUp(p, skill, lvl + 1);
		} else {
			double result = newXP / ((lvl * lvl) * 10);
			if (result >= 1) { 
				levelUp(p, skill, lvl + 1);
				checkLeveling(p, skill, lvl + 1, newXP);
			}
		}
	}

	public static void levelUp(Player p, SkillBase skill, int lvl) {
		SkillzLevelEvent event = new SkillzLevelEvent(p, skill, lvl);
		if(event.isCancelled()){
			return;
		}
		Properties prop = new Properties();
		try {
			FileInputStream in = new FileInputStream(new File(folder, p
					.getName().toLowerCase() + ".txt"));
			prop.load(in);
			String get = prop.getProperty(skill.getSkillName());
			String[] array = get.split(";");
			int xp = Integer.parseInt(array[0]);
			String back = Integer.toString(xp) + ";" + Integer.toString(lvl);
			prop.setProperty(skill.getSkillName(), back);
			FileOutputStream out = new FileOutputStream(new File(folder, p
					.getName().toLowerCase() + ".txt"));
			prop.store(out, "Skill=XP;lvl");
			in.close();
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (SkillsSettings.isBroadcastOnLevelup()) {
			Bukkit.getServer().broadcastMessage(
					ChatColor.RED + p.getDisplayName() + ChatColor.WHITE
							+ " leveled up in " + ChatColor.RED + skill.getSkillName().toLowerCase()
							+ ChatColor.WHITE + " and is now level "
							+ ChatColor.RED + lvl + ChatColor.WHITE + "!");
		} else {
			p.sendMessage(SkillsSettings.getLvlup(skill.getSkillName(), lvl));
		}
		if (SkillsSettings.isLightningOnLevelup()) {
			p.getWorld().strikeLightningEffect(p.getLocation());
		}
		giveReward(p, skill, lvl);
		giveItem(p, skill, lvl);
		Skillz.high.checkScore(p, skill, lvl); 
	}

	private static void giveItem(Player p, SkillBase skill, int lvl) {
		ItemStack get = skill.getItemOnLevelup(lvl);
		if(get == null){
			return;
		}
		ItemHandler.addItems(p, get);
		p.sendMessage("You have been given " + ChatColor.RED + get.getAmount() + " " + get.getType().name().toLowerCase() + ChatColor.RED + " for leveling up!");
	}

	private static void giveReward(Player p, SkillBase skill,int lvl) {
		if(!SkillsSettings.HasVault()){
			if(SkillsSettings.isDebug()){
				System.out.println("Vault not found!");
			}
			return;
		}
		Economy e;
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			e = economyProvider.getProvider();
			int money = skill.getMoneyOnLevelup(lvl);
			if(money == 0){
				if(SkillsSettings.isDebug()){
					System.out.println("[Skillz - Debug] No money given: 0 -> " + p.getName() + " " + skill.getSkillName() + " " + lvl);
				}
				return;
			}
			e.depositPlayer(p.getName(), money);
			p.sendMessage("You have been given " + e.format(money) + " for leveling up!");
		} else {
			System.out.println("[Skillz] Couldn't give money reward, Vault not found!");
		}
	}

	public static int getLevel(Player p, SkillBase skill) {
		Properties prop = new Properties();
		try {
			File f = new File(folder, p.getName().toLowerCase() + ".txt");
			if(!f.exists()){
				return 0;
			}
			FileInputStream in = new FileInputStream(new File(folder, p
					.getName().toLowerCase() + ".txt"));
			prop.load(in);
			String key = skill.getSkillName();
			if (!prop.containsKey(key)) {
				return 0;
			}
			String get = prop.getProperty(key);
			in.close();
			String[] dit = get.split(";");
			return Integer.parseInt(dit[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void setLevelWithXP(Player p, SkillBase skill, int level) {
		Properties prop = new Properties();
		try {
			FileInputStream in = new FileInputStream(new File(folder, p
					.getName().toLowerCase() + ".txt"));
			prop.load(in);
			String key = skill.getSkillName();
			if (!prop.containsKey(key)) {
				return;
			}
			prop.setProperty(key, (level - 1) * (level - 1) * 10 + ";" + level);
			FileOutputStream out = new FileOutputStream(new File(folder, p
					.getName().toLowerCase() + ".txt"));
			prop.store(out, "Skill=XP;lvl");
			in.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
