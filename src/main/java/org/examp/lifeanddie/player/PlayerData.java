package org.examp.lifeanddie.player;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    //class player

    private final Map<UUID, PlayerClass> playerClasses = new HashMap<>();

    public PlayerClass getPlayerClass(Player player) {
        return playerClasses.get(player.getUniqueId());
    }
    public void setPlayerClass(UUID playerUUID, PlayerClass playerClass) {
        playerClasses.put(playerUUID, playerClass);
    }
    public boolean hasChosenClass(Player player) {
        return playerClasses.containsKey(player.getUniqueId());
    }

    //cooldowns player

    private final Map<String, Long> cooldowns = new HashMap<>();

    public boolean isInCooldown(String ability, UUID playerId) {
        long currentTime = System.currentTimeMillis();
        if (cooldowns.containsKey(ability + playerId.toString())) {
            long lastUseTime = cooldowns.get(ability + playerId);
            long cooldownTimeLeft = getCooldownTime(ability) - (currentTime - lastUseTime);
            return cooldownTimeLeft > 0;
        }
        return false;
    }

    public void setCooldown(String ability, UUID playerId) {
        cooldowns.put(ability + playerId.toString(), System.currentTimeMillis());
    }

    public long getCooldownTimeLeft(String ability, UUID playerId) {
        long currentTime = System.currentTimeMillis();
        long lastUseTime = cooldowns.getOrDefault(ability + playerId.toString(), 0L);
        long cooldownTime = getCooldownTime(ability);
        long timeLeft = cooldownTime - (currentTime - lastUseTime);
        return Math.max(0, timeLeft);
    }

    public long getCooldownTime(String ability) {
        switch (ability) {
            case "DASH":
                return 7 * 1000;
            case "FORTRESS":
                return 25 * 1000;
            case "FIRE_STRIKE":
                return 15 * 1000;
            case "MAGIC_STAFF":
                return 1 * 1000;
            case "FLY":
                return 15 * 1000;
            case "CLOUD":
                return 30 * 1000;
            case "ERUPTION":
                return 25 * 1000;
            case "WAVE_ARROWS":
                return 20 * 1000;
            case "ASTRAL_SPHERE":
                return 30 * 1000;
            case "EARTH_JAWS":
                return 20 * 1000;
            case "LIGHTNING_STORM":
                return 30 * 1000;
            case "ICE_WAVE":
                return 30 * 1000;
            case "CHAOS_BEARER":
                return 10 * 1000;
            case "BURIAL":
                return 15 * 1000;
            case "LIGHT_HEAVEN":
                return 20 * 1000;
            case "ICE":
                return 20 * 1000;
            case "DASH_TELEPORT":
                return 15 * 1000;
            case "SKYFALL":
                return 15 * 1000;
            case "WRATH_STORM":
                return 15 * 1000;
            default:
                return 0;
        }
    }

    public void resetCooldowns(Player player) {
        if (player != null) {
            UUID playerUUID = player.getUniqueId();
            cooldowns.entrySet().removeIf(entry -> entry.getKey().endsWith(playerUUID.toString()));
        } else {
            return;
        }

    }

}
