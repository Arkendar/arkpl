package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.player.PlayerData;

public class Dash extends AbstractAbility {
    private static final String ABILITY_NAME = "DASH";

    public Dash(LifeAndDie plugin, PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void use(Player player, PlayerData playerData) {
        if (playerData.isInCooldown(ABILITY_NAME, player.getUniqueId())) {
            long cooldownTimeLeft = playerData.getCooldownTimeLeft(ABILITY_NAME, player.getUniqueId());
            sendCooldownMessage(player, ABILITY_NAME, cooldownTimeLeft);
            return;
        }

        playerData.setCooldown(ABILITY_NAME, player.getUniqueId());
        useDash(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }

    private void useDash(Player player) {
        removeFallDamage(player, 60);

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 15) {
                    this.cancel();
                    return;
                }
                Location particleLocation = player.getLocation().clone();
                particleLocation.setY(particleLocation.getY() + 1.0);

                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 1, 0.3, 0.3, 0.3, 0.05);
                player.getWorld().spawnParticle(Particle.END_ROD, particleLocation, 10, 1, 0, 1, 0);
                count++;
            }
        }.runTaskTimer(plugin, 0L, 2L);

        Vector direction = player.getLocation().getDirection().normalize();
        direction.multiply(5); // Рывок на 5 блоков

        Location newLocation = player.getLocation().add(direction);
        double dashStrength = 0.35;
        Vector dashVector = direction.multiply(dashStrength);

        player.setVelocity(dashVector);
        player.getWorld().playSound(newLocation, Sound.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
    }

    private void removeFallDamage(Player player, int durationTicks) {
        player.setFallDistance(0);
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= durationTicks) {
                    this.cancel();
                    return;
                }
                player.setFallDistance(0);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
