package nl.lolmen.Skills.skills;

import nl.lolmen.Skills.CPU;
import nl.lolmen.Skills.SkillBlockBase;
import nl.lolmen.Skillz.Skillz;

import org.bukkit.entity.Player;

public class Mining extends SkillBlockBase {

    private Skillz plugin;

    public Mining(Skillz plugin) {
        super(plugin);
        this.plugin = plugin;
    }
    private int speed;
    private int doubleDropChange;

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean getWillDoubleDrop(Player p) {
        if (this.plugin.getUserManager().getPlayer(p.getName()).getLevel(this.getSkillName()) > Math.random() * doubleDropChange) {
            return true;
        }
        return false;
    }

    public void setDoubleDropChange(int doubleDropChange) {
        this.doubleDropChange = doubleDropChange;
    }
}