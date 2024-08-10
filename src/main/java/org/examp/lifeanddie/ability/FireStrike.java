package org.examp.lifeanddie.ability;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitRunnable;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.player.PlayerData;

public class FireStrike extends AbstractAbility {
    private static final String ABILITY_NAME = "FIRE_STRIKE";
    private static final int DURATION = 40; // тики (2 секунды)
    private static final double SPIRAL_RADIUS = 1.5;
    private static final double SPIRAL_SPEED = Math.PI / 8;

    public FireStrike(LifeAndDie plugin, PlayerData playerData) {
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
        useFireStrike(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }

    private void useFireStrike(Player player) {
        Location startLocation = player.getEyeLocation();
        Vector direction = startLocation.getDirection();

        new BukkitRunnable() {
            int ticks = 0;
            double spiralAngle = 0;

            @Override
            public void run() {
                if (ticks >= DURATION) {
                    this.cancel();
                    return;
                }

                Location currentLocation = startLocation.clone().add(direction.clone().multiply(ticks * 0.5));

                // Создаем спиральный эффект
                for (int i = 0; i < 3; i++) {
                    double angle = spiralAngle + (2 * Math.PI / 3 * i);
                    Vector offset = createPerpendicularVector(direction, angle, SPIRAL_RADIUS);

                    Location particleLocation = currentLocation.clone().add(offset);
                    player.getWorld().spawnParticle(Particle.FLAME, particleLocation, 5, 0.1, 0.1, 0.1, 0.05);
                    player.getWorld().spawnParticle(Particle.LAVA, particleLocation, 1, 0.1, 0.1, 0.1, 0);
                }

                // Основной огненный эффект
                //player.getWorld().spawnParticle(Particle.FLAME, currentLocation, 5, 0.3, 0.3, 0.3, 0.005);
                player.getWorld().spawnParticle(Particle.LAVA, currentLocation, 3, 0.2, 0.2, 0.2, 0);

                if (ticks % 5 == 0) {
                    player.getWorld().playSound(currentLocation, Sound.ITEM_FIRECHARGE_USE, 0.5F, 1.0F);
                }

                // Проверка на попадание
                for (Entity entity : player.getWorld().getNearbyEntities(currentLocation, 2, 2, 2)) {
                    if (entity instanceof Player && entity != player) {
                        Player target = (Player) entity;
                        target.damage(5.0);
                        target.setFireTicks(40); // Поджог на 2 секунды

                        // Эффект попадания
                        target.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, target.getLocation(), 1, 0, 0, 0, 0);
                        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_BURN, 1.0F, 1.0F);
                    }
                }

                spiralAngle += SPIRAL_SPEED;
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Начальный эффект запуска
        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 1, 0, 0, 0, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0F, 0.5F);

        player.sendMessage("Вы использовали Огненный Удар!");
    }

    private Vector createPerpendicularVector(Vector direction, double angle, double length) {
        Vector perpendicular = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
        Vector up = direction.getCrossProduct(perpendicular);
        return perpendicular.multiply(Math.cos(angle) * length).add(up.multiply(Math.sin(angle) * length));
    }
}