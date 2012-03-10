package nl.lolmen.Skills;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import nl.lolmen.Skillz.Skillz;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillsCommand {
	
	public void sendSkills(Player p, Skillz plugin){
		this.sendSkills(p, 1,plugin);
	}
	
	public void sendSkills(Player p, int page, Skillz plugin){
		this.sendSkills(p, p, page,plugin);
	}
	
	public void sendSkills(CommandSender sender, Player p, Skillz plugin){
		this.sendSkills(sender, p, 1,plugin);
	}
	
	public void sendSkills(CommandSender sender, Player p , int page, Skillz plugin){
		new getSkills(sender, p, page, plugin);
	}
}


class getSkills extends Thread {
	private Player p;
	private int page;
	private CommandSender sender;
	private Skillz plugin;
	
	
	public void run() {
		Map<Integer, SkillData> data = new HashMap<Integer, SkillData>();
		if(SkillsSettings.isDebug()){
			if(sender instanceof Player){
				System.out.println("Fetching file from " + p.getDisplayName() + " to " + ((Player)sender).getDisplayName());
			}
			sender.sendMessage("Fetching file from " + p.getDisplayName());
		}
		int count=0, totalXP=0, totalLVL=0;
		if(plugin.useMySQL){
			ResultSet set = plugin.mysql.executeQuery("SELECT * FROM " + plugin.dbTable + " WHERE player='" + p.getName() + "' ORDER BY skill DESC");
			if(set==null){
				System.out.println("Something went wrong while reading the MySQL database.");
				return;
			}
			try {
				while(set.next()){
					String skill = set.getString("skill").toLowerCase();
					data.put(count, new SkillData(skill.startsWith("axes") || skill.startsWith("swords") || skill.startsWith("unarmed") ? skill + " Combat" : skill, set.getInt("xp"), set.getInt("level"), (int)Math.pow(set.getInt("level"), 2) * 10 - set.getInt("xp")));
					totalXP+=set.getInt("xp");
					totalLVL+=set.getInt("level");
					if(SkillsSettings.isDebug()){
						System.out.println("[Skillz - Debug] Added " + skill);
					}
					count++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			try{
				File f = new File("plugins" +File.separator+ "Skillz"+File.separator+ "players"+File.separator + p.getName().toLowerCase() + ".txt");
				if(!f.exists()){
					sender.sendMessage("Something went wrong while trying to fetch your personal file!");
					sender.sendMessage("Tell an admin to take a look at his server.log , he'll know what to do ;)");
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
						System.out.println("[Skillz] Don't know what to do with '" + strLine + "' in " + f.getAbsolutePath());
						continue;
					}
					String[] first = strLine.split("=");
					String skill = first[0];
					if(skill.startsWith("axes") || skill.startsWith("swords") || skill.startsWith("unarmed")){
						skill+=" Combat";
					}
					String[] second = first[1].split(";");
					int xp = Integer.parseInt(second[0]);
					int lvl = Integer.parseInt(second[1]);
					int remaining = ((lvl)*(lvl)*10)-xp;
					totalXP+=xp;
					totalLVL+=lvl;
					data.put(count, new SkillData(skill, xp, lvl, remaining));
					count++;
				}
				in.close();
				dis.close();
				br.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(!(data.size() > page * 8 - 8)){
			sender.sendMessage(ChatColor.RED + "There is no page " + page + "!");
			return;
		}
		for(int i = 0; i < page * 8; i++){
			int get = i + (page-1)*8;
			if(data.containsKey(get)){
				SkillData d = data.get(get);
				double percent = 100 - (d.getRem() / (Math.pow(d.getLVL(), 2) * 10 - Math.pow(d.getLVL() - 1, 2) * 10) * 100);
				int stripes = (int)percent / (100/20); //Draws the red stripes
				if(d.getLVL() == 0){
					stripes = 0;
				}
				if(SkillsSettings.isDebug()){
					System.out.println("[Skillz - Debug] Percent: " + percent + " stripes: " + stripes);
				}
				StringBuilder str = new StringBuilder();
				str.append(ChatColor.WHITE + "[");
				for(int b = 0; b < stripes; b++){
					str.append(ChatColor.GREEN + "|");
				}
				for(int a = 0; a < 20 - stripes; a++){
					str.append(ChatColor.RED + "|");
				}
				str.append(ChatColor.WHITE + "]");
				sender.sendMessage(ChatColor.RED + d.getSkill()+ ChatColor.WHITE + " Level: " + ChatColor.GREEN + d.getLVL() + ChatColor.WHITE + " XP: " + ChatColor.GREEN + d.getXP()  + " " + str.toString());
			}else{
				if(SkillsSettings.isDebug()){
					sender.sendMessage("[Skillz - Debug] No value: " + get);
				}
			}
		}
		sender.sendMessage(ChatColor.RED + "Total Level: " + ChatColor.GREEN + totalLVL + ChatColor.RED + " Total XP: " + ChatColor.GREEN + totalXP);
	}
	
	public getSkills(CommandSender sender, Player p, int page, Skillz plugin){
		this.page = page;
		this.p = p;
		this.sender = sender;
		this.plugin = plugin;
		this.start();
	}
}

class SkillData{
	private String s;
	private int x,l,r;
	public SkillData(String skill, int xp, int lvl, int rem){
		setS(skill);
		setX(xp);
		setL(lvl);
		setR(rem);
	}
	public String getSkill() {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
	public void setS(String s) {
		this.s = s;
	}
	public int getXP() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getLVL() {
		return l;
	}
	public void setL(int l) {
		this.l = l;
	}
	public int getRem() {
		return r;
	}
	public void setR(int r) {
		this.r = r;
	}
}
