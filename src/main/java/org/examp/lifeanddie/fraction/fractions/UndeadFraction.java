package org.examp.lifeanddie.fraction.fractions;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.examp.lifeanddie.fraction.Fraction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.examp.lifeanddie.fraction.FractionManager;

import java.util.Random;

public class UndeadFraction implements Fraction, Listener {
    private static final String NAME = "Нежить";
    private static final double ABSORPTION_CHANCE = 0.10; // 10% шанс получить поглощение
    private static final double LAST_BREATH_CHANCE = 0.15; // 15% шанс выжить с 1 HP
    private final FractionManager fractionManager;

    private Random random = new Random();

    public UndeadFraction(FractionManager fractionManager) {
        this.fractionManager = fractionManager;
    }

    @Override
    public String getName() {
        return NAME;}

    @Override
    public void applyEffects(Player player) {
    }

    @Override
    public void removeEffects(Player player) {
        // Удаление эффектов не требуется
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        Fraction playerFraction = fractionManager.getPlayerFraction(player);

        if (!(playerFraction instanceof UndeadFraction)) return;

        // Шанс получить поглощение
        if (random.nextDouble() < ABSORPTION_CHANCE) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 0)); // 10 секунд поглощения
            player.sendMessage("Вы получили эффект поглощения!");
        }

        // Последний Вздох
        if (player.getHealth() - event.getFinalDamage() <= 0 && random.nextDouble() < LAST_BREATH_CHANCE) {
            event.setDamage(player.getHealth() - 1);
            player.sendMessage("Вы избежали смерти благодаря Последнему Вздоху!");
        }
    }

    // Метод для подсвечивания игроков с низким здоровьем
    public void highlightLowHealthPlayers(Player player) {
        for (Player target : player.getWorld().getPlayers()) {
            if (target != player && target.getHealth() <= 6) { // 3 сердца или меньше
                player.spigot().playEffect(target.getLocation().add(0, 1, 0), Effect.HEART, 0, 0, 0.5f, 0.5f, 0.5f, 0, 10, 16);
            }
        }
    }

}