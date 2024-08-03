package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.PlayerData;

public class ChaosBearer extends AbstractAbility{
    private static final String ABILITY_NAME = "CHAOS_BEARER";

    public ChaosBearer(LifeAndDie plugin, PlayerData playerData) {
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
        useChaosBearer(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
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

    private void useChaosBearer(Player player) {
        Location startLocation = player.getLocation();
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1, false, false));
        removeFallDamage(player, 80);

        new BukkitRunnable() {
            final int dashes = 3; // Количество рывков
            int currentDash = 0;

            @Override
            public void run() {
                if (currentDash < dashes) {
                    Vector direction = player.getLocation().getDirection().normalize();
                    direction.multiply(5); // Рывок на 5 блоков

                    Location newLocation = player.getLocation().add(direction);


                    // Рывок
                    double dashStrength = 0.35;
                    Vector dashVector = direction.multiply(dashStrength);
                    player.setVelocity(dashVector);

                    player.getWorld().playSound(newLocation, Sound.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

                    new BukkitRunnable() {
                        int count = 0;

                        @Override
                        public void run() {
                            if (count >= 15) {
                                this.cancel();
                                return;
                            }
                            player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getLocation(), 3, 0.3, 0.3, 0.3, 0.05);
                            count++;

                            for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 1, 1, 1)) {
                                if (entity instanceof Player && entity != player) {
                                    Location particleLocation = player.getLocation().clone();
                                    particleLocation.setY(particleLocation.getY() + 1.0);
                                    Player target = (Player) entity;
                                    target.damage(2.0);
                                    player.getWorld().spawnParticle(Particle.FALLING_DUST, particleLocation, 30, 1, 2, 1, 0.5);
                                }
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 2L);

                    currentDash++;
                } else {
                    player.teleport(startLocation);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 15);
    }
}
