package nl.lolmen.Skillz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.bukkit.configuration.file.YamlConfiguration;

public class Convert {
	public Skillz plugin;
	public Convert(Skillz skillz) {
		plugin = skillz;
	}

	double start;
	double stop;

	public boolean flatToSQL(){
		start = System.nanoTime();
		plugin.log.warning("Starting conversion from Flatfile to SQLite. Expect huge lag!");
		plugin.getServer().broadcastMessage("Starting conversion, expect lag and maybe disconnection!");
		File dir = new File(plugin.maindir + "players/");
		File[] filename = dir.listFiles();
		if(plugin.dbManager == null){
			plugin.loadSQL();
		}
		if(!plugin.dbManager.checkTable("Skillz")){
			plugin.dbManager.createTable("CREATE TABLE Skillz ('id' INT PRIMARY KEY, 'player' TEXT NOT NULL, 'skill' TEXT NOT NULL, 'xp' int , 'level' int ) ;");
		}
		plugin.dbManager.wipeTable("Skillz");
		for (File f : filename) {
			String file = f.getName();
			plugin.log.info("[Skillz] Converting " + file +" to SQLite..");
			String p = file.substring(0, file.lastIndexOf("."));
			try {
				BufferedReader in1 = new BufferedReader(new FileReader(f));
				String str;
				while ((str = in1.readLine()) != null)
				{
					if(str.startsWith("#")){
						break;
					}
					String[] split = str.split("=");
					String skill = split[0];
					String[] lvlsplit = split[1].split(";");
					int xp = Integer.parseInt(lvlsplit[0]);
					int lvl = Integer.parseInt(lvlsplit[1]);
					String query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', '" + skill + "', " + xp + ", " + lvl + ");";
					plugin.dbManager.query(query);
				}
				in1.close();
				plugin.useSQL = true;
				plugin.useMySQL = false;
				setting("useSQLite", true);
				setting("useMySQL", false);
				plugin.log.info("[Skillz] Conversion complete. Using SQLite now.");
				plugin.getServer().broadcastMessage("Conversion complete! Have a good day!");
				stop = System.nanoTime();
				double taken = (stop-start)/1000000;
				plugin.log.info("Conversion took " + Double.toString(taken) + "ms!");
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	public boolean flatToMySQL(){
		start = System.nanoTime();
		plugin.log.warning("[Skillz] Starting conversion from Flatfile to MySQL. Expect huge lag!");
		plugin.getServer().broadcastMessage("Starting conversion, expect lag and maybe disconnection!");
		File dir = new File(plugin.maindir + "players/");
		File[] filename = dir.listFiles();
		if(plugin.mysql == null){
			plugin.loadMySQL();
		}
		try {
			if(!plugin.mysql.checkTable("Skillz")){
				plugin.mysql.createTable("CREATE TABLE Skillz ('id' INT PRIMARY KEY, 'player' TEXT NOT NULL, 'skill' TEXT NOT NULL, 'xp' int , 'level' int ) ;");
			}
			plugin.mysql.wipeTable("Skillz");
			for (File f : filename) {
				String file = f.getName();
				plugin.log.info("Converting " + file +" to MySQL..");
				String p = file.substring(0, file.lastIndexOf("."));
				try {
					BufferedReader in1 = new BufferedReader(new FileReader(f));
					String str;
					while ((str = in1.readLine()) != null)
					{
						if(str.startsWith("#")){
							break;
						}
						String[] split = str.split("=");
						String skill = split[0];
						String[] lvlsplit = split[1].split(";");
						int xp = Integer.parseInt(lvlsplit[0]);
						int lvl = Integer.parseInt(lvlsplit[1]);
						String query = "INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', '" + skill + "', " + xp + ", " + lvl + ");";
						plugin.mysql.query(query);
					}
					in1.close();
					plugin.useSQL = false;
					plugin.useMySQL = true;
					setting("useSQLite", false);
					setting("useMySQL", true);
					plugin.log.info("[Skillz] Conversion complete. Using MySQL now.");
					plugin.getServer().broadcastMessage("Conversion complete! Have a good day!");
					stop = System.nanoTime();
					double taken = (stop-start)/1000000;
					plugin.log.info("Conversion took " + Double.toString(taken) + "ms!");
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		return false;
	}
	public boolean SQLtoMySQL(){
		start = System.nanoTime();
		if(plugin.mysql == null){
			plugin.loadMySQL();
		}
		if(plugin.dbManager == null){
			plugin.loadSQL();
		}
		try {
			if(plugin.mysql.checkTable("Skillz")){
				plugin.mysql.wipeTable("Skillz");
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		ResultSet set = plugin.dbManager.query("SELECT * FROM Skillz");
		if(set == null){
			plugin.log.warning("[Skillz] Something seems to be wrong with your SQL database!");
			return false;
		}
		plugin.log.warning("Starting conversion from SQLite to MySQL. Expect huge lag!");
		plugin.getServer().broadcastMessage("Starting conversion, expect lag and maybe disconnection!");
		try {
			while(set.next()){
				String p = set.getString("player");
				String skill = set.getString("skill");
				int xp = set.getInt("xp");
				int lvl = set.getInt("level");
				plugin.mysql.query("INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', '" + skill + "', " + xp + "," + lvl +");");

			}
			plugin.useSQL = false;
			plugin.useMySQL = true;
			setting("useSQLite", false);
			setting("useMySQL", true);
			plugin.log.info("[Skillz] Conversion complete. Using MySQL as database.");
			plugin.getServer().broadcastMessage("Conversion complete! Have a good day!");
			stop = System.nanoTime();
			double taken = (stop-start)/1000000;
			plugin.log.info("Conversion took " + Double.toString(taken) + "ms!");
			return true;			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean SQLtoflat(){
		start = System.nanoTime();
		if(plugin.dbManager == null){
			plugin.loadSQL();
		}
		if(!plugin.dbManager.checkConnection()){
			plugin.log.warning("[Skillz] Something seems to be wrong with your SQLite Database! It seems to not-exist!");
			return false;
		}
		String query = "SELECT * FROM Skillz";
		ResultSet set = plugin.dbManager.query(query);
		if(set == null){
			plugin.log.warning("[Skillz] Something seems to be wrong with your SQLite database!");
			return false;
		}
		plugin.log.warning("Starting conversion from SQLite to Flatfile. Expect huge lag!");
		plugin.getServer().broadcastMessage("Starting conversion, expect lag and maybe disconnection!");
		try {
			Properties prop = new Properties();
			while(set.next()){
				String player = set.getString("player");
				if(!new File(plugin.maindir + "players/" + player + ".txt").exists()){
					new File(plugin.maindir + "players/" + player + ".txt").createNewFile();
				}
				int xp = set.getInt("xp");
				int lvl = set.getInt("level");
				String skill = set.getString("skill");
				prop.put(skill, xp + ";" + lvl);
				FileOutputStream out = new FileOutputStream(new File(plugin.maindir + "players/" + player + ".txt"));
				prop.store(out, "[Skill]=[XP];[LVL]");
			}
			plugin.useSQL = false;
			plugin.useMySQL = false;
			setting("useSQLite", false);
			setting("useMySQL", false);
			plugin.log.info("[Skillz] Conversion complete. Using flatfile as database.");
			plugin.getServer().broadcastMessage("Conversion complete! Have a good day!");
			stop = System.nanoTime();
			double taken = (stop-start)/1000000;
			plugin.log.info("Conversion took " + Double.toString(taken) + "ms!");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean MySQLtoFlat(){
		start = System.nanoTime();
		if(plugin.mysql == null){
			plugin.loadMySQL();
		}
		if(!plugin.mysql.checkConnection()){
			plugin.log.warning("[Skillz] Something seems to be wrong with your MySQL Database! It seems to not-exist!");
			return false;
		}
		String query = "SELECT * FROM Skillz";
		ResultSet set;
		try {
			set = plugin.mysql.query(query);

			if(set == null){
				plugin.log.warning("[Skillz] Something seems to be wrong with your MySQL database!");
				return false;
			}
			plugin.log.warning("Starting conversion from MySQL to FlatFile. Expect huge lag!");
			plugin.getServer().broadcastMessage("Starting conversion, expect lag and maybe disconnection!");
			try {
				Properties prop = new Properties();
				while(set.next()){
					String player = set.getString("player");
					if(!new File(plugin.maindir + "players/" + player + ".txt").exists()){
						new File(plugin.maindir + "players/" + player + ".txt").createNewFile();
					}
					int xp = set.getInt("xp");
					int lvl = set.getInt("level");
					String skill = set.getString("skill");
					prop.put(skill, xp + ";" + lvl);
					FileOutputStream out = new FileOutputStream(new File(plugin.maindir + "players/" + player + ".txt"));
					prop.store(out, "[Skill]=[XP];[LVL]");
				}
				plugin.useSQL = false;
				plugin.useMySQL = false;
				setting("useSQLite", false);
				setting("useMySQL", false);
				plugin.log.info("[Skillz] Conversion complete. Using FlatFile as database.");
				plugin.getServer().broadcastMessage("Conversion complete! Have a good day!");
				stop = System.nanoTime();
				double taken = (stop-start)/1000000;
				plugin.log.info("Conversion took " + Double.toString(taken) + "ms!");
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return false;
		} catch (InstantiationException e1) {
			e1.printStackTrace();
			return false;
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
			return false;
		}
	}
	public boolean MySQLtoSQL() {
		start = System.nanoTime();
		if(plugin.mysql == null){
			plugin.loadMySQL();
		}
		if(plugin.dbManager == null){
			plugin.loadSQL();
		}
		if(plugin.dbManager.checkTable("Skillz")){
			plugin.dbManager.wipeTable("Skillz");
		}
		ResultSet set;
		try {
			set = plugin.mysql.query("SELECT * FROM Skillz");
			if(set == null){
				plugin.log.warning("[Skillz] Something seems to be wrong with your MySQL database!");
				return false;
			}
			plugin.log.warning("Starting conversion from MySQL to SQLite. Expect huge lag!");
			plugin.getServer().broadcastMessage("Starting conversion, expect lag and maybe disconnection!");
			try {
				while(set.next()){
					String p = set.getString("player");
					String skill = set.getString("skill");
					int xp = set.getInt("xp");
					int lvl = set.getInt("level");
					plugin.dbManager.query("INSERT INTO Skillz (player, skill, xp, level) VALUES ('" + p + "', '" + skill + "', " + xp + "," + lvl +");");
				}
				plugin.useSQL = true;
				plugin.useMySQL = false;
				setting("useSQLite", true);
				setting("useMySQL", false);
				plugin.log.info("[Skillz] Conversion complete. Using SQLite as database.");
				plugin.getServer().broadcastMessage("Conversion complete! Have a good day!");
				stop = System.nanoTime();
				double taken = (stop-start)/1000000;
				plugin.log.info("Conversion took " + Double.toString(taken) + "ms!");
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return false;
		} catch (InstantiationException e1) {
			e1.printStackTrace();
			return false;
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
			return false;
		}
	}

	public void setting(String name, boolean to){
		YamlConfiguration c = new YamlConfiguration();
		try {
			c.load(plugin.skillzFile);
			c.set(name, to);
			c.save(plugin.skillzFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
