package org.examp.lifeanddie;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.entity.Player;

public class WorldChangeListener implements Listener {
    private final LifeAndDie plugin;

    public WorldChangeListener(LifeAndDie plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (WorldManager.isAbilityRestricted(player.getWorld())) {
            plugin.resetPlayerCooldowns(player);
            player.sendMessage("Ваши способности были сброшены при входе в этот мир.");
        }
    }
}