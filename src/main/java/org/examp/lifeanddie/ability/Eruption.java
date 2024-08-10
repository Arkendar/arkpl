package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.player.PlayerData;

public class Eruption extends AbstractAbility{
    private static final String ABILITY_NAME = "ERUPTION";

    public Eruption(LifeAndDie plugin, PlayerData playerData) {
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
        useEruption(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }


    private void useEruption(Player player) {
        Location location = player.getLocation();
        World world = player.getWorld();

        new BukkitRunnable() {
            int ticks = 0;
            final int DURATION = 140;

            public void run() {
                if (ticks >= DURATION) {
                    this.cancel();
                    return;
                }

                // Основной столб огня
                for (double y = 0; y < 7; y += 0.5) {
                    Location flameLoc = location.clone().add(0, y, 0);
                    world.spawnParticle(Particle.FLAME, flameLoc, 2, 0.3, 0, 0.3, 0.01);
                    world.spawnParticle(Particle.SMOKE_LARGE, flameLoc, 1, 0.3, 0, 0.3, 0.01);
                }

                // Разлетающиеся частицы
                for (int i = 0; i < 10; i++) {
                    double angle = Math.random() * 2 * Math.PI;
                    double radius = Math.random() * 3;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    double y = Math.random() * 7;

                    Location particleLoc = location.clone().add(x, y, z);
                    world.spawnParticle(Particle.LAVA, particleLoc, 1, 0, 0, 0, 0);

                    if (Math.random() < 0.1) {
                        world.spawnParticle(Particle.FLAME, particleLoc, 1, 0.1, 0.1, 0.1, 0.05);
                    }
                }

                // Поджигаем игроков в радиусе каждый тик
                for (Entity entity : world.getNearbyEntities(location, 7, 7, 7)) {
                    if (entity instanceof Player && entity != player) {
                        entity.setFireTicks(60); // Поджигаем на 3 секунды
                    }
                }

                // Воспроизводим звук каждые 20 тиков (1 секунду)
                if (ticks % 20 == 0) {
                    world.playSound(location, Sound.BLOCK_FIRE_AMBIENT, 1.0f, 1.0f);
                    world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 0.5f);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
    }

}
