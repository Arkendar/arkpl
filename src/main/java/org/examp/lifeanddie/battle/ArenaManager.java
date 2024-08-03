package org.examp.lifeanddie.battle;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class ArenaManager {
    private Map<String, BattleArena> arenas = new HashMap<>();

    public void addArena(String name, Location spawnPoint1, Location spawnPoint2) {
        arenas.put(name, new BattleArena(name, spawnPoint1, spawnPoint2));
    }

    public BattleArena getArena(String name) {
        return arenas.get(name);
    }

    public BattleArena getRandomArena() {
        if (arenas.isEmpty()) return null;
        return (BattleArena) arenas.values().toArray()[0]; // Возвращаем первую арену (можно улучшить)
    }
}
