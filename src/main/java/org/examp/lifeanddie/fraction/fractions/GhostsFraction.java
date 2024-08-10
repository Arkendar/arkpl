package org.examp.lifeanddie.fraction.fractions;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.examp.lifeanddie.fraction.Fraction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.examp.lifeanddie.fraction.FractionManager;

import java.util.Random;

public class GhostsFraction implements Fraction, Listener {
    private static final String NAME = "Призраки";
    private static final double DODGE_CHANCE = 0.15; // 15% шанс уклонения
    private static final int GHOST_STEP_DURATION = 60; // 3 секунды (60 тиков)
    private static final float GHOST_STEP_SPEED = 0.3f; // Увеличение скорости на 30%
    private final FractionManager fractionManager;

    public GhostsFraction(FractionManager fractionManager) {
        this.fractionManager = fractionManager;
    }

    private Random random = new Random();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void applyEffects(Player player) {
        // Постоянное уменьшение радиуса обнаружения
        // Это может быть реализовано через отдельный механизм обнаружения игроков
    }

    @Override
    public void removeEffects(Player player) {
        // Удаление эффектов не требуется, так как они временные или пассивные
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        // Получаем текущую фракцию игрока через FractionManager
        Fraction playerFraction = fractionManager.getPlayerFraction(player);

        if (!(playerFraction instanceof ShadowBrotherhoodFraction)) return;

        // Шанс уклонения
        if (random.nextDouble() < DODGE_CHANCE) {
            event.setCancelled(true);
            player.sendMessage("Вы уклонились от атаки!");
            return;
        }

        // Призрачный Шаг
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, GHOST_STEP_DURATION,
                Math.round(GHOST_STEP_SPEED * 20), false, false));
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
}