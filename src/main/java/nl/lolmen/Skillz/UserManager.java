package nl.lolmen.Skillz;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.lolmen.Skills.SkillsSettings;

/**
 *
 * @author Lolmewn <lolmewn@centrility.nl>
 */
public class UserManager {

    private Skillz plugin;
    private HashMap<String, User> users = new HashMap<String, User>();

    public UserManager(Skillz plugin) {
        this.plugin = plugin;
    }

    private Skillz getPlugin() {
        return this.plugin;
    }

    public void loadPlayer(String name) {
        //check if MySQL is enabled
        if (this.getPlugin().useMySQL) {
            ResultSet set = this.getPlugin().getMySQL().executeQuery(
                    "SELECT * FROM " + this.getPlugin().getDatabaseTable() + " WHERE player='"
                    + name + "' ORDER BY skill");
            if (set == null) {
                //Something wrong with database
                this.getPlugin().getLogger().warning("Something wrong with MySQL Resultset while loading " + name + "..");
                this.getPlugin().getLogger().warning("Using empty Player Profile for " + name + "..");
                this.users.put(name, new User(name));
                return;
            }
            try {
                User user = new User(name);
                while (set.next()) {
                    String skill = set.getString("skill");
                    int xp = set.getInt("xp");
                    int lvl = set.getInt("level");
                    user.addData(skill, xp, lvl);
                }
                this.users.put(name, user);
            } catch (SQLException ex) {
                Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        //FlatFile

        try {
            File f = new File("plugins" + File.separator + "Skillz" + File.separator + "players" + File.separator + name.toLowerCase() + ".txt");
            if (!f.exists()) {
                this.users.put(name, new User(name));
                return;
            }
            DataInputStream dis;
            BufferedReader br;
            User user = new User(name);
            FileInputStream in = new FileInputStream(f);
            dis = new DataInputStream(in);
            br = new BufferedReader(new InputStreamReader(dis));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine.contains("#")) {
                    continue;
                }
                if (!strLine.contains("=") || !strLine.contains(";")) {
                    System.out.println("[Skillz] Don't know what to do with '" + strLine + "' in " + f.getAbsolutePath());
                    continue;
                }
                String[] first = strLine.split("=");
                String skill = first[0];
                String[] second = first[1].split(";");
                int xp = Integer.parseInt(second[0]);
                int lvl = Integer.parseInt(second[1]);
                user.addData(skill, xp, lvl);
            }
            dis.close();
            br.close();
            in.close();
            this.users.put(name, user);
        } catch (Exception e) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public User getPlayer(String name) {
        if (this.users.containsKey(name)) {
            return this.users.get(name);
        }
        this.loadPlayer(name);
        if (this.users.containsKey(name)) {
            return this.users.get(name);
        }
        return new User(name);
    }

    public boolean hasPlayer(String name) {
        return this.users.containsKey(name);
    }

    public void save(boolean threaded) {
        if (threaded) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    save(false);
                }
            }).start();
            return;
        }
        //save data
        for (String username : this.users.keySet()) {
            User user = this.users.get(username);
            for (String skill : user.getSkills()) {
                if (this.plugin.useMySQL) {
                    ResultSet set = this.getPlugin().getMySQL().executeQuery("SELECT * FROM " + this.getPlugin().getDatabaseTable() + " WHERE player='" + username + "' AND skill='" + skill + "'");
                    if(set == null){
                        //dafuq?
                        this.getPlugin().getLogger().info("Something went wrong while saving " + username + "'s skill " + skill + " to mysql, Resultset=null");
                        break;
                    }
                    try {
                        boolean found = false;
                        while(set.next()){
                            //Got a value, update it
                            this.plugin.getMySQL().executeStatement("UPDATE " + this.plugin.getDatabaseTable()
                                + " SET xp=" + user.getXP(skill) + ", level=" + user.getLevel(skill)
                                + " WHERE player='" + username + "' AND skill='" + skill + "'");
                            if (SkillsSettings.isDebug()) {
                                this.plugin.getLogger().info("[Debug] Saved " + skill + " for " + username);
                            }
                            found = true;
                            break;
                        }
                        if(found){
                            continue;
                        }
                        //It's not in the table, insert it
                        this.getPlugin().getMySQL().executeStatement("INSERT INTO " + this.getPlugin().getDatabaseTable() + " (player, skill, xp, level) VALUES ('"
                                + username + "', '" + skill + "', " + user.getXP(skill) + ", " + user.getLevel(skill) + ")");
                    } catch (SQLException ex) {
                        Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                } else {
                    File f = new File(plugin.maindir + "players/" + username + ".txt");
                    f.getParentFile().mkdirs();
                    if (!f.exists()) {
                        try {
                            f.createNewFile();
                        } catch (IOException ex) {
                            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
                            this.plugin.getLogger().warning("Couldn't save data for " + username + ", see error above");
                            continue;
                        }
                    }
                    Properties prop = new Properties();
                    try {
                        FileInputStream in = new FileInputStream(f);
                        prop.load(in);
                        prop.put(skill, user.getXP(skill) + ";" + user.getLevel(skill));
                        FileOutputStream out = new FileOutputStream(f);
                        prop.store(out, "Skill=XP;lvl");
                        if (SkillsSettings.isDebug()) {
                            this.plugin.getLogger().info("[Debug] Saved " + skill + " for " + username);
                        }
                        in.close();
                        out.flush();
                        out.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
