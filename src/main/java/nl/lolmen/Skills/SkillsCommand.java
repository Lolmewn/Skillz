package nl.lolmen.Skills;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SkillsCommand {
	
	public void sendSkills(Player p){
		sendSkills(p, 1);
	}
	
	public void sendSkills(Player p, int page){
		new getSkills(p, page);
	}
}


class getSkills extends Thread {
	Player p;
	int page;
	public void run() {
		Map<Integer, SkillData> data = new HashMap<Integer, SkillData>();
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
			int count = 0;
			int totalXP = 0, totalLVL = 0;
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
			if(!(data.size() > page * 10 - 10)){
				p.sendMessage(ChatColor.RED + "There is no page " + page + "!");
				return;
			}
			for(int i = 0; i < page * 10; i++){
				int get = i + (page-1)*10;
				if(data.containsKey(get)){
					SkillData d = data.get(get);
					p.sendMessage(ChatColor.RED + d.getSkill()+ ChatColor.WHITE + " Level: " + ChatColor.GREEN + d.getLVL() + ChatColor.WHITE + " XP: " + ChatColor.GREEN + d.getXP() + ChatColor.WHITE  + " Remaining: " + ChatColor.GREEN + d.getRem());
				}else{
					if(SkillsSettings.isDebug()){
						p.sendMessage("[Skillz - Debug] No value: " + get);
					}
				}
			}
			p.sendMessage(ChatColor.RED + "Total Level: " + ChatColor.GREEN + totalLVL + ChatColor.RED + " Total XP: " + ChatColor.GREEN + totalXP);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public getSkills(Player p) {
		this.p = p;
		this.start();
	}
	public getSkills(Player p2, int page) {
		this.page = page;
		this.p = p2;
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
