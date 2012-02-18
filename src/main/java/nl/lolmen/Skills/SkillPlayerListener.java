package nl.lolmen.Skills;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import nl.lolmen.Skillz.Skillz;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class SkillPlayerListener implements Listener {
	private Skillz plugin;
	public SkillPlayerListener(Skillz main){
		plugin = main;
	}	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		double time = System.nanoTime();
		Player p = event.getPlayer();
		if ((!plugin.useSQL) && (!plugin.useMySQL) && 
				(!new File(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt").exists())) {
			try {
				plugin.log.info("[Skillz] File created for " + p.getName().toLowerCase() + "!");
				new File(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt").createNewFile();
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(new File(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt"));
				prop.load(in);
				for(SkillBase skill: plugin.skillManager.getSkills()){
					prop.put(skill.getSkillName(), "0;0");
				}
				FileOutputStream out = new FileOutputStream(new File(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt"));
				prop.store(out, "Skill=XP;lvl");
				in.close();
				out.flush();
				out.close();
				double time2 = System.nanoTime();
				double taken = (time2 - time) / 1000000.0D;
				plugin.log.info("Creation took " + Double.toString(taken) + "ms!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ((plugin.useSQL) && (!plugin.useMySQL)) {
			String query = "SELECT * FROM skillz WHERE player = '" + p + "'";
			ResultSet res = plugin.dbManager.query(query);
			try {
				boolean exists = res.next();
				if (exists)
					return;
			}
			catch (SQLException e) {
				e.printStackTrace();
			}

			query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', 'acrobatics', 0, 0);";
			plugin.dbManager.query(query);
			query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', 'archery' , 0 , 0);";
			plugin.dbManager.query(query);
			query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', 'digging' , 0 , 0);";
			plugin.dbManager.query(query);
			query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', 'swords' , 0 , 0);";
			plugin.dbManager.query(query);
			query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', 'woodcutting' , 0 , 0);";
			plugin.dbManager.query(query);
			query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', 'unarmed' , 0 , 0);";
			plugin.dbManager.query(query);
			query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', 'axes' , 0 , 0);";
			plugin.dbManager.query(query);
			query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', 'swimming' , 0 , 0);";
			plugin.dbManager.query(query);
			query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', 'farming' , 0 , 0);";
			plugin.dbManager.query(query);
			query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', 'mining' , 0 , 0);";
			plugin.dbManager.query(query);
			plugin.log.info("Skillz SQL Entry created for " + p + "!");
			double time2 = System.nanoTime();
			double taken = (time2 - time) / 1000000.0D;
			plugin.log.info("Creation took " + Double.toString(taken) + "ms!");
		} else if ((!plugin.useSQL) && (plugin.useMySQL)) {
			try {
				String query = "SELECT * FROM skillz WHERE player='" + p.getName() + "';";
				ResultSet res = plugin.mysql.query(query);
				if (res != null) {
					return;
				}
				query = "INSERT INTO skillz (player, skill, xp, level) VALUES ('" + p.getName() + "', 'acrobatics', 0, 0);";
				plugin.mysql.query(query);
				query = "INSERT INTO skillz (player, skill, xp, level) VALUES ('" + p.getName() + "', 'archery' , 0 , 0);";
				plugin.mysql.query(query);
				query = "INSERT INTO skillz (player, skill, xp, level) VALUES ('" + p.getName() + "', 'digging' , 0 , 0);";
				plugin.mysql.query(query);
				query = "INSERT INTO skillz (player, skill, xp, level) VALUES ('" + p.getName() + "', 'swords' , 0 , 0);";
				plugin.mysql.query(query);
				query = "INSERT INTO skillz (player, skill, xp, level) VALUES ('" + p.getName() + "', 'woodcutting' , 0 , 0);";
				plugin.mysql.query(query);
				query = "INSERT INTO skillz (player, skill, xp, level) VALUES ('" + p.getName() + "', 'unarmed' , 0 , 0);";
				plugin.mysql.query(query);
				query = "INSERT INTO skillz (player, skill, xp, level) VALUES ('" + p.getName() + "', 'axes' , 0 , 0);";
				plugin.mysql.query(query);
				query = "INSERT INTO skillz (player, skill, xp, level) VALUES ('" + p.getName() + "', 'swimming' , 0 , 0);";
				plugin.mysql.query(query);
				query = "INSERT INTO skillz (player, skill, xp, level) VALUES ('" + p.getName() + "', 'farming' , 0 , 0);";
				plugin.mysql.query(query);
				query = "INSERT INTO skillz (player, skill, xp, level) VALUES ('" + p.getName() + "', 'mining' , 0 , 0);";
				plugin.mysql.query(query);
				plugin.log.info("skillz MySQL Entry created for " + p.getName() + "!");
				double time2 = System.nanoTime();
				double taken = (time2 - time) / 1000000.0D;
				plugin.log.info("Creation took " + Double.toString(taken) + "ms!");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if ((plugin.useSQL) && (plugin.useMySQL)) {
			plugin.log.info("[Skillz] SQL and MySQL are both enabled! Nothing happened!");
			p.sendMessage("Tell an admin the settings of Skillz are wrong!");
			return;
		}
	}

}
