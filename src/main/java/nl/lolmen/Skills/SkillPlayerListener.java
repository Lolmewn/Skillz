package nl.lolmen.Skills;

import nl.lolmen.Skillz.Skillz;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class SkillPlayerListener implements Listener {

    private Skillz plugin;

    public SkillPlayerListener(Skillz main) {
        plugin = main;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        double time = System.nanoTime();
        Player p = event.getPlayer();
        if (!this.plugin.getSkillManager().configed && p.isOp()) {
            p.sendMessage("To configure Skillz, type /skills config");
            if (SkillsSettings.isDebug()) {
                this.plugin.getLogger().info("[DEBUG] Asked " + p.getName() + " to config Skillz");
            }
        }
        
        this.plugin.getUserManager().loadPlayer(p.getName());
        double time2 = System.nanoTime();
                double taken = (time2 - time) / 1000000.0D;
                this.plugin.getLogger().info("Loaded player in " + Double.toString(taken) + "ms!");
        /*
        if (!this.plugin.useMySQL && (!new File(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt").exists())) {
            try {
                this.plugin.getLogger().info("[Skillz] File created for " + p.getName().toLowerCase() + "!");
                new File(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt").createNewFile();
                Properties prop = new Properties();
                FileInputStream in = new FileInputStream(new File(plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt"));
                prop.load(in);
                for (SkillBase skill : this.plugin.getSkillManager().getSkills()) {
                    if (!skill.isEnabled()) {
                        continue;
                    }
                    prop.put(skill.getSkillName(), "0;0");
                }
                FileOutputStream out = new FileOutputStream(new File(this.plugin.maindir + "players/" + p.getName().toLowerCase() + ".txt"));
                prop.store(out, "Skill=XP;lvl");
                in.close();
                out.flush();
                out.close();
                double time2 = System.nanoTime();
                double taken = (time2 - time) / 1000000.0D;
                this.plugin.getLogger().info("Creation took " + Double.toString(taken) + "ms!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (this.plugin.useMySQL) {
            try {
                String query = "SELECT * FROM " + this.plugin.dbTable + " WHERE player='" + p.getName() + "';";
                ResultSet res = this.plugin.getMySQL().executeQuery(query);
                if (res != null) {
                    while (res.next()) {
                        return; //is in the database, no need to add him
                    }
                    //not in the DB after all
                }
                for (SkillBase skill : plugin.getSkillManager().getSkills()) {
                    if (!skill.isEnabled()) {
                        continue;
                    }
                    this.plugin.getMySQL().executeQuery("INSERT INTO " + this.plugin.dbTable + " (player, skill, xp, level) VALUES ('" + p.getName() + "', '" + skill.getSkillName() + "', 0, 0);");
                }
                this.plugin.getLogger().info("[Skillz] MySQL Entry created for " + p.getName() + "!");
                double time2 = System.nanoTime();
                double taken = (time2 - time) / 1000000.0D;
                this.plugin.getLogger().info("Creation took " + Double.toString(taken) + "ms!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        if (event.isCancelled()) {
            return;
        }
        this.plugin.fb.playerAnimate(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled() || this.plugin.getSkillManager().configed) {
            return;
        }
        if (this.plugin.getSkillManager().beingConfigged && event.getPlayer().isOp() && this.plugin.getSkillManager().configger.getPlayer().getName().equals(event.getPlayer().getName()) && !this.plugin.getSkillManager().configger.isPaused()) {
            //Don't send messages from the configger to others
            event.setCancelled(true);
            event.getPlayer().sendMessage(this.plugin.getSkillManager().configger.getTodonext().name().toLowerCase() + ": " + event.getMessage());
            this.plugin.getSkillManager().configger.handleInput(event.getMessage());
            return;
        }
        if (this.plugin.getSkillManager().beingConfigged && !this.plugin.getSkillManager().configger.isPaused()) {
            //Don't send messages from others to the person configging
            event.getRecipients().remove(Bukkit.getServer().getPlayer(this.plugin.getSkillManager().configger.getPlayer().getName()));
        }
    }
}
