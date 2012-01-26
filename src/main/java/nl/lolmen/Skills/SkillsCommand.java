package nl.lolmen.Skills;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SkillsCommand {
	
	public void sendSkills(Player p){
		new getSkills(p);
	}
}


class getSkills extends Thread {
	Player p;
	public void run() {
		try{
			File f = new File("plugins" +File.separator+ "Skillz"+File.separator+ "players"+File.separator + p.getName().toLowerCase() + ".txt");
			if(!f.exists()){
				p.sendMessage("Something went wrong while trying to fetch your personal file!");
				p.sendMessage("Tell an admin to take a look at his server.log , he'll know what to do ;)");
				System.out.println("[Skillz] File for player " + p.getName() + " not found at " + f.getAbsolutePath());
				return;
			}
			FileInputStream in = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(in);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				if(strLine.contains("#")){
					continue;
				}
				if(!strLine.contains("=") || !strLine.contains(";")) {
					System.out.println("[LittleBigPlugin - Skills] Don't know what to do with '" + strLine + "' in " + f.getAbsolutePath());
					continue;
				}
				String[] first = strLine.split("=");
				String skill = first[0];
				String[] second = first[1].split(";");
				int xp = Integer.parseInt(second[0]);
				int lvl = Integer.parseInt(second[1]);
				int remaining = ((lvl)*(lvl)*10)-xp;
				p.sendMessage(ChatColor.RED + skill.substring(0, 1).toUpperCase() + skill.substring(1).toLowerCase()+ ChatColor.WHITE + " Level: " + ChatColor.GREEN + second[1] + ChatColor.WHITE + " XP: " + ChatColor.GREEN + second[0] + ChatColor.WHITE  + " Remaining: " + ChatColor.GREEN + remaining);
			}
			in.close();
			dis.close();
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public getSkills(Player p) {
		this.p = p;
		this.start();
	}
}
