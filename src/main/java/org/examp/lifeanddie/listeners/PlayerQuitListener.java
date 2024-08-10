package org.examp.lifeanddie.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.examp.lifeanddie.fraction.FractionManager;
import org.examp.lifeanddie.fraction.Fraction;

public class PlayerQuitListener implements Listener {
    private final FractionManager fractionManager;

    public PlayerQuitListener(FractionManager fractionManager) {
        this.fractionManager = fractionManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Получаем фракцию игрока
        Fraction playerFraction = fractionManager.getPlayerFraction(player);

        if (playerFraction != null) {
            // Удаляем фракцию игрока
            fractionManager.removePlayerFraction(player);
        }
    }
}
