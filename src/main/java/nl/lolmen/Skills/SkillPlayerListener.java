package nl.lolmen.Skills;

import nl.lolmen.Skillz.Skillz;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
        if(!this.plugin.getUserManager().hasPlayer(p.getName())){
            this.plugin.getLogger().info("Loading player " + event.getPlayer().getName() + "...");
            this.plugin.getUserManager().loadPlayer(p.getName());
        }        
        double time2 = System.nanoTime();
        double taken = (time2 - time) / 1000000.0D;
        this.plugin.getLogger().info("Loaded player in " + Double.toString(taken) + "ms!");
    }

    /*@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        if (event.isCancelled()) {
            return;
        }
        this.plugin.fb.playerAnimate(event.getPlayer());
    }*/

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
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
