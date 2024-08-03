package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Fortress extends AbstractAbility{
    private static final String ABILITY_NAME = "FORTRESS";

    public Fortress(LifeAndDie plugin, PlayerData playerData) {
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
        useFortress(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }


    private void useFortress(Player player) {

        Location location = player.getLocation().add(player.getLocation().getDirection().multiply(2));
        diamondResist(player, location);
        player.getWorld().playSound(location, Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
    }

    public void diamondResist(Player player, Location location) {
        PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 1, true, false);
        player.addPotionEffect(resistance);
        PotionEffect glowing = new PotionEffect(PotionEffectType.GLOWING, 300, 1, true, false);
        player.addPotionEffect(glowing);
        player.spawnParticle(Particle.TOTEM, location, 20, 1, 1, 1, 0);

        // Список для хранения задач и щитов
        List<BukkitRunnable> tasks = new ArrayList<>();
        List<ArmorStand> shields = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ArmorStand shield = location.getWorld().spawn(location.clone().add(0, 1, 0), ArmorStand.class);
            shield.setVisible(false);
            shield.setGravity(false);
            shield.setMarker(true);
            shield.setHelmet(new ItemStack(Material.SHIELD));
            shields.add(shield);

            int finalI = i;
            BukkitRunnable task = new BukkitRunnable() {
                double angle = finalI * 90;

                @Override
                public void run() {
                    if (!player.isOnline() || shield.isDead()) {
                        shield.remove();
                        cancel();
                        return;
                    }

                    Location playerLocation = player.getLocation().add(0, 1, 0);
                    angle += 5;  // Угол поворота
                    double radians = Math.toRadians(angle);
                    double radius = 1.5;  // Радиус вращения
                    double x = playerLocation.getX() + radius * Math.cos(radians);
                    double z = playerLocation.getZ() + radius * Math.sin(radians);
                    Location newLocation = new Location(playerLocation.getWorld(), x, playerLocation.getY(), z);
                    shield.teleport(newLocation);
                }
            };
            tasks.add(task);
            task.runTaskTimer(plugin, 0, 1);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (BukkitRunnable task : tasks) {
                    task.cancel();
                }
                for (ArmorStand shield : shields) {
                    shield.remove();
                }
            }
        }.runTaskLater(plugin, 300); // 300 тиков = 15 секунд
    }

}
