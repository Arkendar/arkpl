package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.player.PlayerData;

public class Skyfall extends AbstractAbility{
    private static final String ABILITY_NAME = "SKYFALL";

    public Skyfall(LifeAndDie plugin, PlayerData playerData) {
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
        useSkyfall(player);
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

    private void useSkyfall(Player player) {

        Player target = null;
        double closestAngle = Double.MAX_VALUE;

        Vector playerDirection = player.getLocation().getDirection().normalize();

        // Поиск ближайшего игрока по направлению взгляда
        for (Player potentialTarget : player.getWorld().getPlayers()) {
            if (potentialTarget.equals(player)) continue;

            Vector directionToTarget = potentialTarget.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            double angle = playerDirection.angle(directionToTarget);

            if (angle < closestAngle) {
                closestAngle = angle;
                target = potentialTarget;
            }
        }

        if (target == null) {
            player.sendMessage("Нет доступных целей для использования умения.");
            return;
        }

        Location targetLocation = target.getLocation().clone();
        targetLocation.setY(targetLocation.getY() + 10); // Телепортируем игрока на 10 блоков выше цели

        player.teleport(targetLocation);

        removeFallDamage(player, 60);

        // Запуск визуальных эффектов и механики падения
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 20) {
                    this.cancel();
                    return;
                }

                Location particleLocation = player.getLocation().clone();
                particleLocation.setY(particleLocation.getY() + 1.0);

                player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getLocation(), 30, 0.1, 0.1, 0.1, 0.01);
                player.getWorld().spawnParticle(Particle.LAVA, particleLocation, 10, 0.5, 0, 0.5, 0);

                for (Entity entity : particleLocation.getWorld().getNearbyEntities(particleLocation, 2, 2, 2)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        ((LivingEntity) entity).damage(3);
                    }
                }
                count++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
