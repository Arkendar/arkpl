package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.PlayerData;

public class IceWave extends AbstractAbility{
    private static final String ABILITY_NAME = "ICE_WAVE";

    public IceWave(LifeAndDie plugin, PlayerData playerData) {
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
        useIceWave(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }

    public void useIceWave(Player player) {
        player.sendMessage("§bВы активировали Ледяную волну!");

        Location center = player.getLocation();

        new BukkitRunnable() {
            double radius = 0.5;
            int ticks = 0;

            @Override
            public void run() {
                if (radius > 10 || ticks > 100) { // Максимальный радиус 10 блоков или 5 секунд
                    this.cancel();
                    return;
                }

                for (double angle = 0; angle < 360; angle += 10) {
                    double radians = Math.toRadians(angle);
                    double x = radius * Math.cos(radians);
                    double z = radius * Math.sin(radians);
                    Location particleLoc = center.clone().add(x, 0.1, z);

                    player.getWorld().spawnParticle(Particle.SNOWBALL, particleLoc, 5, 0.1, 0.1, 0.1, 0);
                    player.getWorld().spawnParticle(Particle.SNOW_SHOVEL, particleLoc, 1, 0, 0, 0, 0);
                }

                for (Entity entity : player.getWorld().getNearbyEntities(center, radius, 2, radius)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        LivingEntity target = (LivingEntity) entity;
                        int slownessDuration = 100 + ticks; // Увеличиваем длительность с каждым тиком
                        int slownessAmplifier = Math.min(30, ticks / 5); // Увеличиваем силу замедления, максимум до 4 уровня

                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, slownessDuration, slownessAmplifier));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, slownessDuration, 128));


                        target.damage(1);

                        Vector knockback = target.getLocation().toVector().subtract(center.toVector()).normalize().multiply(0.3);
                        knockback.setY(0.1);
                        target.setVelocity(knockback);
                    }
                }

                player.getWorld().playSound(center, Sound.BLOCK_GLASS_BREAK, 0.5f, 1.0f);

                radius += 0.2;
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
