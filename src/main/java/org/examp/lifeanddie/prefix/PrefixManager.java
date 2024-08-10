package org.examp.lifeanddie.prefix;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.examp.lifeanddie.battle.BattleStatistics;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PrefixManager {
    private final JavaPlugin plugin;
    private final BattleStatistics battleStatistics;
    private final BattleStatistics.PlayerStats playerStats;
    private Map<String, String> tags;
    private Map<UUID, String> playerTags;
    private File tagsFile;
    private FileConfiguration tagsConfig;

    public PrefixManager(JavaPlugin plugin, BattleStatistics battleStatistics) {
        this.plugin = plugin;
        this.battleStatistics = battleStatistics;
        this.playerStats = this.battleStatistics.new PlayerStats();
        this.tags = new HashMap<>();
        this.playerTags = new HashMap<>();
        this.tagsFile = new File(plugin.getDataFolder(), "player_tags.yml");
        this.tagsConfig = YamlConfiguration.loadConfiguration(tagsFile);
        loadPlayerTags();

        //теги
        addTag("top", " &6&l▶ [LEADER] ◀&r ");
        addTag("vip", " &a[VIP]&r ");
        addTag("admin", " &c❖ [ADMIN] ❖&r ");


        addTag("10", " &7[Ученик]&r ");
        addTag("20", " &7[Подмастерье]&r ");
        addTag("50", " &8[Опытный]&r ");
        addTag("100", " &7&l[Ветеран]&r ");
        addTag("200", " &e[Герой]&r ");
        addTag("300", " &6&l[Чемпион]&r ");
        addTag("500", " &b&l[Легенда]&r ");
        addTag("700", " &d&l[Титан]&r ");
        addTag("1000", " &5&l[Бессмертный]&r ");
        addTag("1300", " &c&l[Король]&r ");
        addTag("1500", " &4&l❖ [Император] ❖&r ");

        addTag("default", " &7[НОВИЧОК] ");
    }

    public void savePlayerTags() {
        // Очищаем существующие теги
        tagsConfig.set("tags", null);

        for (Map.Entry<UUID, String> entry : playerTags.entrySet()) {
            tagsConfig.set("tags." + entry.getKey().toString(), entry.getValue());
        }
        try {
            tagsConfig.save(tagsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить теги игроков: " + e.getMessage());
        }
    }

    private void loadPlayerTags() {
        ConfigurationSection tagsSection = tagsConfig.getConfigurationSection("tags");
        if (tagsSection != null) {
            for (String key : tagsSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String tagKey = tagsSection.getString(key);
                    if (tagKey != null) {
                        playerTags.put(uuid, tagKey);
                    } else {
                        plugin.getLogger().warning("Null tag found for UUID: " + key);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in tags config: " + key);
                }
            }
        }
        plugin.getLogger().info("Loaded " + playerTags.size() + " player tags.");
    }

    public String getPlayerTagKey(UUID uuid) {
        return playerTags.get(uuid);
    }

    public void addTag(String key, String tag) {
        tags.put(key, ChatColor.translateAlternateColorCodes('&', tag));
    }

    public String getTag(String key) {
        return tags.get(key);
    }

    public void setPlayerTag(Player player, String tagKey) {
        if (hasAccessToTag(player, tagKey)) {
            String tag = tags.get(tagKey);
            if (tag != null) {
                playerTags.put(player.getUniqueId(), tagKey);
                updatePlayerName(player);
                savePlayerTags();
            } else {
                player.sendMessage(ChatColor.RED + "Ошибка: Тег не найден.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "У вас нет доступа к этому тегу.");
        }
    }

    public String getPlayerTag(Player player) {
        String tagKey = playerTags.get(player.getUniqueId());
        String tag = tagKey != null ? tags.get(tagKey) : tags.get("default");
        return tag != null ? tag : "";
    }

    public void updatePlayerName(Player player) {
        String tag = getPlayerTag(player);
        String coloredName = tag + player.getName();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.setPlayerListName(coloredName);
            player.setDisplayName(coloredName);
            player.setCustomName(coloredName);
            player.setCustomNameVisible(true);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.hidePlayer(plugin, player);
                onlinePlayer.showPlayer(plugin, player);
            }
        });
    }

    public boolean hasAccessToTag(Player player, String tagKey) {
        switch (tagKey) {
            case "default":
                return true;
            case "top":
                return battleStatistics.isInTop(player);
            case "10":
                return battleStatistics.getPlayerRating(player) >= 10;
            case "20":
                return battleStatistics.getPlayerRating(player) >= 20;
            case "50":
                return battleStatistics.getPlayerRating(player) >= 50;
            case "100":
                return battleStatistics.getPlayerRating(player) >= 100;
            case "200":
                return battleStatistics.getPlayerRating(player) >= 200;
            case "300":
                return battleStatistics.getPlayerRating(player) >= 300;
            case "500":
                return battleStatistics.getPlayerRating(player) >= 500;
            case "700":
                return battleStatistics.getPlayerRating(player) >= 700;
            case "1000":
                return battleStatistics.getPlayerRating(player) >= 1000;
            case "1300":
                return battleStatistics.getPlayerRating(player) >= 1300;
            case "1500":
                return battleStatistics.getPlayerRating(player) >= 1500;
            default:
                return player.hasPermission("lifeanddie.tag." + tagKey);
        }
    }

    public void getAvailableTagsAsync(Player player, Consumer<Map<String, String>> callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Map<String, String> availableTags = new HashMap<>();
            if (!player.hasPlayedBefore()) {
                availableTags.put("default", tags.get("default"));
            } else {
                for (Map.Entry<String, String> entry : tags.entrySet()) {
                    if (hasAccessToTag(player, entry.getKey())) {
                        availableTags.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            plugin.getServer().getScheduler().runTask(plugin, () -> callback.accept(availableTags));
        });
    }

    public void removePlayerTag(Player player) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            playerTags.remove(player.getUniqueId());
            updatePlayerName(player);
        });
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public void updateAllPlayerTags() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerName(player);
        }
    }
}