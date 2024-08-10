package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.player.PlayerData;

public class WaveArrows extends AbstractAbility {
    private static final String ABILITY_NAME = "WAVE_ARROWS";
    private static final int ARROW_COUNT = 11;
    private static final double ARROW_SPREAD = Math.toRadians(15); // 22.5 градусов
    private static final double ARROW_SPEED = 2.0;
    private static final int ARROW_LIFETIME = 40; // тиков (2 секунды)

    public WaveArrows(LifeAndDie plugin, PlayerData playerData) {
        super(plugin, playerData);
    }

    @Override
    public void use(Player player, PlayerData playerData) {
        if (playerData.isInCooldown(ABILITY_NAME, player.getUniqueId())) {
            long cooldownTimeLeft = playerData.getCooldownTimeLeft(ABILITY_NAME, player.getUniqueId());
            sendCooldownMessage(player, ABILITY_NAME, cooldownTimeLeft);
            return;
        }

        shootWaveArrows(player);
        playerData.setCooldown(ABILITY_NAME, player.getUniqueId());
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }

    private void shootWaveArrows(Player player) {
        Location startLocation = player.getEyeLocation();
        Vector direction = startLocation.getDirection();

        // Эффект взрыва частиц при запуске
        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, startLocation, 1, 0, 0, 0, 0);
        player.getWorld().playSound(startLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);

        for (int i = 0; i < ARROW_COUNT; i++) {
            double angle = ARROW_SPREAD * (i - (ARROW_COUNT - 1) / 2.0);
            Vector arrowDirection = rotateVector(direction, angle);

            Arrow arrow = player.getWorld().spawnArrow(startLocation, arrowDirection, (float) ARROW_SPEED, 0);
            arrow.setShooter(player);
            arrow.setGravity(false);
            arrow.setCritical(true);

            new BukkitRunnable() {
                int ticks = 0;
                double spiralRadius = 0.5;
                double spiralAngle = 0;

                @Override
                public void run() {
                    if (ticks >= ARROW_LIFETIME || arrow.isDead()) {
                        // Эффект исчезновения
                        Location finalLocation = arrow.getLocation();
                        arrow.getWorld().spawnParticle(Particle.CLOUD, finalLocation, 20, 0.2, 0.2, 0.2, 0.05);
                        arrow.remove();
                        this.cancel();
                        return;
                    }

                    Location arrowLocation = arrow.getLocation();
                    Vector arrowVelocity = arrow.getVelocity().normalize();

                    // Спиральный след
                    for (int j = 0; j < 2; j++) {
                        double x = spiralRadius * Math.cos(spiralAngle + (Math.PI * j));
                        double y = spiralRadius * Math.sin(spiralAngle + (Math.PI * j));

                        // Создаем вектор, перпендикулярный направлению стрелы
                        Vector perpendicular = new Vector(-arrowVelocity.getZ(), 0, arrowVelocity.getX()).normalize();
                        Vector up = arrowVelocity.getCrossProduct(perpendicular);

                        Vector offset = perpendicular.multiply(x).add(up.multiply(y));
                        Location particleLocation = arrowLocation.clone().add(offset);

                        // Используем разные частицы для создания эффекта градиента
                        if (ticks < ARROW_LIFETIME / 2) {
                            arrowLocation.getWorld().spawnParticle(Particle.FLAME, particleLocation, 1, 0, 0, 0, 0);
                        } else {
                            arrowLocation.getWorld().spawnParticle(Particle.SPELL_WITCH, particleLocation, 1, 0, 0, 0, 0);
                        }
                    }

                    spiralAngle += Math.PI / 8;
                    ticks++;
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }

        player.sendMessage("Вы выпустили волну стрел!");
    }

    private Vector rotateVector(Vector vector, double angle) {
        double x = vector.getX() * Math.cos(angle) - vector.getZ() * Math.sin(angle);
        double z = vector.getX() * Math.sin(angle) + vector.getZ() * Math.cos(angle);
        return new Vector(x, vector.getY(), z).normalize();
    }
}