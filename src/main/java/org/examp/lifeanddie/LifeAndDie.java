package org.examp.lifeanddie;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.examp.lifeanddie.ability.Fortress;
import org.examp.lifeanddie.battle.*;
import org.examp.lifeanddie.commands.*;
import org.examp.lifeanddie.commands.battlecommands.CreateArenaCommand;
import org.examp.lifeanddie.commands.battlecommands.DuelAcceptCommand;
import org.examp.lifeanddie.commands.battlecommands.DuelDeclineCommand;
import org.examp.lifeanddie.commands.battlecommands.SendDuelCommand;
import org.examp.lifeanddie.commands.fractioncommands.JoinFractionCommand;
import org.examp.lifeanddie.commands.fractioncommands.LeaveFractionCommand;
import org.examp.lifeanddie.commands.statscommands.StatsCommand;
import org.examp.lifeanddie.commands.statscommands.TopCommand;
import org.examp.lifeanddie.fraction.Fraction;
import org.examp.lifeanddie.fraction.FractionManager;
import org.examp.lifeanddie.fraction.fractions.GhostsFraction;
import org.examp.lifeanddie.fraction.fractions.ShadowBrotherhoodFraction;
import org.examp.lifeanddie.fraction.fractions.UndeadFraction;
import org.examp.lifeanddie.gui.ClassSelectionGUI;
import org.examp.lifeanddie.gui.TagGUI;
import org.examp.lifeanddie.listeners.*;
import org.examp.lifeanddie.listeners.fraction.FractionDamageListener;
import org.examp.lifeanddie.listeners.fraction.FractionSelectionListener;
import org.examp.lifeanddie.player.PlayerClassManager;
import org.examp.lifeanddie.player.PlayerData;
import org.examp.lifeanddie.prefix.PrefixManager;
import org.examp.lifeanddie.battle.BattleListener;


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
    private PrefixManager prefixManager;
    private TagGUI tagGUI;
    private CooldownDisplayManager cooldownDisplayManager;
    private Fortress fortress;
    private FractionManager fractionManager;

    @Override
    public void onEnable() {
        this.playerData = new PlayerData();

        this.cooldownDisplayManager = new CooldownDisplayManager(this, playerData);

        this.playerClassManager = new PlayerClassManager(this);
        this.skillInventoryManager = new SkillInventoryManager(playerClassManager);

        this.battleConfig = new BattleConfig(this);

        this.arenaManager = new ArenaManager(this);


        this.battleStatistics = new BattleStatistics(this);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            this.battleStatistics.loadStats();
        });
        this.battleManager = new BattleManager(this, battleConfig, battleStatistics, arenaManager);
        this.duelManager = new DuelManager(this, battleManager, battleStatistics, arenaManager);
        this.sendDuelCommand = new SendDuelCommand(duelManager);

        this.prefixManager = new PrefixManager(this, battleStatistics);
        this.tagGUI = new TagGUI(prefixManager);

        this.fortress = new Fortress(this, playerData);

        this.fractionManager = new FractionManager(this);
        fractionManager.loadFractions();

        new BukkitRunnable() {
            @Override
            public void run() {
                fractionManager.updateFractionAbilities();
            }
        }.runTaskTimer(this, 20L, 20L); // Запуск каждую секунду (20 тиков)

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            prefixManager.updateAllPlayerTags();
        }, 20L * 60, 20L * 60);


        createDefaultConfig();
        registerListeners();
        registerCommands();

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().runTaskAsynchronously(LifeAndDie.this, () -> {
                    for (Duel duel : duelManager.getActiveDuels()) {
                        duel.update();
                    }
                });
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, fractionManager, prefixManager), this);
        getServer().getPluginManager().registerEvents(new TagGUIListener(prefixManager), this);
        getServer().getPluginManager().registerEvents(new AbilityUseListener(playerData, this, cooldownDisplayManager), this);
        getServer().getPluginManager().registerEvents(new CooldownItemListener(cooldownDisplayManager), this);
        getServer().getPluginManager().registerEvents(playerClassManager, this);
        getServer().getPluginManager().registerEvents(skillInventoryManager, this);
        getServer().getPluginManager().registerEvents(new BattleListener(this, battleManager, duelManager), this);
        getServer().getPluginManager().registerEvents(new ClassInventoryClickListener(playerClassManager), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new FractionDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new FractionSelectionListener(fractionManager), this);
        getServer().getPluginManager().registerEvents(new GhostsFraction(fractionManager), this);
        getServer().getPluginManager().registerEvents(new ShadowBrotherhoodFraction(fractionManager), this);
        getServer().getPluginManager().registerEvents(new UndeadFraction(fractionManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(fractionManager), this);
        getServer().getPluginManager().registerEvents(new WorldChangeListener(this), this);

    }

    private void registerCommands() {
        if (getCommand("class") != null) {
            getLogger().info("Registering command /class");
            ClassSelectionGUI classSelectionGUI = new ClassSelectionGUI();
            getCommand("class").setExecutor(new ChooseClassCommand(classSelectionGUI));
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

        //Фракции
        if (getCommand("joinfraction") != null) {
            getLogger().info("Registering command /joinfraction");
            getCommand("joinfraction").setExecutor(new JoinFractionCommand());

        } else {
            getLogger().severe("Command /joinfraction is not defined in plugin.yml");
        }

        if (getCommand("leavefraction") != null) {
            getLogger().info("Registering command /leavefraction");
            getCommand("leavefraction").setExecutor(new LeaveFractionCommand(fractionManager));
        } else {
            getLogger().severe("Command /leavefraction is not defined in plugin.yml");
        }


        //stats
        this.getCommand("stats").setExecutor(new StatsCommand(battleStatistics));
        //top
        this.getCommand("top").setExecutor(new TopCommand(this,battleStatistics));
        //tag
        getCommand("tag").setExecutor(new TagCommand(tagGUI));
        //reset
        this.getCommand("reset").setExecutor(new ClearInventoryCommand());


    }
    @Override
    public void onDisable() {
        if (this.battleStatistics != null) {
            this.battleStatistics.saveStats();
        }
        if (this.prefixManager != null) {
            this.prefixManager.savePlayerTags();
        }
        if (fortress != null) {
            fortress.clearAllFortresses();
        }
        if(fractionManager != null){
            fractionManager.saveFractions();
        }


        // Plugin shutdown logic
    }

    public PlayerData getPlayerData() {
        return playerData;
    }
    public FractionManager getFractionManager() {return fractionManager;}

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

    public void resetPlayerCooldowns(Player player) {
        if(player!=null){
        playerData.resetCooldowns(player);
        cooldownDisplayManager.resetCooldowns(player);}
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
            Player player = (Player) event.getEntity();
            player.setFoodLevel(20);
            player.setSaturation(20f);
        }
    }
}
