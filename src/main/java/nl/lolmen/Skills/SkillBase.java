package nl.lolmen.Skills;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import nl.lolmen.API.SkillzXPGainEvent;
import nl.lolmen.Skillz.Skillz;

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
	
	public SkillBase(Skillz plugin){
		this.plugin = plugin;
	}

	public void addXP(final Player p, final int XP) {
		if(SkillsSettings.isDebug()){
			System.out.println("[Skillz - Debug] Adding " + XP + " xp to " + this.getSkillName() + " from " + p.getDisplayName());
		}
		if(SkillsSettings.isUsePerSkillPerms()){
			if(SkillsSettings.isDebug()){
				System.out.println("[Skillz - Debug] Using per-Skill-Perms");
			}
			if(!p.hasPermission("skills." + this.getSkillName())){
				if(SkillsSettings.isDebug()){
					System.out.println(p.getDisplayName() + " doesn't have perms to gain XP");
				}
				return;
			}
		}
		final SkillBase skill = this;
		int xp = 0;
		int lvl = 0;
		SkillzXPGainEvent event = new SkillzXPGainEvent(p, skill, XP);
		this.plugin.getServer().getPluginManager().callEvent(event);
		if(event.isCancelled()){
			return;
		}
		CPU.xpUps += XP;
		if(!this.plugin.useMySQL){
			try {
				if(!new File(folder, p.getName().toLowerCase() + ".txt").exists()){
					new File(folder, p.getName().toLowerCase() + ".txt").createNewFile();
					System.out.println("[Skillz] New file created for " + p.getName());
				}
				String skillname = skill.getSkillName().toLowerCase();
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(new File(folder, p.getName().toLowerCase() + ".txt"));
				prop.load(in);
				if (!prop.containsKey(skillname)) {
					System.out.println("[Skillz] Skill " + skillname + " not found, adding!");
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
				in.close();
				out.flush();
				out.close();
				CPU.checkLeveling(p, skill, lvl, (xp + XP), this.plugin);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			this.plugin.getMySQL().executeStatement("UPDATE " + this.plugin.dbTable + " SET xp=xp+" + XP + " WHERE player='" + p.getName() + "' AND skill='" + this.getSkillName() + "' LIMIT 1");
			CPU.checkLeveling(p, skill, CPU.getLevel(p, skill, this.plugin), CPU.getXP(p, skill, this.plugin), this.plugin);
		}
	}


	public String getSkillName() {
		return skillName.toLowerCase();
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public ItemStack getItemOnLevelup(int level) {
		String obv = this.itemOnLevelup;
		for(int i : this.every_many_levels.keySet()){
			double calc = (double)level / (double)i;
			if(getDecimal(calc) == 0){
				if(SkillsSettings.isDebug()){
					System.out.println("[Skillz - Debug] Found match (item) -> " + level + " " + i + "  with decimal " + getDecimal(calc) + " and calc " + calc);
				}
				obv = this.every_many_levels.get(i);
			}
		}
		if(this.fixed_levels.containsKey(level)){
			obv = this.fixed_levels.get(level).toLowerCase();
		}
		if(obv == null){
			return null;
		}
		if(SkillsSettings.isDebug()){
			System.out.println("[Skillz - Debug] Splitting " + obv);
		}
		String[] arg = obv.split(",");
		if(arg[0].equals("0") || arg[1].equals("0")){
			return null;
		}
		return new ItemStack(Integer.parseInt(arg[0]), Integer.parseInt(arg[1]));
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
		if(this.fixed_levels_money.containsKey(level)){
			return this.fixed_levels_money.get(level);
		}
		int money = this.moneyOnLevelup;
		for(int i: this.every_many_levels_money.keySet()){
			double calc = (double)level / (double)i;
			if(getDecimal(calc) == 0){
				if(SkillsSettings.isDebug()){
					System.out.println("[Skillz - Debug] Found match (money) -> " + level + " " + i + "  with decimal " + getDecimal(calc) + " and calc " + calc);
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
	
	public void add_to_every_many_levels(int level, String det){
		if(SkillsSettings.isDebug()){
			System.out.println("[Skillz - Debug] many: Starting to add " + det + " to " + this.getSkillName() + " " + level);
		}
		if(det.contains(":")){
			String[] both = det.split(":");
			for(String s: both){
				if(s.contains("ITEM;")){
					String[] item = s.split(";");
					if(item[1].contains(",")){
						this.every_many_levels.put(level, item[1]);
						if(SkillsSettings.isDebug()){
							System.out.println("[Skillz - Debug] Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
						}
					}
				}
				if(s.contains("MONEY;")){
					String[] item = s.split(";");
					this.every_many_levels_money.put(level, Integer.parseInt(item[1]));
					if(SkillsSettings.isDebug()){
						System.out.println("[Skillz - Debug] Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
					}
					
					
				}
			}
			return;
		}else if(det.contains("ITEM;")){
			String[] item = det.split(";");
			if(item[1].contains(",")){
				this.every_many_levels.put(level, item[1]);
				if(SkillsSettings.isDebug()){
					System.out.println("[Skillz - Debug] Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
				}
			}
			return;
		}else if(det.contains("MONEY;")){
			String[] m = det.split(";");
			try{
				this.every_many_levels_money.put(level, Integer.parseInt(m[1]));
				if(SkillsSettings.isDebug()){
					System.out.println("[Skillz - Debug] Added " + m[1] + " for lvl " + level + " " + this.getSkillName());
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
			System.out.println("[Skillz - Debug] fixed: Starting to add " + det + " to " + this.getSkillName() + " " + level);
		}
		if(det.contains(":")){
			//Both money and Item
			String[] both = det.split(":");
			for(String s: both){
				if(s.contains("ITEM;")){
					String[] item = s.split(";");
					if(item[1].contains(",")){
						this.fixed_levels.put(level, item[1]);
						if(SkillsSettings.isDebug()){
							System.out.println("[Skillz - Debug] Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
						}
					}
				}
				if(s.contains("MONEY;")){
					String[] item = s.split(";");
					this.fixed_levels_money.put(level, Integer.parseInt(item[1]));
					if(SkillsSettings.isDebug()){
						System.out.println("[Skillz - Debug] Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
					}
					
					
				}
			}
			return;
		}else if(det.contains("ITEM;")){
			String[] item = det.split(";");
			if(item[1].contains(",")){
				this.fixed_levels.put(level, item[1]);
				if(SkillsSettings.isDebug()){
					System.out.println("[Skillz - Debug] Added " + item[1] + " for lvl " + level + " " + this.getSkillName());
				}
			}
			return;
		}else if(det.contains("MONEY;")){
			String[] m = det.split(";");
			try{
				this.fixed_levels_money.put(level, Integer.parseInt(m[1]));
				if(SkillsSettings.isDebug()){
					System.out.println("[Skillz - Debug] Added " + m[1] + " for lvl " + level + " " + this.getSkillName());
				}
			}catch(Exception e){
				System.out.println("[Skillz] ERROR you have an error in the config, " + m[1] + " could not be converted to int at fixed_levels in " + this.getSkillName() + "!");
			}
			return;
		}
		System.out.println("[Skillz] ERROR when trying to add fixed_levels: lvl " + level + " in " + this.getSkillName() + ", " + det + " is not valid");
	}

}