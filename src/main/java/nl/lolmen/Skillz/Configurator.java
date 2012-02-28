package nl.lolmen.Skillz;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Configurator {
	Player p;
	Skillz plugin;
	private todo todonext;
	public enum todo{
		start, dbtype, dbhost, dbpass, dbuser, dbport, dbname
	}
	private boolean paused;
	
	public Configurator(Skillz s, Player p){
		this.p = p;
		plugin = s;
		this.start();
	}
	
	public void start(){
		final String[] messages = {"Welcome to the configuration of Skillz", "This will guide you through the config of Skillz", 
				"First, Read all these messages. Then the configuration will start.", "Type start in chat to start the process.", "You can pause at any time by typing 'start'", "NOTE: type start, not /start ;)"};
		for(int i = 0; i < messages.length; i++){
			final int count = i;
			Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
				public void run() {
					p.sendMessage(messages[count]);
				}
			}, count*60 + 60);
		}
		setTodonext(todo.start);
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
			this.p.sendMessage("Starting the configuration of Skillz..");
			this.p.sendMessage("What type of database do you want to use? flatfile/sqlite/mysql");
			this.setTodonext(todo.dbtype);
			return;
		}
		if(this.getTodonext().equals(todo.dbtype)){
			
		}
	}

	public Player getPlayer() {
		return p;
	}

}
