package org.examp.lifeanddie;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.examp.lifeanddie.battle.*;
import org.examp.lifeanddie.commands.ChooseClassCommand;
import org.examp.lifeanddie.commands.ChooseSkillCommand;
import org.examp.lifeanddie.commands.StatsCommand;
import org.examp.lifeanddie.commands.battlecommands.CreateArenaCommand;
import org.examp.lifeanddie.commands.battlecommands.DuelAcceptCommand;
import org.examp.lifeanddie.commands.battlecommands.DuelDeclineCommand;
import org.examp.lifeanddie.commands.battlecommands.SendDuelCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class LifeAndDie extends JavaPlugin {

    private PlayerClassManager playerClassManager;
    private SkillInventoryManager skillInventoryManager;
    private PlayerData playerData;
    private SendDuelCommand sendDuelCommand;
    private BattleManager battleManager;
    private DuelManager duelManager;
    private BattleConfig battleConfig;
    private ArenaManager arenaManager;
    private BattleStatistics battleStatistics;
    private List<Duel> activeDuels;




    @Override
    public void onEnable() {
        this.playerData = new PlayerData();
        this.playerClassManager = new PlayerClassManager(this);
        this.skillInventoryManager = new SkillInventoryManager(playerClassManager);


//        this.battleStatistics = new BattleStatistics();
        this.battleConfig = new BattleConfig(this);

        this.arenaManager = new ArenaManager();

        this.sendDuelCommand = new SendDuelCommand(duelManager);

        this.battleStatistics = new BattleStatistics(this);
        this.battleStatistics.loadStats();

        this.battleManager = new BattleManager(this, battleConfig, battleStatistics, arenaManager);
        this.duelManager = new DuelManager(this, battleManager, battleStatistics, arenaManager);

        this.activeDuels = new ArrayList<>();

        createDefaultConfig();
        registerListeners();
        registerCommands();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Duel duel : duelManager.getActiveDuels()) {
                    duel.update();
                }
            }
        }.runTaskTimer(this, 20L, 20L); // Проверка каждую секунду
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerClassManager), this);
        getServer().getPluginManager().registerEvents(new AbilityUseListener(playerData, this), this);
        getServer().getPluginManager().registerEvents(playerClassManager, this);
        getServer().getPluginManager().registerEvents(skillInventoryManager, this);
        getServer().getPluginManager().registerEvents(new BattleListener(this, battleManager, duelManager), this);
    }

    private void registerCommands() {
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

        // Регистрация команд для дуэлей
        if (getCommand("duel") != null) {
            getLogger().info("Registering command /duel");
            getCommand("duel").setExecutor(sendDuelCommand);
        } else {
            getLogger().severe("Command /duel is not defined in plugin.yml");
        }
        if (getCommand("duelaccept") != null) {
            getLogger().info("Registering command /duelaccept");
            this.getCommand("duelaccept").setExecutor(new DuelAcceptCommand(duelManager, sendDuelCommand));
        } else {
            getLogger().severe("Command /duelaccept is not defined in plugin.yml");
        }
        if (getCommand("dueldecline") != null) {
            getLogger().info("Registering command /dueldecline");
            getCommand("dueldecline").setExecutor(new DuelDeclineCommand(sendDuelCommand)); // Передача sendDuelCommand
        } else {
            getLogger().severe("Command /dueldecline is not defined in plugin.yml");
        }

        // Регистрация команды для создания арены
        if (getCommand("createarena") != null) {
            getLogger().info("Registering command /createarena");
            getCommand("createarena").setExecutor(new CreateArenaCommand(arenaManager));
        } else {
            getLogger().severe("Command /createarena is not defined in plugin.yml");
        }

        //stats
        this.getCommand("stats").setExecutor(new StatsCommand(battleStatistics));

    }
    @Override
    public void onDisable() {
        this.battleStatistics.saveStats(); // Сохранение статистики при выключении

        // Plugin shutdown logic
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    private void createDefaultConfig() {
        // Получаем файл config.yml
        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            // Если файл не существует, сохраняем ресурс по умолчанию
            saveDefaultConfig();
        }

        // Загружаем конфигурацию
        FileConfiguration config = getConfig();

        // Устанавливаем значения по умолчанию, если они отсутствуют
        config.addDefault("duel.countdown_time", 10);
        config.addDefault("duel.max_duration", 300);

        // Сохраняем значения по умолчанию
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void checkDuelsState() {
        this.getLogger().info("Checking duels state:");
        for (AbstractBattle battle : battleManager.getActiveBattles()) {
            if (battle instanceof Duel) {
                Duel duel = (Duel) battle;
                this.getLogger().info("Duel: " + duel.getParticipants() + ", State: " + duel.getState());
            }
        }
    }
}
