package nl.lolmen.Skillz.Socketing;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;

import nl.lolmen.Skillz.Skillz;

public class Handler extends Thread{
	Socket s;
	Skillz plugin;
	public Handler(Socket s, Skillz plug){
		this.s = s;
		plugin = plug;
		this.start();
	}
	
	@Override
	public void run(){
		try{
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String want = in.readLine();
			System.out.println(want);
			if(want == null || want.equalsIgnoreCase("")){
				System.out.println("[Skillz] Null sent on Socket, doing nothing with it.");
				return;
			}
			if(want.startsWith("SEND")){
				String important = want.substring(5);
				//Sends back what it has received
				out.println("RECEIVE " + important);
				if(important.equalsIgnoreCase("need player list")){
					if(!plugin.useSQL && !plugin.useMySQL){
						File dir = new File(plugin.maindir + "players/");
						File filename[] = dir.listFiles();
						for (int i = 0; i > filename.length; i++){
							String file = filename[i].getName();
							String pname = file.substring(0, file.lastIndexOf("."));
							out.println(pname.getBytes());
						}
					}
					if(plugin.useSQL && !plugin.useMySQL){
						String query = "SELECT * FROM Skillz;";
						ResultSet set = plugin.dbManager.query(query);
						boolean exists = set.next();
						if (!exists) {
							out.println("No players to display, sorry!");
						}
						while (set.next()) {
							out.println(set.getString("player"));
						}
					}else{
						String query = "SELECT * FROM Skillz;";
						ResultSet set = plugin.mysql.query(query);
						boolean exists = set.next();
						if (!exists) {
							out.println("No players to display, sorry!");
						}
						while (set.next()) {
							out.println(set.getString("player"));
						}
					}
				}else{
					out.println("something else");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
