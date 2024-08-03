package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.PlayerData;

import java.util.List;

public class Burial extends AbstractAbility{
    private static final String ABILITY_NAME = "BURIAL";

    public Burial(LifeAndDie plugin, PlayerData playerData) {
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
        useBurial(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }

    private void useBurial(Player caster) {
        Player target = getTargetPlayer(caster, 10); // 10 блоков - максимальная дистанция

        if (target == null) {
            caster.sendMessage("Цель не найдена!");
            return;
        }

        Location originalLocation = target.getLocation().clone();
        Location teleportLocation = originalLocation.clone().add(0, -4, 0);

        // Телепортация цели вниз
        target.teleport(teleportLocation);

        // Задержка перед телепортацией обратно
        new BukkitRunnable() {
            @Override
            public void run() {
                target.teleport(originalLocation);

                // Дополнительные эффекты после возвращения
                target.getWorld().spawnParticle(Particle.FALLING_DUST, target.getLocation(), 50, 0.2, 2.2, 0.2, 0.1);
            }
        }.runTaskLater(plugin, 60); // 3 секунды (60 тиков)
    }

    private Player getTargetPlayer(Player caster, int maxDistance) {
        List<Entity> nearbyEntities = caster.getNearbyEntities(maxDistance, maxDistance, maxDistance);
        Vector direction = caster.getLocation().getDirection();
        Player target = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player && entity != caster) {
                Vector toEntity = entity.getLocation().toVector().subtract(caster.getLocation().toVector());
                double angle = direction.angle(toEntity);
                double distance = toEntity.length();

                if (angle < Math.PI / 8 && distance < closestDistance) { // угол примерно 22.5 градуса
                    closestDistance = distance;
                    target = (Player) entity;
                }
                entity.getWorld().spawnParticle(Particle.FALLING_DUST, entity.getLocation(), 50, 0.3, 2.2, 0.3, 0.1);
            }
        }

        return target;
    }
}
