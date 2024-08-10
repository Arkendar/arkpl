package org.examp.lifeanddie.fraction.fractions;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.examp.lifeanddie.fraction.Fraction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.Location;
import org.examp.lifeanddie.fraction.FractionManager;

import java.util.Random;

public class ShadowBrotherhoodFraction implements Fraction, Listener {
    private static final String NAME = "Братство Теней";
    private static final double POISON_CHANCE = 0.2; // 20% шанс отравить атакующего
    private static final double TELEPORT_CHANCE = 0.1; // 10% шанс телепортироваться
    private final FractionManager fractionManager;

    private Random random = new Random();

    public ShadowBrotherhoodFraction(FractionManager fractionManager) {
        this.fractionManager = fractionManager;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void applyEffects(Player player) {
        setAttribute(player, Attribute.GENERIC_MOVEMENT_SPEED, 0.15);
        setAttribute(player, Attribute.GENERIC_MAX_HEALTH, -4);
    }

    @Override
    public void removeEffects(Player player) {
        setAttribute(player, Attribute.GENERIC_MOVEMENT_SPEED, -0.15);
        setAttribute(player, Attribute.GENERIC_MAX_HEALTH, 4);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        Fraction playerFraction = fractionManager.getPlayerFraction(player);
        if (!(playerFraction instanceof ShadowBrotherhoodFraction)) return;

        // Яд
        if (event.getDamager() instanceof Player && random.nextDouble() < POISON_CHANCE) {
            Player attacker = (Player) event.getDamager();
            attacker.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0)); // 5 секунд отравления
            player.sendMessage("Вы отравили атакующего!");
        }

        // Шанс телепортироваться
        if (random.nextDouble() < TELEPORT_CHANCE) {
            Location attackerLocation = event.getDamager().getLocation();
            Location teleportLocation = attackerLocation.clone().add(0, 3, 0); // Телепортация на 3 блока выше атакующего
            player.teleport(teleportLocation);
            player.sendMessage("Вы телепортировались над противником!");
        }
    }

    public void activateDangerSense(Player player) {
        for (Player nearbyPlayer : player.getWorld().getPlayers()) {
            if (nearbyPlayer != player && nearbyPlayer.getLocation().distance(player.getLocation()) <= 10) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false)); // 5 секунд ускорения
                break;
            }
        }
    }

    public void checkInvisibility(Player player) {
        boolean shouldBeInvisible = true;
        for (Player nearbyPlayer : player.getWorld().getPlayers()) {
            if (nearbyPlayer != player && nearbyPlayer.getLocation().distance(player.getLocation()) <= 20) {
                shouldBeInvisible = false;
                break;
            }
        }

        if (shouldBeInvisible) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 50, 0, false, false));
        } else {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    private void setAttribute(Player player, Attribute attribute, double amount) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(attributeInstance.getBaseValue() + amount);
        }
    }
}