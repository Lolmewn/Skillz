package nl.lolmen.Skills;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import nl.lolmen.API.SkillzXPGainEvent;
import nl.lolmen.Skillz.Skillz;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SkillBase {

	private String skillName;
	private String itemOnLevelup;
	private int moneyOnLevelup;
	private int multiplier;
	private boolean enabled;
	
	private HashMap<Integer, String> every_many_levels = new HashMap<Integer, String>();
	private HashMap<Integer, Integer> every_many_levels_money = new HashMap<Integer, Integer>();
	private HashMap<Integer, String> fixed_levels = new HashMap<Integer, String>();
	private HashMap<Integer, Integer> fixed_levels_money = new HashMap<Integer, Integer>();
	File folder = new File("plugins" + File.separator + "Skillz" + File.separator + "players" + File.separator);

	public void addXP(final Player p, final int XP) {
		final SkillBase skill = this;
		int xp;
		int lvl;
		SkillzXPGainEvent event = new SkillzXPGainEvent(p, skill, XP);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(event.isCancelled()){
			return;
		}
		try {
			if(!new File(folder, p.getName().toLowerCase() + ".txt").exists()){
				new File(folder, p.getName().toLowerCase() + ".txt").createNewFile();
				Skillz.p.log.info("New file created for " + p.getName());
			}
			String skillname = skill.getSkillName().toLowerCase();
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(new File(folder, p.getName().toLowerCase() + ".txt"));
			prop.load(in);
			if (!prop.containsKey(skillname)) {
				System.out.println("Skill " + skillname + " not found, adding!");
				prop.put(skillname, "0;0");
				prop.store(new FileOutputStream(new File(folder, p
						.getName().toLowerCase() + ".txt")),
						"Skill=XP;lvl");
			}
			String get = prop.getProperty(skillname);
			String[] array = get.split(";");
			xp = Integer.parseInt(array[0]);
			lvl = Integer.parseInt(array[1]);
			prop.setProperty(skillname,Integer.toString(xp + XP) + ";"+ array[1]);
			FileOutputStream out = new FileOutputStream(new File(
					folder, p.getName().toLowerCase() + ".txt"));
			prop.store(out, "Skill=XP;lvl");
			CPU.checkLeveling(p, skill, lvl, (xp + XP));
			in.close();
			out.flush();
			out.close();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public String getSkillName() {
		return skillName.toLowerCase();
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public ItemStack getItemOnLevelup(int level) {
		String obv = itemOnLevelup;
		for(int i: every_many_levels.keySet()){
			double calc = (double)level / (double)i;
			if(getDecimal(calc) == 0){
				if(SkillsSettings.isDebug()){
					System.out.println("Found match (item) -> " + level + " " + i + "  with decimal " + getDecimal(calc) + " and calc " + calc);
				}
				obv = every_many_levels.get(i);
			}
		}
		if(fixed_levels.containsKey(level)){
			obv = fixed_levels.get(level).toLowerCase();
		}
		if(obv == null){
			return null;
		}
		if(SkillsSettings.isDebug()){
			System.out.println("Splitting " + obv);
		}
		String[] arg = obv.split(",");
		if(arg[0].equals("0") || arg[1].equals("0")){
			return null;
		}
		ItemStack stack = new ItemStack(Integer.parseInt(arg[0]), Integer.parseInt(arg[1]));
		return stack;
	}
	
	public int getDecimal(double d){
		int multiplier = (int) Math.pow(10, 3);
	    long result = ((long) (d * multiplier)) - (((long) d) * multiplier);
	    return (int)result;
	}

	public void setItemOnLevelup(String itemOnLevelup) {
		this.itemOnLevelup = itemOnLevelup;
	}

	public int getMoneyOnLevelup(int level) {
		if(fixed_levels_money.containsKey(level)){
			return fixed_levels_money.get(level);
		}
		int money = moneyOnLevelup;
		for(int i: every_many_levels_money.keySet()){
			double calc = (double)level / (double)i;
			if(getDecimal(calc) == 0){
				if(SkillsSettings.isDebug()){
					System.out.println("Found match (money) -> " + level + " " + i + "  with decimal " + getDecimal(calc) + " and calc " + calc);
				}
				money = every_many_levels_money.get(i);
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
	
	public void add_to_every_many_levels(int level, String det){
		if(SkillsSettings.isDebug()){
			System.out.println("[Skillz] many: Starting to add " + det + " to " + this.getSkillName() + " " + level);
		}
		if(det.contains(":")){
			String[] both = det.split(":");
			for(String s: both){
				if(s.contains("ITEM;")){
					String[] item = s.split(";");
					if(item[1].contains(",")){
						every_many_levels.put(level, item[1]);
						if(SkillsSettings.isDebug()){
							System.out.println("Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
						}
					}
				}
				if(s.contains("MONEY;")){
					String[] item = s.split(";");
					every_many_levels.put(level, item[1]);
					if(SkillsSettings.isDebug()){
						System.out.println("Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
					}
					
					
				}
			}
			return;
		}else if(det.contains("ITEM;")){
			String[] item = det.split(";");
			if(item[1].contains(",")){
				every_many_levels.put(level, item[1]);
				if(SkillsSettings.isDebug()){
					System.out.println("Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
				}
			}
			return;
		}else if(det.contains("MONEY;")){
			String[] m = det.split(";");
			try{
				every_many_levels_money.put(level, Integer.parseInt(m[1]));
				if(SkillsSettings.isDebug()){
					System.out.println("Added " + m[1] + " for lvl " + level + " " + this.getSkillName());
				}
			}catch(Exception e){
				System.out.println("[Skillz] ERROR you have an error in the config, " + m[1] + " could not be converted to int at every_many_levels in " + this.getSkillName() + "!");
			}
			return;
		}
		System.out.println("[Skillz] ERROR when trying to add every_many_levels: lvl " + level + " in " + this.getSkillName() + ", " + det + " is not valid");
	}
	
	public void add_to_fixed_levels(int level, String det){
		if(SkillsSettings.isDebug()){
			System.out.println("[Skillz] fixed: Starting to add " + det + " to " + this.getSkillName() + " " + level);
		}
		if(det.contains(":")){
			//Both money and Item
			String[] both = det.split(":");
			for(String s: both){
				if(s.contains("ITEM;")){
					String[] item = s.split(";");
					if(item[1].contains(",")){
						fixed_levels.put(level, item[1]);
						if(SkillsSettings.isDebug()){
							System.out.println("Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
						}
					}
				}
				if(s.contains("MONEY;")){
					String[] item = s.split(";");
					fixed_levels.put(level, item[1]);
					if(SkillsSettings.isDebug()){
						System.out.println("Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
					}
					
					
				}
			}
			return;
		}else if(det.contains("ITEM;")){
			String[] item = det.split(";");
			if(item[1].contains(",")){
				fixed_levels.put(level, item[1]);
				if(SkillsSettings.isDebug()){
					System.out.println("Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
				}
			}
			return;
		}else if(det.contains("MONEY;")){
			String[] m = det.split(";");
			try{
				fixed_levels_money.put(level, Integer.parseInt(m[1]));
				if(SkillsSettings.isDebug()){
					System.out.println("Added " + m[1] + " for lvl " + level + " " + this.getSkillName());
				}
			}catch(Exception e){
				System.out.println("[Skillz] ERROR you have an error in the config, " + m[1] + " could not be converted to int at fixed_levels in " + this.getSkillName() + "!");
			}
			return;
		}
		System.out.println("[Skillz] ERROR when trying to add fixed_levels: lvl " + level + " in " + this.getSkillName() + ", " + det + " is not valid");
	}

}