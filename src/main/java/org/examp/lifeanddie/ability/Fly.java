package org.examp.lifeanddie.ability;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.PlayerData;

public class Fly extends AbstractAbility{
    private static final String ABILITY_NAME = "FLY";

    public Fly(LifeAndDie plugin, PlayerData playerData) {
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
        useFly(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }
    private void useFly(Player player) {


        Vector direction = new Vector(0, 1, 0);
        direction.multiply(1.5);

        Location newLocation = player.getLocation().add(direction);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);

        // Взлёт
        player.setVelocity(direction);
        player.getWorld().playSound(newLocation, Sound.ITEM_ELYTRA_FLYING, 1.0F, 1.0F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 0)); // 40 тиков = 3 секунды
        removeFallDamage(player, 100);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 10, 0)); // 40 тиков = 3 секунды
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
        }, 70L);

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 10) {
                    this.cancel();
                    return;
                }
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.3, 0.3, 0.3, 0.05);
                count++;
            }
        }.runTaskTimer(plugin, 0L, 4L);
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
