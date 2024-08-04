package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.PlayerData;

public class AstralSphere extends AbstractAbility{
    private static final String ABILITY_NAME = "ASTRAL_SPHERE";

    public AstralSphere(LifeAndDie plugin, PlayerData playerData) {
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
        useAstralSphere(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }

    private void useAstralSphere(Player player) {
        player.sendMessage("Вы активировали Астральную Сферу!");

        Location sphereLocation = player.getLocation();

        new BukkitRunnable() {
            int ticks = 0;
            final double radius = 5.0;
            int particleCounter = 0;

            @Override
            public void run() {
                if (ticks >= 32) { // 10 секунд (4 тика в секунду)
                    this.cancel();
                    return;
                }

                // Отталкиваем ближайших врагов
                for (Entity entity : sphereLocation.getWorld().getNearbyEntities(sphereLocation, radius, radius, radius)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        Vector direction = entity.getLocation().subtract(sphereLocation).toVector().normalize();
                        entity.setVelocity(direction.multiply(1));
                    }
                }

                // Восстанавливаем здоровье игрока
                if (ticks % 4 == 0) { // Каждую секунду (каждые 4 тика при 5 тиках обновления)
                    double newHealth = Math.min(player.getHealth() + 2, player.getMaxHealth());
                    player.setHealth(newHealth);
                }

                // Создаем вращающиеся частицы
                if (particleCounter % 3 == 0) { // Каждые 15 тиков
                    for (double phi = 0; phi <= Math.PI; phi += Math.PI / 15) {
                        double y = radius * Math.cos(phi);
                        for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 30) {
                            double x = radius * Math.cos(theta) * Math.sin(phi);
                            double z = radius * Math.sin(theta) * Math.sin(phi);

                            Location particleLoc = sphereLocation.clone().add(x, y + 1, z);
                            sphereLocation.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
                        }
                    }
                }

                particleCounter++;
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 4L); // Обновление каждые 5 тиков

        // Воспроизводим звук активации
        player.getWorld().playSound(sphereLocation, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
    }
}
