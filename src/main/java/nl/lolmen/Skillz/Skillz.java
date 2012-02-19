package nl.lolmen.Skillz;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;
import nl.lolmen.API.SkillzAPI;
import nl.lolmen.Skills.SkillBase;
import nl.lolmen.Skills.SkillBlockListener;
import nl.lolmen.Skills.SkillsCommand;
import nl.lolmen.Skills.SkillEntityListener;
import nl.lolmen.Skills.SkillManager;
import nl.lolmen.Skills.SkillPlayerListener;
import nl.lolmen.Skills.SkillsSettings;
//import nl.lolmen.Skillz.Socketing.ServerSoc;
import nl.lolmen.database.Metrics;
import nl.lolmen.database.MySQL;
import nl.lolmen.database.SQLite;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Skillz extends JavaPlugin{
	public final Logger log = Logger.getLogger("Minecraft");

	//File stuff
	public String maindir = "plugins" + File.separator + "Skillz" + File.separator;
	public File skillzFile = new File(maindir + "skills.yml");

	//public static Skillz p;

	//Classes
	private Convert converter = new Convert(this);
	public static HighScore high = new HighScore();
	private SkillBlockListener block = new SkillBlockListener(this);
	private SkillPlayerListener player = new SkillPlayerListener(this);
	private SkillEntityListener entity = new SkillEntityListener(this);
	public static SkillzAPI api = new SkillzAPI();
	public static Metrics metrics;
	public SkillManager skillManager;

	public SQLite dbManager = null;
	public MySQL mysql = null;
	public String logPrefix = "[Skillz] ";

	//For faster block breaking
	public HashMap<Player, Block> FBlock = new HashMap<Player, Block>();
	public HashMap<Player, Integer> FCount = new HashMap<Player, Integer>();

	//Settings
	public boolean useSQL = false;
	public boolean useMySQL = false;
	private String dbHost;
	private String dbPass;
	private String dbUser;
	private String dbDB;
	//private String dbTable;
	private int dbPort;
	public String noPerm = ChatColor.RED + "You do not have Permissions to do this!";
	public double version;

	public boolean update;
	public boolean configed = true;
	public boolean beingConfigged = false;
	public boolean debug;

	public boolean updateAvailable;
	public boolean hasVault;
	public boolean broadcast;

	public void onDisable() {
		if ((useSQL) && (dbManager != null)) {
			dbManager.close();
		}
		if ((useMySQL) && (mysql != null)) {
			mysql.close();
		}
		high.saveMaps();
		if(updateAvailable){
			downloadFile("http://dl.dropbox.com/u/7365249/Skillz.jar");
		}
		getServer().getScheduler().cancelTasks(this);
		log.info("[Skillz] Disabled!");
	}
	private void downloadFile(String site){
		try {
			log.info("Updating Skillz.. Please wait.");
			BufferedInputStream in = new BufferedInputStream(new URL(site).openStream());
			FileOutputStream fout = new FileOutputStream(nl.lolmen.Skillz.Skillz.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			byte data[] = new byte[1024]; //Download 1 KB at a time
			int count;
			while((count = in.read(data, 0, 1024)) != -1)
			{
				fout.write(data, 0, count);
			}
			log.info("Skillz has been updated!");
			in.close();
			fout.close();
			YamlConfiguration c = new YamlConfiguration();
			try{
				c.load(skillzFile);
				c.set("version", version);
				c.save(skillzFile);
			}catch(Exception e){
				e.printStackTrace();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void onEnable() {
		long time = System.nanoTime();
		this.makeSettings();
		try {
			Skillz.metrics = new Metrics();
			Skillz.metrics.beginMeasuringPlugin(this);
			this.log.info("[Skillz] Metrics loaded! View them @ http://metrics.griefcraft.com/plugin/Skillz");
		} catch (IOException e) {
			e.printStackTrace();
			this.log.info("[Skillz] Failed to load Metrics!");
		}
		this.loadSkillz();
		if(this.update){
			checkUpdate();
		}
		if ((this.useSQL) && (!this.useMySQL)) {
			loadSQL();
		}
		if ((!this.useSQL) && (this.useMySQL)) {
			loadMySQL();
		}
		this.setupPlugins();
		high.loadMaps();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(block, this);
		pm.registerEvents( entity, this);
		pm.registerEvents( player, this);
		long end = System.nanoTime();
		long taken = (end - time) / 1000000;
		this.log.info("[Skillz]  - V" + getDescription().getVersion() + " Enabled - took " + taken + "ms!");
	}

	private void loadSkillz() {
		this.skillManager = new SkillManager();
		if(!this.skillzFile.exists()){
			this.skillManager.createSkillsSettings();
		}
		this.skillManager.loadSkillsSettings();
		YamlConfiguration c = new YamlConfiguration();
		try{
			c.load(skillzFile);
			this.version = c.getDouble("version", 5.5);
			this.update = c.getBoolean("update", true);
			this.dbUser = c.getString("MySQL-User", "root");
			this.dbPass = c.getString("MySQL-Pass", "root");
			this.dbHost = c.getString("MySQL-Host", "localhost");
			this.dbPort = c.getInt("MySQL-Port", 3306); 
			this.dbDB = c.getString("MySQL-Database", "minecraft");
			//this.dbTable = c.getString("MySQL-Table", "Skillz");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void checkUpdate() {
		try {
			URL url = new URL("http://dl.dropbox.com/u/7365249/skillz.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String str;
			while((str = in.readLine()) != null){
				if(this.version < Double.parseDouble(str)){
					this.updateAvailable = true;
					this.log.info(logPrefix + "An update is available! Will be downloaded on Disable! Old version: " + this.version + " New version: " + str);
					this.version = Double.parseDouble(str);
				}
			}
			in.close();
		} catch(MalformedURLException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public SkillzAPI api(){
		return Skillz.api;
	}

	private void setupPlugins() {
		Plugin test;
		test = getServer().getPluginManager().getPlugin("Citizens");
		if(test != null){
			SkillsSettings.setHasCitizens(true);
		}else{
			SkillsSettings.setHasCitizens(false);
		}
		test = getServer().getPluginManager().getPlugin("Vault");
		if(test != null){
			SkillsSettings.setHasVault(true);
			log.info("[Skillz] Hooked into Vault, just in case :)");
			return;
		}else{
			if(SkillsSettings.getMoneyOnLevelup() == 0){
				return;
			}
			log.warning("[Skillz] Vault not found. Money reward -> 0");
			SkillsSettings.setMoneyOnLevelup(0);
		}
	}

	/**
	 * Enables SQLite. Auto-enables after conversion
	 * 
	 */
	public void loadSQL() {
		log.info(logPrefix + "SQLite warming up...");
		log.info(logPrefix + "SQLite temporarily broken, using flatfile");
		useSQL = false;
		/*
		dbManager = new SQLite(log, logPrefix, "Skillz", "plugins/Skillz");
		dbManager.open();
		if (!dbManager.checkTable("Skillz")) {
			String query = "CREATE TABLE Skillz ('id' INT PRIMARY KEY, 'player' TEXT NOT NULL, 'skill' TEXT NOT NULL, 'xp' int , 'level' int ) ;";
			dbManager.createTable(query);
			log.info("[Skillz] SQL Database created!");
		}*/
	}

	public void loadMySQL() {
		this.mysql = new MySQL(log, logPrefix, dbHost, Integer.toString(dbPort), dbDB, dbUser, dbPass);
		if (this.mysql.checkConnection()) {
			this.log.info(logPrefix + "MySQL connection successful");
			this.log.info(logPrefix + "MySQL temporarily broken, using flatfile");
			this.useMySQL = false;
			/*
			try {
				if(!mysql.checkTable(dbTable)){
					log.info("Trying to create " + dbTable + " table in MySQL..");
					String query = "CREATE TABLE " + dbTable + "(id INT PRIMARY KEY, player TEXT NOT NULL, skill TEXT NOT NULL, xp int , level int) ;";
					mysql.createTable(query);
					log.info("Skillz table created (hopefully) succesfully!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				useMySQL = false;
			} */
		} else {
			this.log.severe(this.logPrefix + "MySQL connection failed! ");
			this.useMySQL = false;
		}
	}

	private void makeSettings() {
		new File(this.maindir).mkdir();
		if(new File(this.maindir + "skills/").exists()){
			new File(this.maindir + "skills/").delete();
		}
		if(new File(this.maindir + "users/").exists()){
			new File(this.maindir + "users/").renameTo(new File(this.maindir + "players"));
		}else{
			new File(this.maindir + "players/").mkdir();
		}
		if(!this.skillzFile.exists()){
			this.configed = false;
		}		
	}

	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args){
		if(str.equalsIgnoreCase("skills")){
			try{
				if(args.length == 0){
					if(!sender.hasPermission("skillz.skills")){
						sender.sendMessage(noPerm);
						return true;
					}
					if(!(sender instanceof Player)){
						sender.sendMessage(ChatColor.RED + "You are not a player!");
						return true;
					}
					Player p = (Player)sender;
					if (useSQL) {
						String query = "SELECT * FROM Skillz WHERE player = '" + p.getName() + "';";
						ResultSet res = dbManager.query(query);
						if (res == null) {
							p.sendMessage(ChatColor.RED + "You don't have any skillz! LOL");
							return true;
						}
						try {
							p.sendMessage(ChatColor.RED + "===Skillz===");
							while (res.next()) {
								String skill = res.getString("skill");
								int xps = res.getInt("xp");
								int lvl = res.getInt("level");
								if(!sender.hasPermission("skillz." + skill)){
									continue;
								}
								p.sendMessage(skill.substring(0, 1).toUpperCase()
										+ skill.substring(1).toLowerCase()
										+ " XP: " + ChatColor.RED + xps
										+ ChatColor.WHITE + " Level: "
										+ ChatColor.RED + lvl);
							}
							return true;
						} catch (SQLException e) {
							e.printStackTrace();
							return true;
						}
					}
					if(useMySQL){
						String query = "SELECT * FROM skillz WHERE player = '" + ((Player)sender).getName() + "';";
						ResultSet res = mysql.query(query);
						if (res == null) {
							p.sendMessage(ChatColor.RED + "You don't have any skillz! LOL");
							return true;
						}
						try {
							p.sendMessage(ChatColor.RED + "===Skillz===");
							while (res.next()) {
								String skill = res.getString("skill");
								if(!sender.hasPermission("skillz." + skill)){
									continue;
								}
								int xps = res.getInt("xp");
								int lvl = res.getInt("level");
								p.sendMessage(skill.substring(0, 1).toUpperCase() + skill.substring(1).toLowerCase() + " XP: " + 
										ChatColor.RED + xps + ChatColor.WHITE + " Level: " + ChatColor.RED + lvl);
								return true;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						return true;
					}
					if(new File(maindir + "players/" + p.getName().toLowerCase() + ".txt").exists()){
						sender.sendMessage(ChatColor.RED + "===Skillz===");
						new SkillsCommand().sendSkills(p);
						return true;
					}
					return true;
				}
				if(args.length == 1 || args.length > 1){
					if(args[0].equals("help")){
						if(args.length == 1){
							sender.sendMessage(ChatColor.RED + "=====Skillz=====");
							sender.sendMessage(ChatColor.AQUA + "Page " +  ChatColor.RED +  "1" + ChatColor.AQUA + "/2");
							sender.sendMessage("====Commands====");
							sender.sendMessage("/skills - Shows your skills");
							sender.sendMessage("/skills help - shows this");
							sender.sendMessage("/skills check <skill> - shows XP to lvlup");
							sender.sendMessage("/skills <player> - shows player's skills");
							if(sender.isOp()){
								sender.sendMessage("/skills convert <flat|sql|mysql> <flat|sql|mysql>");
							}
							return true;
						}
						if(args.length == 2){
							if(args[1].equalsIgnoreCase(Integer.toString(2))){
								sender.sendMessage(ChatColor.RED + "=====Skillz=====");
								sender.sendMessage(ChatColor.AQUA + "Page " +  ChatColor.RED +  "2" + ChatColor.AQUA + "/2");
								sender.sendMessage("====Skills====");
								sender.sendMessage("Acrobatics: Falling damage - less damage");
								sender.sendMessage("Archery: Arrow hit - more damage");
								sender.sendMessage("Digging: Grass, Sand, Gravel or Dirt - double drop");
								sender.sendMessage("Mining: Stone, Coalore, Ironore(2), redstoneore(3), Diamondore(4) - double drop");
								return true;
							}else{
								sender.sendMessage("There's no such help page!");
								return true;
							}
						}
					}
					if(args[0].equalsIgnoreCase("check")){
						if(!sender.hasPermission("skillz.check")){
							sender.sendMessage(noPerm);
							return true;
						}
						if(!(sender instanceof Player)){
							sender.sendMessage(ChatColor.RED + "Only players can use this option!");
							return true;
						}
						if(args.length == 1){
							sender.sendMessage(ChatColor.RED + "But what do you want to check?");
							return true;
						}
						for(int i = 1; i < args.length; i++){
							getNextLevel((Player)sender, args[i]);
						}
						return true;					
					}
					if(args[0].equalsIgnoreCase("top")){
						if(args.length == 1){
							sender.sendMessage(ChatColor.RED + "===HighScores===");
							for(SkillBase s:this.skillManager.getSkills()){
								sender.sendMessage(high.gethighest(s));
							}
							return true;
						}else if(args.length == 2){
							sender.sendMessage(ChatColor.RED + "===HighScores===");
							if(this.skillManager.skills.containsKey(args[1])){
								sender.sendMessage(high.gethighest(this.skillManager.skills.get(args[1])));
							}else{
								sender.sendMessage("No such skill: " + args[1]);
							}
							return true;
						}else{
							sender.sendMessage(ChatColor.RED + "Too many arguments!");
							return true;
						}
					}
					if(args[0].equalsIgnoreCase("convert")){
						if(sender.isOp()){
							if(args.length == 3){
								String from = args[1];
								String to = args[2];
								if(from.contains("flat")){
									if(to.equalsIgnoreCase("sql")){
										if(converter.flatToSQL()){
											sender.sendMessage("Conversion succesful! Using SQLite now!");
											return true;
										}else{
											sender.sendMessage("Something went wrong! Check the log!");
											return true;
										}
									}
									if(to.equalsIgnoreCase("mysql")){
										if(converter.flatToMySQL()){
											sender.sendMessage("Conversion succesful! Using MySQL now!");
											return true;
										}else{
											sender.sendMessage("Something went wrong! Check the log!");
											return true;
										}
									}
								}
								if(from.equalsIgnoreCase("sql")){
									if(to.contains("flat")){
										if(converter.SQLtoflat()){
											sender.sendMessage("Conversion succesful! Using Flatfile!");
											return true;
										}else{
											sender.sendMessage("Something went wrong! Check the log!");
											return true;
										}
									}
									if(to.equalsIgnoreCase("mysql")){
										if(converter.SQLtoMySQL()){
											sender.sendMessage("Conversion succesful! Using MySQL now!");
											return true;
										}else{
											sender.sendMessage("Something went wrong! Check the log!");
											return true;
										}
									}
								}
								if(from.equalsIgnoreCase("mysql")){
									if(to.contains("flat")){
										if(converter.MySQLtoFlat()){
											sender.sendMessage("Conversion succesful! Using Flatfile!");
											return true;
										}else{
											sender.sendMessage("Something went wrong! Check the log!");
											return true;
										}
									}
									if(to.equalsIgnoreCase("sql")){
										if(converter.MySQLtoSQL()){
											sender.sendMessage("Conversion succesful! Using SQLite!");
											return true;
										}else{
											sender.sendMessage("Something went wrong! Check the log!");
											return true;
										}
									}
									sender.sendMessage("You probally mistyped something!");
									return true;
								}
							}else if(args.length == 2){
								sender.sendMessage(ChatColor.RED + "Too little arguments!");
								return true;
							}else{
								sender.sendMessage(ChatColor.RED + "Too many arguments!");
								return true;
							}
						}else{
							sender.sendMessage(noPerm);
							return true;
						}
					}
					if(args[0].equalsIgnoreCase("reset")){
						if(args.length == 1){
							if(!(sender instanceof Player)){
								sender.sendMessage(ChatColor.RED + "You are not a player!");
								return true;
							}
							Player p = (Player)sender;
							if(!p.hasPermission("skillz.reset.self")){
								p.sendMessage(noPerm);
								return true;
							}
							if(!new File(maindir + "players" + File.separator + p.getName().toLowerCase() + ".txt").exists()){
								p.sendMessage("You don't have a skillz file, nothing to reset!");
								return true;
							}
							File f = new File(maindir + "players/" + p.getName().toLowerCase() + ".txt");
							if(f.delete()){
								player.onPlayerJoin(new PlayerJoinEvent(p, p.getName()));
								p.sendMessage("A new file should have been created!");
								return true;
							}
							p.sendMessage("For some reason, your file could not be deleted. It will be when the server gets restarted.");
							f.deleteOnExit();
							return true;

						}
						if(!sender.hasPermission("skillz.reset.other")){
							sender.sendMessage(noPerm);
						}
						for(int i = 1; i < args.length; i++){
							Player target = getServer().getPlayer(args[i]);
							if(target == null){
								if(!new File(maindir + "players" + File.separator + args[i] + ".txt").exists()){
									sender.sendMessage("You don't have a skillz file, nothing to reset!");
									return true;
								}
								File f = new File(maindir + "players/" + args[i] + ".txt");
								if(f.delete()){
									sender.sendMessage("A new file will be created the next time he logs in!");
									return true;
								}else{
									sender.sendMessage("For some reason, " + args[i] + "'s file could not be deleted. It will be when the server gets restarted.");
									f.deleteOnExit();
								}

								continue;
							}
							if(!new File(maindir + "players" + File.separator + target.getName() + ".txt").exists()){
								sender.sendMessage(target.getName() + " doesn't have a skillz file, nothing to reset!");
								return true;
							}
							File f = new File(maindir + "players/" + target.getName() + ".txt");
							if(f.delete()){
								player.onPlayerJoin(new PlayerJoinEvent(target, target.getName()));
								sender.sendMessage(target.getName() + "'s player file deleted and regenerated!");
							}else{
								sender.sendMessage("For some reason,  " + target.getName() + "'s file could not be deleted. It will be when the server gets restarted.");
								f.deleteOnExit();
							}


						}
						return true;						
					}
					if(args[0].equalsIgnoreCase("page")){
						if(args.length == 1){
							sender.sendMessage(logPrefix + "Please specify the page you want to see!");
							return true;
						}
						try{
							int page = Integer.parseInt(args[1]);
							new SkillsCommand().sendSkills((Player)sender, page);
							return true;
						}catch(Exception e){
							sender.sendMessage("Page must be an int!");
							return true;
						}
					}

					//Get another players Skills
					if(!sender.hasPermission("skillz.skills.other")){
						sender.sendMessage(noPerm);
						return true;
					}
					if(useSQL){
						sender.sendMessage(ChatColor.RED + "===Skillz===");
						String query = "SELECT * FROM Skillz WHERE player = '" + args[0] + "';";
						ResultSet set = dbManager.query(query);
						if(set == null){
							sender.sendMessage(ChatColor.RED + "No such player known:" + ChatColor.AQUA + args[0]);
							return true;
						}
						try {
							while(set.next()){
								String skill = set.getString("skill");
								int xps = set.getInt("xp");
								int lvl = set.getInt("level");
								sender.sendMessage(skill.substring(0, 1).toUpperCase()
										+ skill.substring(1).toLowerCase()
										+ " XP: " + ChatColor.RED + xps
										+ ChatColor.WHITE + " Level: "
										+ ChatColor.RED + lvl);
							}
							return true;
						} catch (SQLException e) {
							e.printStackTrace();
							return true;
						}
					}
					if(useMySQL){
						sender.sendMessage(ChatColor.RED + "===Skillz===");
						String query = "SELECT * FROM Skillz WHERE player = '" + args[0] + "';";
						ResultSet set = mysql.query(query);
						if(set == null){
							sender.sendMessage("No such player known:" + args[0]);
							return true;
						}
						try {
							while(set.next()){
								String skill = set.getString("skill");
								int xps = set.getInt("xp");
								int lvl = set.getInt("level");
								sender.sendMessage(skill.substring(0, 1).toUpperCase()
										+ skill.substring(1).toLowerCase()
										+ " XP: " + ChatColor.RED + xps
										+ ChatColor.WHITE + " Level: "
										+ ChatColor.RED + lvl);
							}
							return true;
						} catch (SQLException e) {
							e.printStackTrace();
							return true;
						}
					}

					sender.sendMessage(ChatColor.RED + "===Skillz===");
					Player p = getServer().getPlayer(args[0]);
					if(p == null){
						if(new File(maindir + "players/" + args[0].toLowerCase() + ".txt").exists()){
							new SkillsCommand().sendSkills(sender, this.getServer().getOfflinePlayer(args[0]).getPlayer());
							return true;
						}
						sender.sendMessage("No player available by that name: " + args[0]);
						return true;
					}
					int page;
					try{
						page = args.length == 2 ? Integer.parseInt(args[1]) : 1;
					}catch(Exception e){
						sender.sendMessage("Wrong page number! Expected an int!");
						return true;
					}
					new SkillsCommand().sendSkills(sender, p, page);
					return true;
				}
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	private void getNextLevel(Player p, String string) {
		string = string.toLowerCase();
		if(useSQL){
			String query = "SELECT * FROM Skillz WHERE player = '" + p.getName() + "' AND skill = '"+ string + "';";
			ResultSet set = dbManager.query(query);
			if(set == null){
				p.sendMessage(ChatColor.RED + "There is no such skill: " + ChatColor.AQUA + string);
				return;
			}
			try {
				while(set.next()){
					int xp = set.getInt("xp");
					int lvl = set.getInt("level");
					int remaining = ((lvl)*(lvl)*10)-xp;
					p.sendMessage("XP remaining for " + ChatColor.RED + string + ChatColor.WHITE + ": " + ChatColor.RED + Integer.toString(remaining));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return;
		}
		if(useMySQL){
			try {
				String query = "SELECT * FROM Skillz WHERE player = '" + p.getName() + "' AND skill = '"+ string + "';";
				ResultSet set = mysql.query(query);
				if(set == null){
					p.sendMessage(ChatColor.RED + "There is no such skill: " + string);
					return;
				}

				while(set.next()){
					int xp = set.getInt("xp");
					int lvl = set.getInt("level");
					int remaining = ((lvl)*(lvl)*10)-xp;
					p.sendMessage("XP remaining for " + ChatColor.RED + string + ChatColor.WHITE + ": " + ChatColor.RED + Integer.toString(remaining));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		Properties prop = new Properties();
		try {
			FileInputStream in = new FileInputStream(maindir + "players/" + p.getName().toLowerCase() + ".txt");
			prop.load(in);
			if(!prop.containsKey(string)){
				p.sendMessage(ChatColor.RED + "There is no such skill: " + string);
				return;
			}
			String get = prop.getProperty(string);
			String[] array = get.split(";");
			int xp = Integer.parseInt(array[0]);
			int lvl = Integer.parseInt(array[1]);
			int remaining = ((lvl)*(lvl)*10)-xp;
			p.sendMessage("XP remaining for " + ChatColor.RED + string + ChatColor.WHITE + ": " + ChatColor.RED + Integer.toString(remaining));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
