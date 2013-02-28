package nl.lolmen.Skillz;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import nl.lolmen.Skills.SkillBase;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HighScore {

    HashMap<String, data> map = new HashMap<String, data>();
    private File mapFile = new File("plugins/Skillz/highscore.dat");

    public HighScore() {
    }

    public void checkScore(Player p, SkillBase skill, int lvl) {
        //If it doesn't contain it, return.
        if (!map.containsKey(skill.getSkillName())) {
            map.put(skill.getSkillName(), new data(p.getName(), lvl));
            return;
        }
        data get = map.get(skill.getSkillName());
        int have = get.getLevel();
        if (lvl > have) {
            map.remove(skill.getSkillName());
            map.put(skill.getSkillName(), new data(p.getName(), lvl));
        }
    }

    public String gethighest(SkillBase skills) {
        //If it doesn't contain it, return
        String skill = skills.getSkillName().toLowerCase();
        if (!map.containsKey(skill)) {
            return "No top player for " + ChatColor.RED + skill.substring(0, 1).toUpperCase() + skill.substring(1).toLowerCase() + ChatColor.WHITE + "!";
        }
        data get = map.get(skill);
        String p = get.getPlayer();
        if (skill.equalsIgnoreCase("axes") || skill.equalsIgnoreCase("swords") || skill.equalsIgnoreCase("unarmed")) {
            skill = skill + " Combat";
        }
        return ChatColor.GREEN + skill.substring(0, 1).toUpperCase() + skill.substring(1).toLowerCase() + ": " + ChatColor.RED + p + ChatColor.WHITE + " with level " + ChatColor.RED + get.getLevel() + ChatColor.WHITE + "!";
    }

    public void saveMaps() {
        Properties prop = new Properties();

        HashMap<String, String> newmap = new HashMap<String, String>();
        Set<String> set = map.keySet();
        Iterator<String> itr = set.iterator();
        while (itr.hasNext()) {
            String get = itr.next();
            data d = map.get(get);
            newmap.put(get, d.getPlayer() + "," + Integer.toString(d.getLevel()));
        }

        try {
            prop.putAll(newmap);
            if (!mapFile.exists()) {
                mapFile.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(mapFile);
            prop.store(out, "HighScore File, DO NOT CHANGE!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMaps() {
        if (!mapFile.exists()) {
            return;
        }
        if (!map.isEmpty()) {
            System.out.println("[Skillz] Aparently the highscores have already been loaded!");
            return;
        }
        try {
            FileInputStream in = new FileInputStream(mapFile);
            DataInputStream dis = new DataInputStream(in);
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                processLine(strLine);
            }
            in.close();
            dis.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processLine(String strLine) {
        if (strLine.startsWith("#")) {
            return;
        }
        String[] splot = strLine.split("=");
        String[] datas = splot[1].split(",");
        map.put(splot[0], new data(datas[0], Integer.parseInt(datas[1])));
        System.out.println("[Skillz] Loaded HighScore " + splot[0] + ", " + datas[0] + ", " + datas[1] + "!");
    }
}

class data {

    String p;
    int lvl;

    public data(String p, int lvl) {
        this.p = p;
        this.lvl = lvl;
    }

    public String getPlayer() {
        return p;
    }

    public int getLevel() {
        return lvl;
    }

    @Override
    public String toString() {
        return p + " " + Integer.toString(lvl);
    }
}
