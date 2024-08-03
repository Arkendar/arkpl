package org.examp.lifeanddie.ability;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.PlayerData;

public class MagicStaff extends AbstractAbility{
    private static final String ABILITY_NAME = "MAGIC_STAFF";

    public MagicStaff(LifeAndDie plugin, PlayerData playerData) {
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
        useMagicStaff(player);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }

    private void useMagicStaff(Player player) {
        Location startLocation = player.getEyeLocation();
        Vector direction = startLocation.getDirection().normalize();
        World world = player.getWorld();

        for (int i = 0; i < 20; i++) {
            Location particleLocation = startLocation.clone().add(direction.clone().multiply(i));
            if (particleLocation.getBlock().getType().isSolid()) {
                break;
            }

            world.spawnParticle(Particle.FLAME, particleLocation, 20, 0, 1, 0, 0);
            world.playSound(particleLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

            for (Entity entity : world.getNearbyEntities(particleLocation, 1, 1, 1)) {
                if (entity instanceof LivingEntity && entity != player) {
                    LivingEntity target = (LivingEntity) entity;
                    target.damage(6.0, player);
                    return;
                }
            }
        }
    }
}
