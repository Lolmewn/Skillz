package nl.lolmen.Skills.skills;

import java.util.Random;

import org.bukkit.Bukkit;

import nl.lolmen.Skills.SkillBase;
import nl.lolmen.Skills.SkillsSettings;
import nl.lolmen.Skillz.MathProcessor;
import nl.lolmen.Skillz.Skillz;

public class Swimming extends SkillBase {

    public Swimming(Skillz plugin) {
        super(plugin);
    }
    private String noDrownChance;

    public String getNoDrownChance() {
        return noDrownChance;
    }

    public void setNoDrownChance(String noDrownChance) {
        this.noDrownChance = noDrownChance;
    }

    public boolean wontDrown(int level) {
        int chance = this.getNoDrownChance(level);
        Random rant = new Random();
        int result = rant.nextInt(100);
        return result < chance ? true : false;
    }

    public int getNoDrownChance(int level) {
        if (noDrownChance != null && noDrownChance != "" && noDrownChance.contains("$LEVEL")) {
            String send = noDrownChance.replace("$LEVEL", Integer.toString(level));
            return (int) MathProcessor.processEquation(send);
        } else if (SkillsSettings.isDebug()) {
            Bukkit.getLogger().info("Can't calculate crit chance for " + this.getSkillName() + ", config is wrong: " + this.noDrownChance);
        }
        return 0;
    }
}
