package org.examp.lifeanddie.fraction;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.fraction.fractions.FortressGuardiansFraction;
import org.examp.lifeanddie.fraction.fractions.GhostsFraction;
import org.examp.lifeanddie.fraction.fractions.UndeadFraction;
import org.examp.lifeanddie.fraction.fractions.ShadowBrotherhoodFraction;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FractionManager {
    private final Map<UUID, Fraction> playerFractions;
    private final LifeAndDie plugin;
    private final File fractionFile;
    private final Map<String, Fraction> fractions;

    public FractionManager(LifeAndDie plugin) {
        this.plugin = plugin;
        this.playerFractions = new HashMap<>();
        this.fractionFile = new File(plugin.getDataFolder(), "fractions.yml");
        this.fractions = new HashMap<>();
        initializeFractions();
    }

    private void initializeFractions() {
        fractions.put("Стражи крепости", new FortressGuardiansFraction());
        fractions.put("Призраки", new GhostsFraction(plugin.getFractionManager()));
        fractions.put("Нежить", new UndeadFraction(plugin.getFractionManager()));
        fractions.put("Братство Теней", new ShadowBrotherhoodFraction(plugin.getFractionManager()));
    }

    public void setPlayerFraction(Player player, Fraction newFraction) {
        UUID playerId = player.getUniqueId();

        if (playerFractions.containsKey(playerId)) {
            Fraction oldFraction = playerFractions.get(playerId);
            oldFraction.removeEffects(player);
        }

        playerFractions.put(playerId, newFraction);
        newFraction.applyEffects(player);
    }

    public Fraction getPlayerFraction(Player player) {
        return playerFractions.get(player.getUniqueId());
    }

    public void removePlayerFraction(Player player) {
        UUID playerId = player.getUniqueId();
        if (playerFractions.containsKey(playerId)) {
            playerFractions.get(playerId).removeEffects(player);
            playerFractions.remove(playerId);
        }
    }

    public void saveFractions() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(fractionFile);
        for (Map.Entry<UUID, Fraction> entry : playerFractions.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue().getName());
        }
        try {
            config.save(fractionFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить данные о фракциях: " + e.getMessage());
        }
    }

    public void loadFractions() {
        if (!fractionFile.exists()) {
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(fractionFile);
        for (String uuidString : config.getKeys(false)) {
            UUID playerId = UUID.fromString(uuidString);
            String fractionName = config.getString(uuidString);
            Fraction fraction = fractions.get(fractionName);
            if (fraction != null) {
                playerFractions.put(playerId, fraction);
            }
        }
    }

    public Map<String, Fraction> getAllFractions() {
        return new HashMap<>(fractions);
    }

    public void applyFractionEffects(Player player) {
        Fraction fraction = getPlayerFraction(player);
        if (fraction != null) {
            fraction.applyEffects(player);
        }
    }

    // Метод для периодической проверки и активации способностей фракций
    public void updateFractionAbilities() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Fraction fraction = getPlayerFraction(player);
            if (fraction != null) {
                if (fraction instanceof GhostsFraction) {
                    ((GhostsFraction) fraction).checkInvisibility(player);
                } else if (fraction instanceof UndeadFraction) {
                    ((UndeadFraction) fraction).highlightLowHealthPlayers(player);
                } else if (fraction instanceof ShadowBrotherhoodFraction) {
                    ((ShadowBrotherhoodFraction) fraction).activateDangerSense(player);
                    ((ShadowBrotherhoodFraction) fraction).checkInvisibility(player);
                }
            }
        }
    }
}