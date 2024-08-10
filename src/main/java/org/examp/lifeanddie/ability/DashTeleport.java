package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DashTeleport extends AbstractAbility {
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
        Player targetPlayer = getNearestPlayer(player);
        if (targetPlayer == null) {
            player.sendMessage("Нет ближайшего игрока для телепортации.");
            return;
        }

        Location targetLocation = targetPlayer.getLocation();
        List<Location> safeLocations = findSafeLocations(targetLocation, 5);

        if (safeLocations.isEmpty()) {
            player.sendMessage("Не найдено безопасных мест для телепортации.");
            return;
        }

        // Создаем облако частиц
        new BukkitRunnable() {
            int duration = 100; // 5 seconds (20 ticks per second)

            @Override
            public void run() {
                if (duration <= 0) {
                    this.cancel();
                    return;
                }

                for (int i = 0; i < 100; i++) {
                    Location fogLocation = getRandomLocationAround(targetLocation, 0, 8);
                    targetLocation.getWorld().spawnParticle(Particle.CLOUD, fogLocation, 2, 0.2, 0.2, 0.2, 0);
                }

                // Применяем эффект слепоты только к цели
                if (targetPlayer.isOnline() && targetPlayer.getLocation().distance(targetLocation) <= 8) {
                    targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                    targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0));
                }

                duration--;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Телепортации
        new BukkitRunnable() {
            int teleportCount = 0;
            final int maxTeleports = 5;

            @Override
            public void run() {
                if (teleportCount >= maxTeleports || teleportCount >= safeLocations.size()) {
                    this.cancel();
                    return;
                }

                Location teleportLocation = safeLocations.get(teleportCount);

                // Сохраняем текущее направление взгляда
                float yaw = player.getLocation().getYaw();
                float pitch = player.getLocation().getPitch();

                // Телепортируем игрока и устанавливаем сохраненное направление взгляда
                teleportLocation.setYaw(yaw);
                teleportLocation.setPitch(pitch);
                player.teleport(teleportLocation);

                // Спавним частицы и проигрываем звук
                player.getWorld().spawnParticle(Particle.CLOUD, teleportLocation, 50, 0.5, 2, 0.5, 0.01);
                player.getWorld().playSound(teleportLocation, Sound.ENTITY_ILLUSION_ILLAGER_MIRROR_MOVE, 1.0F, 1.0F);

                teleportCount++;
            }
        }.runTaskTimer(plugin, 0L, 10L); // Телепорт каждые 0.5 секунды (10 тиков)

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
    }

    private List<Location> findSafeLocations(Location center, int count) {
        List<Location> safeLocations = new ArrayList<>();
        int attempts = 0;
        int maxAttempts = count * 3; // Увеличиваем количество попыток

        while (safeLocations.size() < count && attempts < maxAttempts) {
            Location randomLocation = getRandomLocationAround(center, 3, 5);
            if (isSafeLocation(randomLocation)) {
                safeLocations.add(randomLocation);
            }
            attempts++;
        }

        return safeLocations;
    }

    private boolean isSafeLocation(Location location) {
        return location.getBlock().getType() == Material.AIR &&
                location.clone().add(0, 1, 0).getBlock().getType() == Material.AIR &&
                location.clone().add(0, -1, 0).getBlock().getType().isSolid();
    }

    private Player getNearestPlayer(Player player) {
        List<Player> otherPlayers = player.getWorld().getPlayers().stream()
                .filter(p -> !p.equals(player))
                .collect(Collectors.toList());

        Player nearestPlayer = null;
        double nearestDistance = 20;

        for (Player otherPlayer : otherPlayers) {
            double distance = player.getLocation().distance(otherPlayer.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPlayer = otherPlayer;
            }
        }

        return nearestPlayer;
    }

    private Location getRandomLocationAround(Location center, double minRadius, double maxRadius) {
        double radius = minRadius + Math.random() * (maxRadius - minRadius);
        double angle = Math.random() * 2 * Math.PI;
        double x = center.getX() + radius * Math.cos(angle);
        double z = center.getZ() + radius * Math.sin(angle);
        double y = center.getY() + (Math.random() - 0.5) * 4; // Вертикальный разброс ±2 блока
        return new Location(center.getWorld(), x, y, z);
    }
}