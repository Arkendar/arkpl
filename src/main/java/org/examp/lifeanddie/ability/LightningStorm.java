package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.player.PlayerData;

public class LightningStorm extends AbstractAbility{
    private static final String ABILITY_NAME = "LIGHTNING_STORM";

    public LightningStorm(LifeAndDie plugin, PlayerData playerData) {
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
        useLightningStorm(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }

    public void useLightningStorm(Player player) {
        player.sendMessage("Вы вызвали Грозу!");

        Location startLocation = player.getLocation();
        Vector direction = player.getLocation().getDirection().setY(0).normalize();

        new BukkitRunnable() {
            int ticks = 0;
            final Location currentLocation = startLocation.clone();

            @Override
            public void run() {
                if (ticks >= 20) { // 5 секунд (4 удара молнии в секунду)
                    this.cancel();
                    return;
                }

                // Создаем молнию
                currentLocation.add(direction.clone().multiply(2)); // Двигаемся на 2 блока вперед
                World world = currentLocation.getWorld();
                world.strikeLightningEffect(currentLocation);

                // Наносим урон и отбрасываем игроков в радиусе 3 блоков
                for (Entity entity : world.getNearbyEntities(currentLocation, 3, 3, 3)) {
                    if (entity instanceof Player && entity != player) {
                        Player targetPlayer = (Player) entity;
                        targetPlayer.damage(4.0, player); // Наносим 5 единиц урона

                        // Отбрасываем игрока
                        Vector knockback = targetPlayer.getLocation().subtract(currentLocation).toVector().normalize().multiply(1.5).setY(0.5);
                        targetPlayer.setVelocity(knockback);
                    }
                }

                // Создаем эффект частиц
                world.spawnParticle(Particle.FALLING_DUST, currentLocation, 50, 1, 1, 1, 0.1);

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 5L); // Запускаем каждые 5 тиков (4 раза в секунду)

        // Воспроизводим звук активации
        player.getWorld().playSound(startLocation, Sound.ENTITY_LIGHTNING_THUNDER, 1.0f, 1.0f);
    }
}
