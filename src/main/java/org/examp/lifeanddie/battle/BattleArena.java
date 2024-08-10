package org.examp.lifeanddie.battle;

import org.bukkit.Location;

public class BattleArena {
    private String name;
    private Location spawnPoint1;
    private Location spawnPoint2;
    private BattleArena currentArena;


    public BattleArena(String name, Location spawnPoint1, Location spawnPoint2) {
        if (name == null || spawnPoint1 == null || spawnPoint2 == null) {
            throw new IllegalArgumentException("Arena parameters cannot be null");
        }
        this.name = name;
        this.spawnPoint1 = spawnPoint1;
        this.spawnPoint2 = spawnPoint2;
    }

    // Геттеры
    public String getName() {
        return name;
    }

    public Location getSpawnPoint1() {
        return spawnPoint1;
    }

    public Location getSpawnPoint2() {
        return spawnPoint2;
    }
}
