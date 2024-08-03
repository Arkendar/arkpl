package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.PlayerData;

public class DashTeleport extends AbstractAbility{
    private static final String ABILITY_NAME = "DASH_TELEPORT";

    public DashTeleport(LifeAndDie plugin, PlayerData playerData) {
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
        useDashTeleport(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }


    private void useDashTeleport(Player player) {
        // Определяем направление движения игрока
        Vector direction = player.getLocation().getDirection().normalize();

        // Количество телепортаций
        int teleportCount = 10;

        // Задержка между телепортациями
        int teleportDelay = 5; // Например, 5 тиков (0.25 секунды)

        new BukkitRunnable() {
            int currentTeleport = 0;

            @Override
            public void run() {
                if (currentTeleport >= teleportCount) {
                    this.cancel();
                    return;
                }

                // Определяем точку телепортации
                Location targetLocation = player.getLocation().clone();
                double teleportDistance = 1 + (Math.random() * 3); //дальность телепортации

                // Вычисляем вектор для телепортации в сторону
                Vector sideVector = direction.crossProduct(new Vector(0, 1, 0)).normalize().multiply(teleportDistance);

                // Определяем направление телепортации (влево или вправо)
                boolean teleportRight = Math.random() < 0.5;
                if (teleportRight) {
                    targetLocation.add(sideVector);
                } else {
                    targetLocation.subtract(sideVector);
                }

                // Проверяем, не пересекается ли точка телепортации со стеной или землей
                if (targetLocation.getBlock().getType() != Material.AIR) {
                    // Ищем ближайшую свободную точку для телепортации, двигаясь назад
                    targetLocation.subtract(sideVector); // Возвращаемся к исходной позиции
                    for (int i = 1; i <= 5; i++) {
                        if (targetLocation.add(sideVector.multiply(0.5)).getBlock().getType() == Material.AIR) { // Двигаемся с шагом 0.5
                            break;
                        }
                    }
                }

                // Телепортируем игрока
                player.teleport(targetLocation);

                // Создаем частицы телепортации
                player.getWorld().spawnParticle(Particle.SPELL_WITCH, targetLocation, 50, 0.3, 2, 0.3, 0.01);

                // Добавляем звук телепортации
                player.getWorld().playSound(targetLocation, Sound.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

                currentTeleport++;
            }
        }.runTaskTimer(plugin, 0L, teleportDelay);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 10));
    }
}
