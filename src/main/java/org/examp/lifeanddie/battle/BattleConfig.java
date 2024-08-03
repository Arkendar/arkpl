package org.examp.lifeanddie.battle;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BattleConfig {
    private int duelCountdownTime;
    private int duelMaxDuration;
    private JavaPlugin plugin;

    public BattleConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        this.duelCountdownTime = config.getInt("duel.countdown_time", 10);
        this.duelMaxDuration = config.getInt("duel.max_duration", 300);
    }

    public int getDuelCountdownTime() {
        return duelCountdownTime;
    }

    public int getDuelMaxDuration() {
        return duelMaxDuration;
    }
}
