package org.examp.lifeanddie.battle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ArenaManager {
    private Map<String, BattleArena> arenas = new HashMap<>();
    private JavaPlugin plugin;
    private File arenaFile;
    private FileConfiguration arenaConfig;

    public ArenaManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.arenaFile = new File(plugin.getDataFolder(), "arenas.yml");
        this.arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
        loadArenas();
    }

    public void addArena(String name, Location spawnPoint1, Location spawnPoint2) {
        arenas.put(name, new BattleArena(name, spawnPoint1, spawnPoint2));
        saveArena(name);
    }

    public BattleArena getArena(String name) {
        return arenas.get(name);
    }

    public BattleArena getRandomArena() {
        if (arenas.isEmpty()) return null;
        Random random = new Random();
        return (BattleArena) arenas.values().toArray()[random.nextInt(arenas.size())];
    }

    private void saveArena(String name) {
        BattleArena arena = arenas.get(name);
        String path = "arenas." + name + ".";
        arenaConfig.set(path + "world", arena.getSpawnPoint1().getWorld().getName());
        arenaConfig.set(path + "spawn1", locationToString(arena.getSpawnPoint1()));
        arenaConfig.set(path + "spawn2", locationToString(arena.getSpawnPoint2()));
        saveArenaFile();
    }

    private void loadArenas() {
        if (arenaConfig.contains("arenas")) {
            for (String arenaName : arenaConfig.getConfigurationSection("arenas").getKeys(false)) {
                String path = "arenas." + arenaName + ".";
                String worldName = arenaConfig.getString(path + "world");
                Location spawn1 = stringToLocation(arenaConfig.getString(path + "spawn1"), worldName);
                Location spawn2 = stringToLocation(arenaConfig.getString(path + "spawn2"), worldName);
                addArena(arenaName, spawn1, spawn2);
            }
        }
    }

    private void saveArenaFile() {
        try {
            arenaConfig.save(arenaFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Не удалось сохранить файл арен: " + e.getMessage());
        }
    }

    private String locationToString(Location location) {
        return location.getX() + "," + location.getY() + "," + location.getZ() + "," +
                location.getYaw() + "," + location.getPitch();
    }

    private Location stringToLocation(String str, String worldName) {
        String[] parts = str.split(",");
        return new Location(
                Bukkit.getWorld(worldName),
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Float.parseFloat(parts[3]),
                Float.parseFloat(parts[4])
        );
    }
}