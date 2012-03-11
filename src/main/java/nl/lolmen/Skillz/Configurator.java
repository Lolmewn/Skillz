package nl.lolmen.Skillz;

import nl.lolmen.Skills.SkillsSettings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Configurator {
	private Player p;
	private Skillz plugin;
	private todo todonext;
	public enum todo{
		start, dbtype, dbhost, dbpass, dbuser, dbport, dbname, moneyReward, itemReward, dbtable
	}
	private boolean paused;
	
	public Configurator(Skillz s, Player p){
		this.p = p;
		this.plugin = s;
		this.start();
	}
	
	public void start(){
		final String[] messages = {"Welcome to the configuration of Skillz", "This will guide you through the config of Skillz", 
				"First, Read all these messages. Then the configuration will start.", "You can pause at any time by typing 'pause'", "Anything you say won't be seen by others",
				"Also, you won't be able to see other's messages ", "Type start in chat to start the process, stop to stop it and pause to pause it"};
		for(int i = 0; i < messages.length; i++){
			final int count = i;
			this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, new Runnable(){
				public void run() {
					p.sendMessage(messages[count]);
				}
			}, count*60);
		}
		this.setTodonext(todo.start);
	}
	
	public todo getTodonext() {
		return todonext;
	}

	public void setTodonext(todo todonext) {
		this.todonext = todonext;
	}
	
	public void handleInput(String input){
		if(input.equals("pause")){
			this.p.sendMessage("Pausing configuration, type /skills config to resume");
		}
		if(input.equalsIgnoreCase("start") && this.getTodonext().equals(todo.start)){
			this.p.sendMessage(ChatColor.GRAY + "Starting the configuration of Skillz..");
			this.p.sendMessage("What type of database do you want to use? "+ ChatColor.RED + "flatfile/sqlite/mysql");
			this.setTodonext(todo.dbtype);
			return;
		}
		if(this.getTodonext().equals(todo.dbtype)){
			if(input.startsWith("my")){
				this.plugin.useMySQL = true;
				this.setTodonext(todo.dbhost);
				this.p.sendMessage("What is the database host? Usually localhost");
				return;
			}
			if(input.startsWith("flat")){
				this.plugin.useMySQL = false;
				this.setTodonext(todo.moneyReward);
				this.p.sendMessage("How much money should a player get when he levels up?");
				return;
			}
			this.p.sendMessage("Not sure what database type this is: " + input);
			return;
		}
		if(this.getTodonext().equals(todo.dbhost)){
			this.plugin.dbHost = input;
			this.setTodonext(todo.dbport);
			this.p.sendMessage("What port is the database running on?");
			return;
		}
		if(this.getTodonext().equals(todo.dbport)){
			try{
				int port = Integer.parseInt(input);
				this.plugin.dbPort = port;
				this.setTodonext(todo.dbuser);
				this.p.sendMessage("What's the username of the database?");
				return;
			}catch(NumberFormatException e){
				this.p.sendMessage("Cannot convert " + input + " to int. Please use a number.");
				return;
			}
		}
		if(this.getTodonext().equals(todo.dbuser)){
			this.plugin.dbUser = input;
			this.setTodonext(todo.dbpass);
			this.p.sendMessage("What's the password of the database?");
			return;
		}
		if(this.getTodonext().equals(todo.dbpass)){
			plugin.dbPass = input;
			this.setTodonext(todo.dbname);
			this.p.sendMessage("What's the database's table? Usually minecraft");
			return;
		}
		if(this.getTodonext().equals(todo.dbname)){
			this.plugin.dbName = input;
			this.setTodonext(todo.dbtable);
			this.p.sendMessage("What name do you want to give the Skills table?");
			return;
		}
		if(this.getTodonext().equals(todo.dbtable)){
			this.plugin.dbTable = input;
			this.setTodonext(todo.moneyReward);
			this.p.sendMessage("That's it for the database. How much money should a player get when he levels up?");
			return;
		}
		if(this.getTodonext().equals(todo.moneyReward)){
			try{
				int port = Integer.parseInt(input);
				SkillsSettings.setMoneyOnLevelup(port);
				this.setTodonext(todo.itemReward);
				this.p.sendMessage("What items should a player get when they level up? \nItemID;Amount is the format, 89;2 is 2 glowstone for example.");
				return;
			}catch(NumberFormatException e){
				this.p.sendMessage("Cannot convert " + input + " to int. Please use a number.");
				return;
			}
		}
		if(this.getTodonext().equals(todo.itemReward)){
			if(!input.contains(";")){
				this.p.sendMessage("Error in itemReward! It should contain a ; !\nItemID;Amount is the format, 89;2 is 2 glowstone for example.");
				return;
			}
			String[] split = input.split(";");
			try{
				Integer.parseInt(split[0]);
				Integer.parseInt(split[1]);
				SkillsSettings.setItemOnLevelup(input);
				this.p.sendMessage("That was it for the config, you can always edit skills.yml to change it.");
				this.plugin.getSkillManager().configed = true;
				this.plugin.getSkillManager().beingConfigged = false;
			}catch(Exception e){
				if(SkillsSettings.isDebug()){
					e.printStackTrace();
				}
				this.p.sendMessage("There's something wrong with the input! \nItemID;Amount is the format, 89;2 is 2 glowstone for example.");
			}
		}
	}

	public Player getPlayer() {
		return p;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

}
