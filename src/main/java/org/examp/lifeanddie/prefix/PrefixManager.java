package org.examp.lifeanddie.prefix;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class PrefixManager {
    private final JavaPlugin plugin;
    private final BattleStatistics battleStatistics;
    private Map<String, String> tags;
    private Map<UUID, String> playerTags;
    private File tagsFile;
    private FileConfiguration tagsConfig;

    public PrefixManager(JavaPlugin plugin, BattleStatistics battleStatistics) {
        this.plugin = plugin;
        this.battleStatistics = battleStatistics;
        this.tags = new HashMap<>();
        this.playerTags = new HashMap<>();
        this.tagsFile = new File(plugin.getDataFolder(), "player_tags.yml");
        this.tagsConfig = YamlConfiguration.loadConfiguration(tagsFile);
        loadPlayerTags();

        // Добавляем стандартные теги
        addTag("top", " &6&l[LEADER]&r ");
        addTag("vip", " &a[VIP]&r ");
        addTag("admin", " &c[ADMIN]&r ");
        addTag("default", " &7[НОВИЧОК]&r ");
    }

    public void savePlayerTags() {
        for (Map.Entry<UUID, String> entry : playerTags.entrySet()) {
            tagsConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            tagsConfig.save(tagsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayerTags() {
        for (String key : tagsConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            String tagKey = tagsConfig.getString(key);
            playerTags.put(uuid, tagKey);
        }
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

        // Обновляем имя в табе
        player.setPlayerListName(coloredName);

        // Обновляем отображаемое имя
        player.setDisplayName(coloredName);

        // Обновляем имя над головой игрока
        player.setCustomName(coloredName);
        player.setCustomNameVisible(true);

        // Принудительно обновляем отображение для всех игроков
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hidePlayer(this.plugin, player);
            onlinePlayer.showPlayer(this.plugin, player);
        }
    }


    public boolean hasAccessToTag(Player player, String tagKey) {
        if (tagKey.equals("default")) {
            return true;
        }
        if (tagKey.equals("top")) {
            return battleStatistics.isInTop(player);
        }
        return player.hasPermission("lifeanddie.tag." + tagKey);
    }

    public Map<String, String> getAvailableTags(Player player) {
        Map<String, String> availableTags = new HashMap<>();
        if (!player.hasPlayedBefore()) {
            availableTags.put("default", tags.get("default"));
            return availableTags;
        }
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            if (hasAccessToTag(player, entry.getKey())) {
                availableTags.put(entry.getKey(), entry.getValue());
            }
        }
        return availableTags;
    }

    public void removePlayerTag(Player player) {
        playerTags.remove(player.getUniqueId());
        updatePlayerName(player);
    }
}