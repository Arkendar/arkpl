package org.examp.lifeanddie.battle;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BattleManager {

    private List<AbstractBattle> activeBattles;
    private BattleConfig config;
    private List<BattleArena> availableArenas;
    private final JavaPlugin plugin;
    private BattleStatistics statistics;
    private ArenaManager arenaManager;


    public BattleManager(JavaPlugin plugin, BattleConfig config, BattleStatistics statistics, ArenaManager arenaManager) {

        this.activeBattles = new ArrayList<>();
        this.config = config;
        this.availableArenas = new ArrayList<>();
        this.plugin = plugin;
        this.statistics = statistics;
        this.arenaManager = arenaManager;
    }

    public boolean createDuel(Player player1, Player player2) {
        plugin.getLogger().info("Создание дуэли в BattleManager");
        BattleArena arena = getAvailableArena();
        if (arena == null) {
            plugin.getLogger().warning("Нет доступных арен для дуэли");
            return false;
        }

        List<Player> participants = new ArrayList<>();
        participants.add(player1);
        participants.add(player2);

        Duel duel = new Duel(participants, arena, config, plugin, statistics);
        activeBattles.add(duel);
        duel.start();
        return true;
    }

    public void endBattle(AbstractBattle battle) {
        if (battle.isFinished()) {
            battle.end();
        }
        activeBattles.remove(battle);
    }

    public Optional<AbstractBattle> getBattleForPlayer(Player player) {
        return activeBattles.stream()
                .filter(battle -> battle.getParticipants().contains(player))
                .findFirst();
    }

    public boolean isPlayerInBattle(Player player) {
        return getBattleForPlayer(player).isPresent();
    }

    public BattleArena getAvailableArena() {
        plugin.getLogger().info("getAvailableArena");
        return arenaManager.getRandomArena();
    }

    public List<AbstractBattle> getActiveBattles() {
        return new ArrayList<>(activeBattles);
    }

    public BattleConfig getConfig() {
        return config;
    }

    public void setConfig(BattleConfig config) {
        this.config = config;
    }
}