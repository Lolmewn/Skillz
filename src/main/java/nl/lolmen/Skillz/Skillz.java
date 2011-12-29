package nl.lolmen.Skillz;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import nl.lolmen.API.SkillzAPI;
import nl.lolmen.API.SkillzSettings;
import nl.lolmen.Skills.SkillBase;
import nl.lolmen.Skills.SkillBlockListener;
import nl.lolmen.Skills.SkillCommandHandler;
import nl.lolmen.Skills.SkillEntityListener;
import nl.lolmen.Skills.SkillManager;
import nl.lolmen.Skills.SkillPlayerListener;
import nl.lolmen.Skillz.Socketing.ServerSoc;
import nl.lolmen.database.MySQL;
import nl.lolmen.database.SQLite;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Skillz extends JavaPlugin{
	public final Logger log = Logger.getLogger("Minecraft");
	
	//File stuff
	public String maindir = "plugins" + File.separator + "Skillz" + File.separator;
	public File settings = new File("plugins/Skillz/settings.yml");
	private File skillzFile = new File(maindir + "skills.yml");
	
	public static Skillz p;
	
	//Classes
	private Convert converter = new Convert(this);
	public HighScore high = new HighScore(this);
	private SkillBlockListener block = new SkillBlockListener();
	private SkillPlayerListener player = new SkillPlayerListener(this);
	private SkillEntityListener entity = new SkillEntityListener();
	public static SkillzAPI api = new SkillzAPI();
	
	//Some stuff
	public String[] skills = {"mining", "digging", "acrobatics" , "archery" , "farming" , 
			"woodcutting", "axes", "unarmed", "swords", "swimming"};
	public SQLite dbManager = null;
	public MySQL mysql = null;
	public String logPrefix = "[Skillz] ";
	public HashMap<String, Skill> skillList = new HashMap<String, Skill>();
	
	//For faster block breaking
	public HashMap<Player, Block> FBlock = new HashMap<Player, Block>();
	public HashMap<Player, Integer> FCount = new HashMap<Player, Integer>();

	//Settings
	public boolean usePerms;
	public boolean useIco;
	public boolean use3Co;
	public boolean useBOSE;
	public boolean useSQL = false;
	public boolean useMySQL = false;
	public String lvlupmsg;
	public String fallmsg;
	public String itemreward;
	public String dbHost;
	public String dbPass;
	public String dbUser;
	public String dbDB;
	public String noPerm = ChatColor.RED + "You do not have Permissions to do this!";
	public int reward;
	public double version;
	public boolean lightning;
	
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
			downloadFile("http://dl.dropbox.com/u/7365249/Skillz.jar", "plugins/Skillz.jar");
		}
		getServer().getScheduler().cancelTasks(this);
		log.info("[Skillz] Disabled!");
	}
	private void downloadFile(String site, String destination){
		try {
			log.info("Updating Skillz.. Please wait.");
			BufferedInputStream in = new BufferedInputStream(new URL(site).openStream());
			FileOutputStream fout = new FileOutputStream(destination);
			byte data[] = new byte[1024]; //Download 1 KB at a time
			int count;
			while((count = in.read(data, 0, 1024)) != -1)
			{
				fout.write(data, 0, count);
			}
			log.info("Skillz has been updated!");
			in.close();
			fout.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void onEnable() {
		p = this;
		makeSettings();
		loadSettings();
		loadSkillz();
		if(update){
			checkUpdate();
		}
		if ((useSQL) && (!useMySQL)) {
			loadSQL();
		}
		if ((!useSQL) && (useMySQL)) {
			loadMySQL();
		}
		setupPlugins();
		high.loadMaps();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_BREAK, block, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entity, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, entity, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, player, Priority.Normal, this);
		if(!configed){
			pm.registerEvent(Event.Type.PLAYER_CHAT, player, Priority.Normal, this);
		}
		new ServerSoc(this);
		log.info("[Skillz] Enabled!");
	}

	private void loadSkillz() {
		/*
		YamlConfiguration c = new YamlConfiguration();
		try{
			c.load(skillzFile);
			for(String node: c.getConfigurationSection("").getKeys(false)){
				Skill s = new Skill(node);
				s.setEnabled(c.getBoolean(node + ".enabled"));
				if(c.contains(node + ".block_levels")){
					s.setAllFromLvl0(c.getBoolean(node + ".MineAllBlocksFromFirstLevel"));
					for(String block: c.getConfigurationSection(node + ".block_levels").getKeys(false)){
						int blocked = Integer.parseInt(block);
						int lvl = c.getInt(node + ".block_levels." + block);
						s.addLVL(blocked, lvl);
					}
				}
				if(c.contains(node + ".block_XP")){
					for(String block: c.getConfigurationSection(node + ".block_XP").getKeys(false)){
						int blocked = Integer.parseInt(block);
						int lvl = c.getInt(node + ".block_levels." + block);
						s.addLVL(blocked, lvl);
					}
				}
				if(c.contains(node + ".xp-gain")){
					s.setXpMulti(c.getInt(node + ".xp-gain"));
				}else{
					s.setXpMulti(1);
				}
				if(c.contains(node + ".reward.money")){
					s.setMoneyReward(c.getInt(node + ".reward.money"));
				}else{
					s.setMoneyReward(reward);
				}
				if(c.contains(node + ".reward.item")){
					s.setItemReward(c.getString(node + ".reward.item"));
				}else{
					s.setItemReward(itemreward);
				}
				if(c.contains(node + ".change")){
					s.setChange(c.getInt(node +".change"));
				}
				skillList.put(node, s);
			}
		}catch(Exception e){
			e.printStackTrace();
		}*/
		SkillManager sm = new SkillManager();
		if(!skillzFile.exists()){
			sm.createSkillsSettings();
		}
		sm.loadSkillsSettings();
	}
	private void checkUpdate() {
		try {
			URL url = new URL("http://dl.dropbox.com/u/7365249/skillz.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String str;
			while((str = in.readLine()) != null)
			{
				if(!str.equalsIgnoreCase(Double.toString(version))){
					updateAvailable = true;
					log.info(logPrefix + "An update is available! Will be downloaded on Disable! New version: " + str);
					YamlConfiguration c = new YamlConfiguration();
					try{
						c.load(settings);
						c.set("version", str);
						c.save(settings);
					}catch(Exception e){
						e.printStackTrace();
					}
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
		return api;
	}
	
	public Skill addSkill(String name){
		return new Skill(name);
	}

	private void setupPlugins() {
		Plugin test;
		test = getServer().getPluginManager().getPlugin("Vault");
		if(test != null){
			log.info("[Skillz] Hooked into Vault, just in case :)");
			return;
		}else{
			if(reward == 0){
				return;
			}
			log.info("[Skillz] Vault not found. Money reward -> 0");
		}
		reward = 0;
	}

	/**
	 * Enables SQLite. Auto-enables after conversion
	 * 
	 */
	public void loadSQL() {
		log.info(logPrefix + "SQLite warming up...");
		dbManager = new SQLite(log, logPrefix, "Skillz", "plugins/Skillz");
		dbManager.open();
		if (!dbManager.checkTable("Skillz")) {
			String query = "CREATE TABLE Skillz ('id' INT PRIMARY KEY, 'player' TEXT NOT NULL, 'skill' TEXT NOT NULL, 'xp' int , 'level' int ) ;";
			dbManager.createTable(query);
			log.info("[Skillz] SQL Database created!");
		}
	}

	public void loadMySQL() {
		mysql = new MySQL(log, logPrefix, dbHost, Integer.toString(3306), dbDB, dbUser, dbPass);
		if (mysql.checkConnection()) {
			log.info(logPrefix + "MySQL connection successful");
			try {
				if(!mysql.checkTable("skillz")){
					log.info("Trying to create Skillz table in MySQL..");
					String query = "CREATE TABLE skillz(id INT PRIMARY KEY, player TEXT NOT NULL, skill TEXT NOT NULL, xp int , level int) ;";
					mysql.createTable(query);
					log.info("Skillz table created (hopefully) succesfully!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				useMySQL = false;
			} 
		} else {
			log.severe(logPrefix + "MySQL connection failed! ");
			useMySQL = false;
		}
	}

	private void loadSettings() {
		YamlConfiguration c = new YamlConfiguration();
		try{
			c.load(settings);
			usePerms = c.getBoolean("plugins.usePermissions", false);
			useIco = c.getBoolean("plugins.useiConomy", false);
			use3Co = c.getBoolean("plugins.use3Co", false);
			useBOSE = c.getBoolean("plugins.useBOSEconomy", false);
			useSQL = c.getBoolean("useSQLite", false);
			useMySQL = c.getBoolean("useMySQL", false);
			lvlupmsg = c.getString("LevelupMessage");
			fallmsg = c.getString("FallDamageMessage");
			version = c.getDouble("version", 5);
			reward = c.getInt("reward.money", 0);
			itemreward = c.getString("reward.item", "0,0");
			dbUser = c.getString("MySQL.username");
			dbPass = c.getString("MySQL.password");
			dbDB = c.getString("MySQL.database");
			dbHost = c.getString("MySQL.host");
			lightning = c.getBoolean("LightningonLevelup", false);
			broadcast = c.getBoolean("BroadcastOnLevelup", true);
			debug = c.getBoolean("debug", false);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void extract(){
		log.info(logPrefix + "Trying to create default config...");
		try {
			JarFile jar = new JarFile("plugins/Skillz.jar");
			ZipEntry entry = jar.getEntry("settings.yml");
			File efile = new File(maindir, entry.getName());
			InputStream in = new BufferedInputStream(jar.getInputStream(entry));
			OutputStream out = new BufferedOutputStream(new FileOutputStream(efile));
			int c;
			while((c = in.read()) != -1){
				out.write(c);
			}
			out.flush();
			out.close();
			in.close();
			jar.close();
			log.info(logPrefix + "Default " + "settings" + " created succesfully!");
		}catch (Exception e) {
			e.printStackTrace();
			log.warning(logPrefix + "Error creating settings file! Using default settings!");
		}
	}

	private void makeSettings() {
		new File(maindir).mkdir();
		if(new File(maindir + "skills/").exists()){
			new File(maindir + "skills/").delete();
		}
		if(new File(maindir + "users/").exists()){
			new File(maindir + "users/").renameTo(new File(maindir + "players"));
		}else{
			new File(maindir + "players/").mkdir();
		}
		if(!settings.exists()){
			extract();
			configed = false;
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
						new SkillCommandHandler().sendSkills(p);
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
						for(String s: args){
							getNextLevel((Player)sender, s);
						}
						return true;					
					}
					if(args[0].equalsIgnoreCase("top")){
						if(args.length == 1){
							sender.sendMessage(ChatColor.RED + "===HighScores===");
							for(SkillBase s:SkillManager.getSkills()){
								sender.sendMessage(high.gethighest(s));
							}
							return true;
						}else if(args.length == 2){
							sender.sendMessage(ChatColor.RED + "===HighScores===");
							if(SkillManager.skills.containsKey(args[1])){
								sender.sendMessage(high.gethighest(SkillManager.skills.get(args[1])));
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
					if(args[0].equalsIgnoreCase("test")){
						SkillzSettings set = api.getSettings();
						sender.sendMessage(itemreward);
						sender.sendMessage(Integer.toString(reward));
						set.setItemReward("10,10");
						set.setMoneyReward(reward++);
						sender.sendMessage(itemreward);
						sender.sendMessage(Integer.toString(reward));
						return true;						
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
					if(new File(maindir + "players/" + args[0].toLowerCase() + ".txt").exists()){
						sender.sendMessage(ChatColor.RED + "===Skillz===");
						getSkills(sender, args[0]);
						return true;
					}else{
						sender.sendMessage(ChatColor.RED + "===Skillz===");
						List<Player> players = getServer().matchPlayer(args[0]);
						if(players.size() == 0){
							sender.sendMessage(ChatColor.RED + "No players available with that name!");
							return true;
						}
						String player = "";
						for(Player p: players){
							player = player + p.getName() + ", ";
						}
						sender.sendMessage(ChatColor.RED + "Did you mean: " + ChatColor.AQUA + player + ChatColor.RED + " or something?");
						return true;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/*private void getSkills(Player name) {
		try {
			FileInputStream in;
			if(new File(maindir + "players/"+ name.getName()+ ".txt").exists()){
				in = new FileInputStream(new File(maindir+ "players/"+ name.getName()+ ".txt"));
			}else{
				if(new File(maindir+ "players/" + name.getName().toLowerCase()+ ".txt").exists()){
					in = new FileInputStream(new File(maindir + "players/"+ name.getName().toLowerCase() + ".txt"));
				}else{
					name.sendMessage("Something went wrong while fetching your skills.");
					return;
				}
			}
			DataInputStream dis = new DataInputStream(in);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				processLine(strLine, name);
			}
			in.close();
			dis.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void processLine(String strLine, Player name){
		if(strLine.startsWith("#")){
			return;
		}
		String[] split = strLine.split("=");
		if(!name.hasPermission("skillz.skill." + split[0])){
			return;
		}
		String[] splitt = split[1].split(";");
		if(split[0].equalsIgnoreCase("axes") || split[0].equalsIgnoreCase("swords") || split[0].equalsIgnoreCase("unarmed")){
			split[0] = split[0] + " Combat";
		}
		name.sendMessage(split[0].substring(0, 1).toUpperCase()
				+ split[0].substring(1).toLowerCase()
				+ " XP: " + ChatColor.RED + splitt[0]
						+ ChatColor.WHITE + " Level: "
						+ ChatColor.RED + splitt[1]);
	}*/

	private void getSkills(CommandSender p, String name) {
		try {
			BufferedReader in1 = new BufferedReader(new FileReader(maindir + "players/" + name.toLowerCase() + ".txt"));
			String str;
			while ((str = in1.readLine()) != null) {
				process(str, p);
			}
			in1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void process(String str, Object p) {
		if(str.startsWith("#")){
			return;
		}
		String[] split = str.split("=");
		String skill = split[0];
		String[] skillz = split[1].split(";");
		if(p instanceof Player){
			Player ps = (Player)p;
			ps.sendMessage(skill.substring(0, 1).toUpperCase() + skill.substring(1).toLowerCase()+ " XP: " + ChatColor.RED + skillz[0] + ChatColor.WHITE + " Level: " + ChatColor.RED + skillz[1]);
		}else if(p instanceof CommandSender){
			CommandSender ps = (CommandSender)p;
			ps.sendMessage(skill.substring(0, 1).toUpperCase() + skill.substring(1).toLowerCase()+ " XP: " + ChatColor.RED + skillz[0] + ChatColor.WHITE + " Level: " + ChatColor.RED + skillz[1]);
		}else{
			log.warning(logPrefix + "Lolwut? Neither a player nor a commandsender wants to know something but asks for it anyway!");
		}
	}

	private void getNextLevel(Player p, String string) {
		string = string.toLowerCase();
		if(string.equalsIgnoreCase("check")){
			return;
		}
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