package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.PlayerData;

public class WrathStorm extends AbstractAbility{
    private static final String ABILITY_NAME = "WRATH_STORM";

    public WrathStorm(LifeAndDie plugin, PlayerData playerData) {
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
        useWrathStorm(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }

    private void useWrathStorm(Player player) {


        Location playerLocation = player.getLocation();

        // Расчет радиуса и угла для каждого луча
        int numBeams = 5; // Количество лучей
        double angleStep = 360.0 / numBeams;

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 20;

            @Override
            public void run() {
                if (ticks >= maxTicks) {
                    this.cancel();
                    return;
                }

                // Получение направления взгляда игрока
                Vector direction = player.getEyeLocation().getDirection();

                for (int i = 0; i < numBeams; i++) {
                    // Расчет начальной и конечной точек луча
                    double angle = angleStep * i;
                    double radius = 2 + Math.sin(ticks * 0.2) * 2.5; // Увеличиваем максимальный радиус
                    double xOffset = radius * Math.cos(Math.toRadians(angle));
                    double zOffset = radius * Math.sin(Math.toRadians(angle));

                    Location startLocation = playerLocation.clone().add(xOffset, 1 + Math.random() * 3, zOffset);
                    Location landingLocation = startLocation.clone().add(direction.clone().multiply(10)); // Используем направление взгляда

                    // Создание частиц и нанесение урона
                    for (double t = 0; t < 20; t += 1) {
                        Location particleLocation = startLocation.clone().add(direction.clone().multiply(t));
                        player.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 3, 0, 0, 0, 0);
                        //player.getWorld().spawnParticle(Particle.BLOCK_DUST, particleLocation, 3, 0, 0, 0, 0.5);
                        player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, particleLocation, 0, 3, 1, 3, 0.01);

                        for (Entity entity : particleLocation.getWorld().getNearbyEntities(particleLocation, 1, 1, 1)) {
                            if (entity instanceof LivingEntity && entity != player) {
                                ((LivingEntity) entity).damage(2);
                            }
                        }
                    }
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 3L);

        player.sendMessage("Вы использовали умение Буря Гнева!");
    }
}
