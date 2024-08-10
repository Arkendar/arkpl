package org.examp.lifeanddie.ability;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.player.PlayerData;

public class EarthJaws extends AbstractAbility {
    private static final String ABILITY_NAME = "EARTH_JAWS";
    public EarthJaws(LifeAndDie plugin, PlayerData playerData) {
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
        Location targetLocation = player.getTargetBlock(null, 15).getLocation();
        summonJaws(player, targetLocation);
    }

    @Override
    public String getName() {
        return ABILITY_NAME;
    }

    private void summonJaws(Player player, Location location) {
        World world = player.getWorld();

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Location fangLocation = location.clone().add(new Vector(x, 1, z));

                Block blockBelow = fangLocation.getBlock().getRelative(BlockFace.DOWN);
                if (blockBelow.getType().isSolid()) {
                    world.spawnEntity(fangLocation, EntityType.EVOKER_FANGS);

                    world.getNearbyEntities(fangLocation, 1, 1, 1).forEach(entity -> {
                        if (entity instanceof Player && entity != player) {
                            Player targetPlayer = (Player) entity;
                            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 3));
                            targetPlayer.damage(4.0, player);
                        }
                    });
                }
            }
        }
    }
}
