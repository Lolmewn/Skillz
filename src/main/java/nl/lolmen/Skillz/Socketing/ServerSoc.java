package nl.lolmen.Skillz.Socketing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.bukkit.Bukkit;

import nl.lolmen.Skillz.Skillz;

public class ServerSoc extends Thread{
	
	int thread;
	ServerSocket soc;
	public Skillz plugin;
	Socket incom;
	
	public ServerSoc(Skillz main){
		plugin = main;
		start();
	}
	
	@Override
	public void run(){
		try {
			soc = new ServerSocket(3357);
			soc.setReuseAddress(true);
			if(!soc.isBound()){
				soc.bind(new java.net.InetSocketAddress(3357));
			}
			plugin.log.info("[Skillz] Now listening on port 3357!");
			thread = Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
				public void run(){
					while(soc != null){
						try {
							incom = soc.accept();
							System.out.println("Socket called!");
							new Handler(incom, plugin);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}, 50L);
		} catch (Exception e) {
			//e.printStackTrace();
			plugin.log.warning("[Skillz] Error while listening to port 3357. Already in use?");
		}
	}
}
