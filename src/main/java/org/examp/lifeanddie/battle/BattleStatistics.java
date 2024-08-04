package org.examp.lifeanddie.battle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BattleStatistics {
    private Map<UUID, PlayerStats> playerStats;
    private File statsFile;
    private FileConfiguration statsConfig;

    public BattleStatistics(JavaPlugin plugin) {
        this.playerStats = new HashMap<>();
        this.statsFile = new File(plugin.getDataFolder(), "battlestats.yml");
        this.statsConfig = YamlConfiguration.loadConfiguration(statsFile);
    }

    public void recordWin(Player player) {
        getOrCreateStats(player).wins++;
    }

    public void recordLoss(Player player) {
        getOrCreateStats(player).losses++;
    }

    public void recordKill(Player player) {
        getOrCreateStats(player).kills++;
    }

    public void recordDeath(Player player) {
        getOrCreateStats(player).deaths++;
    }

    public void recordDraw(Player player) {
        getOrCreateStats(player).draws++;
    }

    public void recordAddRate(Player player) {
        getOrCreateStats(player).rating++;
    }

    public void recordRedRate(Player player) {
        getOrCreateStats(player).rating--;
    }

    public PlayerStats getPlayerStats(Player player) {
        return getOrCreateStats(player);
    }

    private PlayerStats getOrCreateStats(Player player) {
        return playerStats.computeIfAbsent(player.getUniqueId(), k -> new PlayerStats());
    }

    public void saveStats() {
        for (Map.Entry<UUID, PlayerStats> entry : playerStats.entrySet()) {
            String path = entry.getKey().toString();
            PlayerStats stats = entry.getValue();
            statsConfig.set(path + ".wins", stats.wins);
            statsConfig.set(path + ".losses", stats.losses);
            statsConfig.set(path + ".draws", stats.draws);
            statsConfig.set(path + ".kills", stats.kills);
            statsConfig.set(path + ".deaths", stats.deaths);
            statsConfig.set(path + ".rating", stats.rating);

        }
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadStats() {
        for (String key : statsConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            PlayerStats stats = new PlayerStats();
            stats.wins = statsConfig.getInt(key + ".wins");
            stats.losses = statsConfig.getInt(key + ".losses");
            stats.draws = statsConfig.getInt(key + ".draws");
            stats.kills = statsConfig.getInt(key + ".kills");
            stats.deaths = statsConfig.getInt(key + ".deaths");
            stats.rating = statsConfig.getInt(key + ".rating");

            playerStats.put(uuid, stats);
        }
    }

    public List<Map.Entry<UUID, PlayerStats>> getTopPlayers(int limit) {
        return playerStats.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().rating, e1.getValue().rating))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public UUID getTopPlayer() {
        UUID topPlayer = playerStats.entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().getRating()))
                .map(Map.Entry::getKey)
                .orElse(null);
        System.out.println("Top player UUID: " + topPlayer);
        return topPlayer;
    }

    public boolean isInTop(Player player) {
        UUID uuidPlayer = player.getUniqueId();
        UUID topPlayer = getTopPlayer();
        return uuidPlayer != null && uuidPlayer.equals(topPlayer);
    }

    public class PlayerStats {
        int wins;
        int losses;
        int draws;
        int kills;
        int deaths;
        int rating;

        public int getWins() { return wins; }
        public int getLosses() { return losses; }
        public int getDraws() { return draws; }
        public int getKills() { return kills; }
        public int getDeaths() { return deaths; }
        public int getRating() { return rating; }

        public void updateRating(Player player, boolean isWin) {
            PlayerStats stats = getOrCreateStats(player);
            int ratingChange = isWin ? 25 : -20;
            stats.rating = Math.max(0, stats.rating + ratingChange);
        }

        public double getKDRatio() {
            return deaths == 0 ? kills : (double) kills / deaths;
        }

        public double getWinRate() {
            int totalGames = wins + losses + draws;
            return totalGames == 0 ? 0 : (double) wins / totalGames;
        }

        @Override
        public String toString() {
            return String.format("Wins: %d, Losses: %d, Draws: %d, Kills: %d, Deaths: %d, K/D: %.2f, Win Rate: %.2f%%",
                    wins, losses, draws, kills, deaths, getKDRatio(), getWinRate() * 100);
        }
    }
}