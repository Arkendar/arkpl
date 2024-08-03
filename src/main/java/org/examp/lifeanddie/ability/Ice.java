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

public class Ice extends AbstractAbility{
    private static final String ABILITY_NAME = "ICE";

    public Ice(LifeAndDie plugin, PlayerData playerData) {
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
        useIce(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }


    private void useIce(Player player) {
        Location playerLocation = player.getLocation();
        Location targetLocation = player.getTargetBlock(null, 20).getLocation();

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 20;

            @Override
            public void run() {
                if (ticks >= maxTicks) {
                    this.cancel();
                    return;
                }

                double radius = 8 + Math.sin(ticks * 0.2) * 2;
                double angle = ticks * 0.5;

                for (int i = 0; i < 5; i++) {
                    double x = Math.cos(angle + i * Math.PI * 0.4) * radius;
                    double z = Math.sin(angle + i * Math.PI * 0.4) * radius;
                    Location startLocation = playerLocation.clone().add(x, 10 + Math.random() * 2, z);
                    Location landingLocation = targetLocation.clone().add(Math.random() * 4 - 2, 0, Math.random() * 4 - 2);

                    Vector direction = landingLocation.toVector().subtract(startLocation.toVector()).normalize().multiply(0.6); // Увеличили скорость

                    new BukkitRunnable() {
                        double t = 0;

                        @Override
                        public void run() {
                            if (t >= 40) {
                                this.cancel();
                                return;
                            }

                            Location particleLocation = startLocation.clone().add(direction.clone().multiply(t));
                            player.getWorld().spawnParticle(Particle.SNOWBALL, particleLocation, 1, 0, 0, 0, 0);
                            player.getWorld().spawnParticle(Particle.SPELL_INSTANT, particleLocation, 1, 0, 0, 0, 0);

                            for (Entity entity : particleLocation.getWorld().getNearbyEntities(particleLocation, 1, 1, 1)) {
                                if (entity instanceof LivingEntity && entity != player) {
                                    ((LivingEntity) entity).damage(5);
                                }
                            }

                            t += 1;
                        }
                    }.runTaskTimer(plugin, 0L, 1L);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 3L);

        player.sendMessage("Вы использовали умение Лёд!");
    }
}
