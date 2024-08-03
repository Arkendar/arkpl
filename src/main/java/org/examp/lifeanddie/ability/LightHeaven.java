package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.PlayerData;

public class LightHeaven extends AbstractAbility{
    private static final String ABILITY_NAME = "LIGHT_HEAVEN";

    public LightHeaven(LifeAndDie plugin, PlayerData playerData) {
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
        useLightHeaven(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }
    private void useLightHeaven(Player player) {
        Location targetLocation = player.getTargetBlock(null, 100).getLocation().add(0, 1, 0); // Призыв на блок, на который смотрит игрок

        // Создаем светящийся столб и эффекты
        new BukkitRunnable() {
            int duration = 0;

            @Override
            public void run() {
                if (duration >= 20) {
                    this.cancel();
                    return;
                }
                int PARTICLE_COUNT = 20;
                int CIRCLE_COUNT = 20;
                // Создание частиц
                for (int circle = 0; circle < CIRCLE_COUNT; circle++) {
                    double altitude = circle * 0.5; // Высота для каждого круга
                    double radius = 1.0 - (circle * 0.05); // Уменьшаем радиус на 0.1 с каждым новым кругом
                    for (int i = 0; i < PARTICLE_COUNT; i++) {
                        double angle = (2 * Math.PI / PARTICLE_COUNT) * i; // Угол для каждой частицы
                        double x = Math.cos(angle) * radius; // Используем уменьшающийся радиус
                        double z = Math.sin(angle) * radius; // Используем уменьшающийся радиус

                        // Позиция частицы
                        Location particleLocation = targetLocation.clone().add(x, altitude, z);
                        targetLocation.getWorld().spawnParticle(Particle.END_ROD, particleLocation, 1, 0, 0, 0, 0); // Создаем частицы
                    }
                }

                // Лечение союзников и урон противникам
                for (Entity entity : targetLocation.getWorld().getNearbyEntities(targetLocation, 2, 5, 2)) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        if (livingEntity.getUniqueId().equals(player.getUniqueId())) {
                            // Лечим игрока
                            livingEntity.setHealth(Math.min(livingEntity.getHealth() + 2, livingEntity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue())); // Исцеление на 2 (10% от 20)
                        } else {
                            livingEntity.damage(1);
                        }
                    }
                }

                duration++;
            }
        }.runTaskTimer(plugin, 0L, 10L);
        Location downTarget = targetLocation;
        for(int t = 0; t <= 3; t++) {
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 8) {
                double x = t * Math.cos(theta);
                double y = 2 * Math.exp(-0.1 * t) * Math.sin(t) + 0.5; // Уменьшил высоту до 0.5
                double z = t * Math.sin(theta);
                downTarget.add(x, y, z);
                player.spawnParticle(Particle.END_ROD, downTarget, 1, 0, 0, 0, 0);
                downTarget.subtract(x, y, z);
            }
        }
    }
}
