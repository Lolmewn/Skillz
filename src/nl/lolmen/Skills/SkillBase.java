package nl.lolmen.Skills;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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
	File folder = new File("plugins" + File.separator + "Skillz" + File.separator + "players" + File.separator);
	
	public void addXP(final Player p, final int XP) {
		final SkillBase skill = this;
		Bukkit.getScheduler().scheduleAsyncDelayedTask(Skillz.p, new Runnable() {
			@Override
			public void run() {
				int xp;
				int lvl;
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
					//System.out.println("Debug[1]");
					String get = prop.getProperty(skillname);
					//System.out.println("Got: " + get);
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

		}, 2L);
	}

	public String getSkillName() {
		return skillName.toLowerCase();
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public ItemStack getItemOnLevelup() {
		String[] arg = itemOnLevelup.split(",");
		ItemStack stack = new ItemStack(Integer.parseInt(arg[0]), Integer.parseInt(arg[1]));
		return stack;
	}

	public void setItemOnLevelup(String itemOnLevelup) {
		this.itemOnLevelup = itemOnLevelup;
	}

	public int getMoneyOnLevelup() {
		return moneyOnLevelup;
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

}