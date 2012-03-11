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
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;
import nl.lolmen.API.SkillzAPI;
import nl.lolmen.Skills.CPU;
import nl.lolmen.Skills.CustomSkillManager;
import nl.lolmen.Skills.SkillBase;
import nl.lolmen.Skills.SkillBlockListener;
import nl.lolmen.Skills.SkillsCommand;
import nl.lolmen.Skills.SkillEntityListener;
import nl.lolmen.Skills.SkillManager;
import nl.lolmen.Skills.SkillPlayerListener;
import nl.lolmen.Skills.SkillsSettings;
import nl.lolmen.Skillz.Metrics.Plotter;
//import nl.lolmen.Skillz.Socketing.ServerSoc;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
	public Logger log = null;

	//File stuff
	public String maindir = "plugins" + File.separator + "Skillz" + File.separator;
	public File skillzFile = new File(maindir + "skills.yml");

	//public static Skillz p;

	//Classes
	private Convert converter = new Convert(this);
	public HighScore high = new HighScore();
	private SkillBlockListener block = new SkillBlockListener(this);
	private SkillPlayerListener player = new SkillPlayerListener(this);
	private SkillEntityListener entity = new SkillEntityListener(this);
	public FastBreak fb = new FastBreak();
	public static SkillzAPI api = new SkillzAPI();
	private Metrics metrics;
	private SkillManager skillManager;
	private CustomSkillManager customManager;
	private MySQL mysql = null;

	//For faster block breaking
	public HashMap<Player, Block> FBlock = new HashMap<Player, Block>();
	public HashMap<Player, Integer> FCount = new HashMap<Player, Integer>();

	//Settings
	public boolean useMySQL = false;
	protected String dbHost;
	protected String dbPass;
	protected String dbUser;
	protected String dbName;
	public String dbTable;
	protected int dbPort;
	public String noPerm = ChatColor.RED + "You do not have Permissions to do this!";
	public double version;

	public boolean update;
	public boolean debug;

	public boolean updateAvailable;
	public boolean hasVault;
	public boolean broadcast;

	public void onDisable() {
		if ((this.useMySQL) && (this.mysql != null)) {
			this.mysql.close();
		}
		this.high.saveMaps();
		if(this.updateAvailable){
			this.downloadFile("http://dl.dropbox.com/u/7365249/Skillz.jar");
		}
		this.getServer().getScheduler().cancelTasks(this);
		this.log.info("Disabled!");
	}
	
	private void downloadFile(String site){
		try {
			this.log.info("Updating Skillz.. Please wait.");
			BufferedInputStream in = new BufferedInputStream(new URL(site).openStream());
			FileOutputStream fout = new FileOutputStream(nl.lolmen.Skillz.Skillz.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			byte data[] = new byte[1024]; //Download 1 KB at a time
			int count;
			while((count = in.read(data, 0, 1024)) != -1)
			{
				fout.write(data, 0, count);
			}
			this.log.info("Skillz has been updated!");
			in.close();
			fout.close();
			YamlConfiguration c = new YamlConfiguration();
			try{
				c.load(this.skillzFile);
				c.set("version", this.version);
				c.save(this.skillzFile);
			}catch(Exception e){
				e.printStackTrace();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void onEnable() {
		double time = System.nanoTime();
		this.log = this.getLogger();
		this.makeSettings();
		this.loadSkillz();
		try {
			this.metrics = new Metrics();
			this.metrics.addCustomData(this, new Plotter(){

				@Override
				public String getColumnName() {
					return "Total Level-Ups";
				}

				@Override
				public int getValue() {
					int amount = CPU.levelUps;
					CPU.levelUps = 0;
					return amount;
				}
			});
			this.metrics.addCustomData(this, new Plotter(){
				@Override
				public String getColumnName() {
					return "Total XP-Gained";
				}

				@Override
				public int getValue() {
					int amount = CPU.xpUps;
					CPU.xpUps = 0;
					return amount;
				}
			});
			this.metrics.beginMeasuringPlugin(this);
			this.getLogger().info("Metrics loaded! View them @ http://metrics.griefcraft.com/plugin/Skillz");
		} catch (IOException e) {
			e.printStackTrace();
			this.getLogger().info("Failed to load Metrics!");
		}
		if(this.update){
			checkUpdate();
		}
		if(this.useMySQL){
			loadMySQL();
		}
		this.setupPlugins();
		high.loadMaps();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(block, this);
		pm.registerEvents(entity, this);
		pm.registerEvents(player, this);
		double end = System.nanoTime();
		double taken = (end - time) / 1000000;
		this.getLogger().info("version " + this.version + " build " + getDescription().getVersion() + " enabled - took " + taken + "ms!");
	}

	private void loadSkillz() {
		this.skillManager = new SkillManager(this);
		this.skillManager.loadSkillsSettings();
		this.customManager = new CustomSkillManager(this);
		this.customManager.loadCustomSkills();
		YamlConfiguration c = new YamlConfiguration();
		try{
			c.load(skillzFile);
			this.version = c.getDouble("version", 5.51);
			this.update = c.getBoolean("update", true);
			this.dbUser = c.getString("MySQL-User", "root");
			this.dbPass = c.getString("MySQL-Pass", "root");
			this.dbHost = c.getString("MySQL-Host", "localhost");
			this.dbPort = c.getInt("MySQL-Port", 3306); 
			this.dbName = c.getString("MySQL-Database", "minecraft");
			this.dbTable = c.getString("MySQL-Table", "Skillz");
			this.useMySQL = c.getBoolean("useMySQL", false);
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
					this.log.info("An update is available! Will be downloaded on Disable! Old version: " + this.version + " New version: " + str);
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
	
	public SkillManager getSkillManager(){
		return this.skillManager;
	}
	
	public CustomSkillManager getCustomSkillManager(){
		return this.customManager;
	}
	
	public MySQL getMySQL(){
		return this.mysql;
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
			log.info("Hooked into Vault, just in case :)");
			return;
		}else{
			if(SkillsSettings.getMoneyOnLevelup() == 0){
				return;
			}
			log.warning("Vault not found. Money reward -> 0");
			SkillsSettings.setMoneyOnLevelup(0);
		}
	}

	public void loadMySQL() {
		this.mysql = new MySQL(this.dbHost, this.dbPort, this.dbUser, this.dbPass, this.dbName, this.dbTable);
		if(this.mysql.isFault()){
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
					sender.sendMessage(ChatColor.RED + "===Skillz===");
					new SkillsCommand().sendSkills(p, this);
					return true;
				}
				if(args.length == 1 || args.length > 1){
					if(args[0].equalsIgnoreCase("check")){
						if(!sender.hasPermission("skillz.check")){
							sender.sendMessage(noPerm);
							return true;
						}
						if(!(sender instanceof Player)){
							sender.sendMessage(ChatColor.RED + "Only players can use this option!");
							return true;
						}
						for(SkillBase s : this.skillManager.getSkills()){
							if(!s.isEnabled()){
								continue;
							}
							this.getNextLevel((Player)sender, s.getSkillName());
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
									if(to.equalsIgnoreCase("mysql")){
										if(converter.flatToMySQL()){
											sender.sendMessage("Conversion succesful! Using MySQL now!");
											return true;
										}else{
											sender.sendMessage("Something went wrong! Check the log!");
											return true;
										}
									}
									sender.sendMessage("Not sure what you ment by " + to);
									return true;
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
									sender.sendMessage("Not sure what you ment by " + to);
									return true;
								}
								sender.sendMessage("Not sure what you ment by " + from);
								return true;
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
							sender.sendMessage("Please specify the page you want to see!");
							return true;
						}
						try{
							int page = Integer.parseInt(args[1]);
							new SkillsCommand().sendSkills((Player)sender, page, this);
							return true;
						}catch(Exception e){
							sender.sendMessage("Page must be an int!");
							return true;
						}
					}
					if(args[0].equalsIgnoreCase("config")){
						if(sender.isOp()){
							if(this.skillManager.beingConfigged){
								sender.sendMessage("Someone is already configging the plugin!");
								return true;
							}
							if(this.skillManager.configed){
								sender.sendMessage("Skillz already is configed! Edit skills.yml to edit");
								return true;
							}
							if(!(sender instanceof Player)){
								sender.sendMessage("You have to be player to use this command!");
								return true;
							}
							this.skillManager.configger = new Configurator(this, (Player)sender);
							this.skillManager.beingConfigged = true;
						}else{
							sender.sendMessage("You have to be OP to use this command!");
						}
						return true;
					}

					//Get another players Skills
					if(!sender.hasPermission("skillz.skills.other")){
						sender.sendMessage(noPerm);
						return true;
					}
					int page = 1;
					try{
						page = args.length == 2 ? Integer.parseInt(args[1]) : 1;
					}catch(Exception e){
						page = 1;
					}
					sender.sendMessage(ChatColor.RED + "===Skillz===");
					Player p = getServer().getPlayer(args[0]);
					if(p == null){
						OfflinePlayer p2 = this.getServer().getOfflinePlayer(args[0]);
						if(p2 == null){
							sender.sendMessage("No player available by that name: " + args[0]);
							return true;
						}
						new SkillsCommand().sendSkills(sender, p2.getName(), page, this);
						return true;
					}
					new SkillsCommand().sendSkills(sender, p, page, this);
					return true;
				}
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	private void getNextLevel(final Player p, final String strings) {
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				String string = strings.toLowerCase();
				if(useMySQL){
					try {
						String query = "SELECT * FROM " + dbTable + " WHERE player = '" + p.getName() + "' AND skill = '"+ string + "';";
						ResultSet set = mysql.executeQuery(query);
						if(set == null){
							p.sendMessage(ChatColor.RED + "There is no such skill: " + string);
							return;
						}
						while(set.next()){
							int xp = set.getInt("xp");
							int lvl = set.getInt("level");
							int remaining = ((lvl)*(lvl)*10)-xp;
							p.sendMessage("XP remaining for " + ChatColor.RED + string + ChatColor.WHITE + ": " + ChatColor.RED + Integer.toString(remaining));
							return;
						}
						p.sendMessage(ChatColor.RED + "There is no such skill: " + string);
						return;
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
		});
		t.run();
	}
}
