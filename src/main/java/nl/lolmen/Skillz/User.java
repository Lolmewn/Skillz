package nl.lolmen.Skillz;

import java.util.HashMap;
import java.util.Set;
import nl.lolmen.Skills.SkillsSettings;
import org.bukkit.Bukkit;

/**
 *
 * @author Lolmewn <info@lolmewn.nl>
 */
public class User {
    
    private String username;
    private HashMap<String, Integer> xp = new HashMap<String, Integer>();
    private HashMap<String, Integer> lvl = new HashMap<String, Integer>();
    
    public User(String name){
        this.username = name;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public void addData(String skill, int xp, int level){
        this.xp.put(skill, xp);
        this.lvl.put(skill, level);
    }
    
    public int getXP(String skill){
        if(this.xp.containsKey(skill)){
            return this.xp.get(skill);
        }
        if(SkillsSettings.isDebug()){
            Bukkit.getLogger().info("[Skillz][Debug] " + skill + " not found in xp, returning 0");
        }
        return 0;
    }
    
    public int getLevel(String skill){
        if(this.lvl.containsKey(skill)){
            return this.lvl.get(skill);
        }
        if(SkillsSettings.isDebug()){
            Bukkit.getLogger().info("[Skillz][Debug] " + skill + " not found in lvl, returning 0");
        }
        return 0;
    }
    
    public Set<String> getSkills(){
        return this.xp.keySet();
    }
    
    public int addXP(String skill, int add){
        if(SkillsSettings.isDebug()){
            Bukkit.getLogger().info("[Skillz][Debug] Adding " + add + " xp to " + skill );
        }
        if(this.xp.containsKey(skill)){
            return this.xp.put(skill, this.xp.get(skill) + add);
        }
        return this.xp.put(skill, add);
    }
    
    public int addLevel(String skill){
        return this.addLevel(skill, 1);
    }
    
    public int addLevel(String skill, int add){
        if(SkillsSettings.isDebug()){
            Bukkit.getLogger().info("[Skillz][Debug] Adding " + add + " lvl to " + skill );
        }
        if(this.lvl.containsKey(skill)){
            return this.lvl.put(skill, this.lvl.get(skill) + add);
        }
        return this.lvl.put(skill, add);
    }

}
