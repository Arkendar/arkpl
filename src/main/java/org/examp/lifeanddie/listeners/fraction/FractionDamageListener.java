package org.examp.lifeanddie.listeners.fraction;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.fraction.Fraction;
import org.examp.lifeanddie.fraction.fractions.FortressGuardiansFraction;

public class FractionDamageListener implements Listener {
    private final LifeAndDie plugin;

    public FractionDamageListener(LifeAndDie plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Fraction fraction = plugin.getFractionManager().getPlayerFraction(player);

        if (fraction instanceof FortressGuardiansFraction) {
            // Уменьшаем урон на 10% для Стражей крепости
            double newDamage = event.getDamage() * 0.9;
            event.setDamage(newDamage);
        }
    }
}