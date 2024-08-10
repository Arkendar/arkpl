package org.examp.lifeanddie.battle;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.examp.lifeanddie.LifeAndDie;

import java.util.Optional;

public class BattleListener implements Listener {
    private final BattleManager battleManager;
    private final DuelManager duelManager;
    private final LifeAndDie plugin;

    public BattleListener(LifeAndDie plugin, BattleManager battleManager, DuelManager duelManager) {
        this.plugin = plugin;
        this.battleManager = battleManager;
        this.duelManager = duelManager;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player damaged = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        battleManager.getBattleForPlayer(damaged).ifPresent(battle -> {
            if (!battle.getParticipants().contains(damager)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.getLogger().info("Player death event triggered");
        Player victim = event.getEntity();
        Player killer = victim.getKiller();


        plugin.getLogger().info("Checking battle for player: " + victim.getName());
        Optional<AbstractBattle> battle = battleManager.getBattleForPlayer(victim);

        if (battle.isPresent()) {
            plugin.getLogger().info("Battle found for player");
            if (battle.get() instanceof Duel) {
                plugin.getLogger().info("Battle is a Duel");
                Duel duel = (Duel) battle.get();
                if (!duel.isFinished()) {
                    plugin.getLogger().info("Duel is not finished, ending it now");
                    if (killer != null) {
                        duel.onPlayerKill(killer, victim);
                        plugin.resetPlayerCooldowns(killer);
                    }
                    plugin.resetPlayerCooldowns(victim);

                    duelManager.endDuel(duel);

                } else {
                    if (killer != null) {
                        duel.onPlayerKill(killer, victim);
                        plugin.resetPlayerCooldowns(killer);
                    }
                    plugin.resetPlayerCooldowns(victim);

                    duelManager.endDuel(duel);
                    plugin.getLogger().info("Duel is already finished");
                }
            } else {
                plugin.getLogger().info("Battle is not a Duel");
            }
        } else {
            plugin.getLogger().info("No battle found for player");
        }
    }

    /*
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.getLogger().info("Player death event triggered");
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        plugin.getLogger().info("Checking battle for player: " + victim.getName());
        Optional<AbstractBattle> battle = battleManager.getBattleForPlayer(victim);

        if (battle.isPresent()) {
            plugin.getLogger().info("Battle found for player");
            if (battle.get() instanceof Duel) {
                plugin.getLogger().info("Battle is a Duel");
                Duel duel = (Duel) battle.get();
                if (!duel.isFinished()) {
                    plugin.getLogger().info("Duel is not finished, ending it now");
                    if (killer != null) {
                        duel.onPlayerKill(killer, victim);
                    }
                    duelManager.endDuel(duel);
                } else {
                    if (killer != null) {
                        duel.onPlayerKill(killer, victim);
                    }
                    duelManager.endDuel(duel);
                    plugin.getLogger().info("Duel is already finished");
                }
            } else {
                plugin.getLogger().info("Battle is not a Duel");
            }
        } else {
            plugin.getLogger().info("No battle found for player");
        }
    }
     */

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<AbstractBattle> optionalBattle = battleManager.getBattleForPlayer(player);

        if (optionalBattle.isPresent()) {
            AbstractBattle battle = optionalBattle.get();
            if (battle instanceof Duel) {
                Duel duel = (Duel) battle;
                duel.onPlayerDamage(player); // Вызываем onPlayerDamage перед end()
                duelManager.endDuel(duel);
            } else {
                battle.end();
            }
        }
    }
}