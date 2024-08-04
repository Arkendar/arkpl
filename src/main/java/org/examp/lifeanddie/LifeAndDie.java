package org.examp.lifeanddie;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.examp.lifeanddie.battle.*;
import org.examp.lifeanddie.commands.*;
import org.examp.lifeanddie.commands.battlecommands.CreateArenaCommand;
import org.examp.lifeanddie.commands.battlecommands.DuelAcceptCommand;
import org.examp.lifeanddie.commands.battlecommands.DuelDeclineCommand;
import org.examp.lifeanddie.commands.battlecommands.SendDuelCommand;
import org.examp.lifeanddie.gui.TagGUI;
import org.examp.lifeanddie.listeners.PlayerJoinListener;
import org.examp.lifeanddie.listeners.TagGUIListener;
import org.examp.lifeanddie.prefix.PrefixManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class LifeAndDie extends JavaPlugin implements Listener {

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
    private PrefixManager prefixManager;
    private TagGUI tagGUI;





    @Override
    public void onEnable() {
        this.playerData = new PlayerData();
        this.playerClassManager = new PlayerClassManager(this);
        this.skillInventoryManager = new SkillInventoryManager(playerClassManager);


        this.battleConfig = new BattleConfig(this);

        this.arenaManager = new ArenaManager(this);

        this.sendDuelCommand = new SendDuelCommand(duelManager);

        this.battleStatistics = new BattleStatistics(this);
        this.battleStatistics.loadStats();

        this.battleManager = new BattleManager(this, battleConfig, battleStatistics, arenaManager);
        this.duelManager = new DuelManager(this, battleManager, battleStatistics, arenaManager);

        this.activeDuels = new ArrayList<>();

        this.prefixManager = new PrefixManager(this, battleStatistics);
        this.tagGUI = new TagGUI(prefixManager);



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
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerClassManager, prefixManager), this);
        getServer().getPluginManager().registerEvents(new TagGUIListener(prefixManager), this);
        getServer().getPluginManager().registerEvents(new AbilityUseListener(playerData, this), this);
        getServer().getPluginManager().registerEvents(playerClassManager, this);
        getServer().getPluginManager().registerEvents(skillInventoryManager, this);
        getServer().getPluginManager().registerEvents(new BattleListener(this, battleManager, duelManager), this);
        getServer().getPluginManager().registerEvents(this, this);


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
        //top
        this.getCommand("top").setExecutor(new TopCommand(battleStatistics));
        //tag
        getCommand("tag").setExecutor(new TagCommand(tagGUI));




    }
    @Override
    public void onDisable() {
        this.battleStatistics.saveStats(); // Сохранение статистики при выключении
        this.prefixManager.savePlayerTags();


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
        config.addDefault("duel.countdown_time", 5);
        config.addDefault("duel.max_duration", 300);

        // Сохраняем значения по умолчанию
        config.options().copyDefaults(true);
        saveConfig();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String tag = prefixManager.getPlayerTag(player);
        String message = event.getMessage();

        // Создаем новое отображаемое имя с тегом
        String newDisplayName = tag + "• " + player.getName();

        // Определяем цвет сообщения в зависимости от тега
        String messageColor = getMessageColorByTag(tag);

        // Создаем новый формат с цветным сообщением
        String newFormat = newDisplayName + " ❱ " + messageColor + "%2$s";
        event.setFormat(newFormat);
    }

    private String getMessageColorByTag(String tag) {

        if (tag.contains("[LEADER]")) {return ChatColor.GOLD + "";
        } else if (tag.contains("[VIP]")) {return ChatColor.GREEN + "";
        } else if (tag.contains("[ADMIN]")) {return ChatColor.RED + "";
        } else if (tag.contains("[НОВИЧОК]")) {return ChatColor.GRAY + "";
        }
        else {return ChatColor.WHITE + "";}
    }
}
