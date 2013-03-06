package nl.lolmen.Skills;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import nl.lolmen.Skills.skills.CustomSkill;
import nl.lolmen.Skillz.Skillz;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomSkillManager {

    private Skillz plugin;
    private File customFile = new File("plugins" + File.separator + "Skillz" + File.separator + "custom.yml");
    private HashMap<String, CustomSkill> customs = new HashMap<String, CustomSkill>();

    public CustomSkillManager(Skillz plugin) {
        this.plugin = plugin;
    }

    public void loadCustomSkills() {
        if (!customFile.exists()) {
            this.createCustomSkills();
        }
        YamlConfiguration c = YamlConfiguration.loadConfiguration(customFile);
        try {
            for (String key : c.getConfigurationSection("").getKeys(false)) {
                this.plugin.debug("Starting to load " + key);
                CustomSkill s = new CustomSkill(this.plugin);
                s.setSkillName(c.getString(key + ".name"));
                s.setEnabled(c.getBoolean(key + ".enabled", true));
                s.setMultiplier(c.getInt(key + ".XP-gain-multiplier", 1));
                s.setUses(c.getString(key + ".uses"));
                for (String use : s.getUsesArray()) {
                    if (c.contains(key + "." + use + ".block_level")) {
                        for (String block : c.getConfigurationSection(key + "." + use + ".block_level").getKeys(false)) {
                            s.addBlockLevels(Integer.parseInt(block), c.getInt(key + "." + use + ".block_level." + block));
                        }
                    }
                    if (c.contains(key + "." + use + ".block_xp")) {
                        for (String block : c.getConfigurationSection(key + "." + use + ".block_xp").getKeys(false)) {
                            s.addBlock(Integer.parseInt(block), c.getInt(key + "." + use + ".block_xp." + block));
                        }
                    }
                }
                s.setItemOnLevelup(c.getString(key + ".reward.item", SkillsSettings.getItemOnLevelup()));
                s.setMoneyOnLevelup(c.getInt(key + ".reward.money", SkillsSettings.getMoneyOnLevelup()));
                if (c.contains(key + ".reward.every_many_levels")) {
                    for (String lvl : c.getConfigurationSection(key + ".reward.every_many_levels").getKeys(false)) {
                        s.add_to_every_many_levels(Integer.parseInt(lvl), c.getString(key + ".reward.every_many_levels." + lvl));
                    }
                }
                if (c.contains(key + ".reward.fixed_levels")) {
                    for (String lvl : c.getConfigurationSection(key + ".reward.fixed_levels").getKeys(false)) {
                        s.add_to_fixed_levels(Integer.parseInt(lvl), c.getString(key + ".reward.fixed_levels." + lvl));
                    }
                }
                this.customs.put(key, s);
                this.plugin.debug("Finished loading " + key + " without errors");

            }
            this.plugin.debug("Loaded " + this.customs.size() + " custom skills");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CustomSkill getSkill(String name) {
        if (this.customs.containsKey(name)) {
            return this.customs.get(name);
        }
        return null;
    }

    public HashSet<CustomSkill> getSkills() {
        HashSet<CustomSkill> set = new HashSet<CustomSkill>();
        for (String item : this.customs.keySet()) {
            set.add(this.customs.get(item));
        }
        return set;
    }

    public HashSet<CustomSkill> getSkillsUsing(String eventType) {
        HashSet<CustomSkill> set = new HashSet<CustomSkill>();
        for (CustomSkill skill : this.getSkills()) {
            if (skill.hasUse(eventType)) {
                set.add(skill);
            }
        }
        return set;
    }

    private void createCustomSkills() {
        Bukkit.getLogger().info("[Skillz] Trying to create default custom skills...");
        try {
            new File("plugins/Skillz/").mkdir();
            File efile = new File("plugins" + File.separator + "Skillz" + File.separator + "custom.yml");
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("custom.yml");
            OutputStream out = new BufferedOutputStream(new FileOutputStream(efile));
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            out.flush();
            out.close();
            in.close();
            Bukkit.getLogger().info("[Skillz] Custom skills created succesfully!");
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().info("[Skillz] Error creating custom skills file! Not using any!");
        }
    }
}
