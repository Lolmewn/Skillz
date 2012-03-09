package nl.lolmen.Skillz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.bukkit.configuration.file.YamlConfiguration;

public class Convert {

	private Skillz plugin;

	public Convert(Skillz skillz) {
		plugin = skillz;
	}

	private double start;
	private double stop;


	public boolean flatToMySQL(){
		start = System.nanoTime();
		plugin.log.warning("[Skillz] Starting conversion from Flatfile to MySQL. Expect huge lag!");
		plugin.getServer().broadcastMessage("Starting conversion, expect lag and maybe disconnection!");
		File dir = new File(plugin.maindir + "players/");
		File[] filename = dir.listFiles();
		if(plugin.mysql == null){
			plugin.loadMySQL();
		}
		if(plugin.mysql.isFault()){
			return false;
		}
		plugin.mysql.executeStatement("DROP TABLE IF EXISTS " + plugin.dbTable);
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
					plugin.mysql.executeQuery(query);
				}
				in1.close();
				plugin.useMySQL = true;
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

		return false;
	}

	public boolean MySQLtoFlat(){
		start = System.nanoTime();
		if(plugin.mysql == null){
			plugin.loadMySQL();
		}
		if(plugin.mysql.isFault()){
			return false;
		}
		String query = "SELECT * FROM " + this.plugin.dbTable;
		ResultSet set;
		try {
			set = plugin.mysql.executeQuery(query);
			if(set == null){
				plugin.log.warning("[Skillz] Something seems to be wrong with your MySQL database!");
				return false;
			}
			plugin.log.warning("Starting conversion from MySQL to FlatFile. Expect huge lag!");
			plugin.getServer().broadcastMessage("Starting conversion, expect lag and maybe disconnection!");
			while(set.next()){
				Properties prop = new Properties();
				String player = set.getString("player");
				if(!new File(plugin.maindir + "players/" + player + ".txt").exists()){
					new File(plugin.maindir + "players/" + player + ".txt").getParentFile().mkdirs();
					new File(plugin.maindir + "players/" + player + ".txt").createNewFile();
				}
				int xp = set.getInt("xp");
				int lvl = set.getInt("level");
				String skill = set.getString("skill");
				prop.put(skill, xp + ";" + lvl);
				FileOutputStream out = new FileOutputStream(new File(plugin.maindir + "players/" + player + ".txt"));
				prop.store(out, "[Skill]=[XP];[LVL]");
				out.flush();
				out.close();
			}
			plugin.useMySQL = false;
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
