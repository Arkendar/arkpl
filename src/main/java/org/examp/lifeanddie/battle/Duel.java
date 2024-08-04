package org.examp.lifeanddie.battle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Duel extends AbstractBattle {
    private final JavaPlugin plugin;
    private BattleStatistics statistics;
    private boolean isEnded = false;
    private boolean wasDamaged = false;
    World targetWorld = Bukkit.getWorld("world"); // Хаб

    public Duel(List<Player> participants, BattleArena arena, BattleConfig config, JavaPlugin plugin, BattleStatistics statistics) {
        super(participants, arena, config);
        this.plugin = plugin;
        this.statistics = statistics;
        if (this.statistics == null) {
            plugin.getLogger().warning("BattleStatistics is null in Duel constructor");
            this.statistics = new BattleStatistics(plugin);
        }
    }

    @Override
    public void start() {
        state = BattleState.STARTING;
        teleportPlayers();
        startCountdown();
    }

    @Override
    public void end() {
        if (isEnded) {
            plugin.getLogger().info("Duel already ended, skipping...");
            return;
        }
        isEnded = true;

        plugin.getLogger().info("Duel end method called");
        state = BattleState.ENDING;
        Player winner = determineWinner();
        if (winner != null) {
            announceWinner(winner);
            //giveReward(winner);
            updateStatistics(winner);
            plugin.getLogger().info("Duel ended. Winner: " + winner.getName());
        } else {
            announceDraw();
            updateStatisticsForDraw();
            plugin.getLogger().info("Duel ended in a draw");
        }
        resetPlayersState();
        state = BattleState.FINISHED;
    }

    @Override
    public boolean isFinished() {
        return state != BattleState.FINISHED && !isEnded;
    }

    @Override
    protected void teleportPlayers() {
        participants.get(0).teleport(arena.getSpawnPoint1());
        participants.get(1).teleport(arena.getSpawnPoint2());
    }

    @Override
    protected void resetPlayersState() {
        for (Player player : participants) {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
                if (targetWorld != null) {
                    Location targetLocation = targetWorld.getSpawnLocation();
                    player.teleport(targetLocation);
                } else {
                    plugin.getLogger().warning("Target world not found!");
                }
            // Дополнительный сброс состояния игрока, если необходимо
        }
    }

    private void startCountdown() {
        new BukkitRunnable() {
            int count = Duel.this.config.getDuelCountdownTime();

            @Override
            public void run() {
                if (count > 0) {
                    for (Player player : participants) {
                        player.sendMessage("Duel starts in " + count + " seconds!");
                    }
                    count--;
                } else {
                    for (Player player : participants) {
                        player.sendMessage("Duel has started!");
                    }
                    state = BattleState.IN_PROGRESS;
                    startTime = System.currentTimeMillis();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private Player determineWinner() {
        if (participants.get(0).isDead()) return participants.get(1);
        if (participants.get(1).isDead()) return participants.get(0);
        if (!participants.get(0).isOnline() && wasDamaged) return participants.get(1); // Проверка на выход и урон
        if (!participants.get(1).isOnline() && wasDamaged) return participants.get(0); // Проверка на выход и урон
        if (participants.get(0).getHealth() > participants.get(1).getHealth()) return participants.get(0);
        if (participants.get(1).getHealth() > participants.get(0).getHealth()) return participants.get(1);
        return null; // В случае ничьей
    }

    private void announceWinner(Player winner) {
        for (Player player : participants) {
            player.sendMessage(winner.getName() + " has won the duel!");
        }
    }

    private void announceDraw() {
        for (Player player : participants) {
            player.sendMessage("The duel ended in a draw!");
        }
    }

    private void giveReward(Player winner) {
        BattleReward reward = new BattleReward(100, 1000); // Пример: 100 опыта и 1000 монет
        reward.giveReward(winner);
    }

    private void updateStatistics(Player winner) {
        Player loser = participants.get(0).equals(winner) ? participants.get(1) : participants.get(0);

        statistics.recordWin(winner);
        statistics.recordLoss(loser);
        statistics.recordKill(winner);
        statistics.recordDeath(loser);
        statistics.recordAddRate(winner);
        statistics.recordRedRate(loser);

    }

    private void updateStatisticsForDraw() {
        for (Player player : participants) {
            statistics.recordDraw(player);
        }
    }

    @Override
    protected void onPlayerKill(Player killer, Player victim) {
        super.onPlayerKill(killer, victim);
        if (statistics == null) {
            plugin.getLogger().warning("BattleStatistics is null in Duel.onPlayerKill");
            statistics = new BattleStatistics(plugin);
        }
        statistics.recordKill(killer);
        statistics.recordDeath(victim);
    }

    public void checkTimeLimit() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime > config.getDuelMaxDuration() * 1000) {
            plugin.getLogger().info("Окончание по времени");
            end(); // Завершаем дуэль, если превышен лимит времени
        }
    }

    public void cancelDuel(Player quitter, Player winner) {
        state = BattleState.ENDING;
        announceWinner(winner);
        updateStatistics(winner);
        resetPlayersState();
        state = BattleState.FINISHED;
    }

    public void update() {
        if (state == BattleState.IN_PROGRESS) {
            checkTimeLimit();
            // Дополнительные проверки, если необходимо
        }
    }

    public void onPlayerDamage(Player player) {
        if (participants.contains(player)) {
            wasDamaged = true;
        }
    }
}