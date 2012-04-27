package nl.lolmen.Skillz;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                    + name + "' LIMIT 1");
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
            try {
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
            } catch (Exception e) {
                Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (Exception e) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public User getPlayer(String name){
        if(this.users.containsKey(name)){
            return this.users.get(name);
        }
        return new User(name);
    }
}
