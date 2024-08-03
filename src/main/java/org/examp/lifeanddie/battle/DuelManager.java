package org.examp.lifeanddie.battle;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class DuelManager {
    private JavaPlugin plugin;
    private BattleManager battleManager;
    private BattleStatistics statistics;
    private List<Duel> activeDuels;
    private ArenaManager arenaManager;

    public DuelManager(JavaPlugin plugin, BattleManager battleManager, BattleStatistics statistics, ArenaManager arenaManager) {
        this.plugin = plugin;
        this.battleManager = battleManager;
        this.statistics = statistics;
        if (this.statistics == null) {
            plugin.getLogger().warning("BattleStatistics is null in DuelManager constructor");
            this.statistics = new BattleStatistics(plugin);
        }
        this.activeDuels = new ArrayList<>();
        this.arenaManager = arenaManager;
    }

    public boolean createDuel(Player player1, Player player2) {
        plugin.getLogger().info("Начало создания дуэли");
        if (battleManager.isPlayerInBattle(player1) || battleManager.isPlayerInBattle(player2)) {
            plugin.getLogger().warning("Один из игроков уже в бою");
            return false;
        }
        BattleArena arena = arenaManager.getRandomArena();
        if (arena == null) {
            plugin.getLogger().warning("Нет доступных арен для дуэли");
            return false;
        }
        boolean success = battleManager.createDuel(player1, player2);
        if (success) {
            plugin.getLogger().info("Дуэль создана успешно");
        } else {
            plugin.getLogger().warning("Не удалось создать дуэль");
        }
        return success;
    }

    public void endDuel(Duel duel) {
        plugin.getLogger().info("Ending duel");
        battleManager.endBattle(duel);
        activeDuels.remove(duel);
        plugin.getLogger().info("Duel ended successfully");
    }

    public void cancelDuel(Player quitter) {
        Optional<Duel> duelOpt = getDuelForPlayer(quitter);
        duelOpt.ifPresent(duel -> {
            Player winner = duel.getParticipants().get(0).equals(quitter) ? duel.getParticipants().get(1) : duel.getParticipants().get(0);
            duel.cancelDuel(quitter, winner);
            endDuel(duel);
        });
    }

    public Optional<Duel> getDuelForPlayer(Player player) {
        return activeDuels.stream()
                .filter(duel -> duel.getParticipants().contains(player))
                .findFirst();
    }

    public void checkTimeLimit() {
        activeDuels.forEach(Duel::checkTimeLimit);
    }

    public List<Duel> getActiveDuels() {
        return new ArrayList<>(activeDuels);
    }
}