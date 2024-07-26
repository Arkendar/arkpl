package org.examp.lifeanddie;

import org.bukkit.plugin.java.JavaPlugin;

public final class LifeAndDie extends JavaPlugin {

    private PlayerClassManager playerClassManager;
    private SkillInventoryManager skillInventoryManager;
    private static LifeAndDie instance;

    @Override
    public void onEnable() {
        instance = this;
        this.playerClassManager = new PlayerClassManager(this);
        this.skillInventoryManager = new SkillInventoryManager(playerClassManager);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerClassManager), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new AbilityUseListener(), this);
        getServer().getPluginManager().registerEvents(playerClassManager, this);
        getServer().getPluginManager().registerEvents(skillInventoryManager, this); // Register skill inventory events

        if (getCommand("class") != null) {
            getLogger().info("Registering command /class");
            getCommand("class").setExecutor(new ChooseClassCommand(playerClassManager));
        } else {
            getLogger().severe("Command /class is not defined in plugin.yml");
        }
        if (getCommand("skill") != null) {
            getLogger().info("Registering command /skill");
            getCommand("skill").setExecutor(new ChooseSkillCommand(skillInventoryManager));
        } else {
            getLogger().severe("Command /skill is not defined in plugin.yml");
        }
    }


    public static LifeAndDie getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public PlayerClassManager getPlayerClassManager() {
        return playerClassManager;
    }

    public SkillInventoryManager getSkillInventoryManager() {
        return skillInventoryManager;
    }
}
