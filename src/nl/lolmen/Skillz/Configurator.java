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
	
	public Configurator(Skillz s, Player p){
		this.p = p;
		plugin = s;
	}
	
	public void start(){
		final String[] messages = {"Welcome to the configuration of Skillz", "Don't worry, this will only happen once ;)", "This will guide you through the config of Skillz", 
				"First, Read all these messages. Then the configuration will start.", "Type start in chat to start the process.", "NOTE: type start, not /start ;)"};
		for(int i = 0; i < messages.length; i++){
			final int count = i;
			Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
				@Override
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

	public Player getPlayer() {
		return p;
	}

}
