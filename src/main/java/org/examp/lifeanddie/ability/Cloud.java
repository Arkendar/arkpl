package org.examp.lifeanddie.ability;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.PlayerData;

import java.util.Random;

public class Cloud extends AbstractAbility{
    private static final String ABILITY_NAME = "CLOUD";

    public Cloud(LifeAndDie plugin, PlayerData playerData) {
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
        useCloud(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }


    private void useCloud(Player player) {

        Location targetLocation = player.getTargetBlock(null, 15).getLocation();

        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0)); // 60 тиков = 3 секунды

        player.playSound(player.getLocation(), Sound.ENTITY_VEX_DEATH, 2.0f, 2.0f);

        new BukkitRunnable() {
            int count = 0;
            final Random random = new Random();
            final Location cloudLocation = targetLocation.clone().add(0, 10, 0); // Локация для темной тучи

            @Override
            public void run() {
                if (count >= 20) { // Спавним 20 зелий (можно изменить)
                    this.cancel();
                    return;
                }

                double offsetX = (random.nextDouble() - 0.5) * 5;
                double offsetZ = (random.nextDouble() - 0.5) * 5;

                Location potionLocation = targetLocation.clone().add(offsetX, 10, offsetZ);

                ItemStack potionItem = new ItemStack(Material.SPLASH_POTION);
                PotionMeta meta = (PotionMeta) potionItem.getItemMeta();
                meta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 1), true);
                potionItem.setItemMeta(meta);

                ThrownPotion thrownPotion = potionLocation.getWorld().spawn(potionLocation, ThrownPotion.class);
                thrownPotion.setItem(potionItem);

                // Устанавливаем траекторию зелья вниз
                thrownPotion.setVelocity(new Vector(0, -0.5, 0));

                // Воспроизводим звук и частицы
                potionLocation.getWorld().playSound(potionLocation, Sound.ENTITY_WITCH_THROW, 0.5f, 1.0f);
                potionLocation.getWorld().spawnParticle(Particle.SMOKE_NORMAL, potionLocation, 10, 0.2, 0.2, 0.2, 0.1);

                // Создаем темную тучу из частиц
                double radius = 3.0; // Радиус кругов
                double centerX1 = 0.0; // Центр первого круга по оси X
                double centerZ1 = 0.0; // Центр первого круга по оси Z


                for (double x = -radius; x <= radius; x += 0.25) {
                    for (double z = -radius; z <= radius; z += 0.25) {
                        double distanceFromCenter1 = Math.sqrt((x - centerX1) * (x - centerX1) + (z - centerZ1) * (z - centerZ1));
                        if (distanceFromCenter1 <= radius) {
                            Location particleLoc1 = cloudLocation.clone().add(x, 0, z);
                            cloudLocation.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particleLoc1, 1, 0.2, 0.2, 0.2, 0.03);
                        }
                    }
                }


                count++;
            }
        }.runTaskTimer(plugin, 0L, 5L); // Запускаем каждые 5 тиков (0.25 секунды)
    }
}
