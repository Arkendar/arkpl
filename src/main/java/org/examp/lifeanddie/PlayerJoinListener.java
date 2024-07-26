package org.examp.lifeanddie;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final PlayerClassManager playerClassManager;

    public PlayerJoinListener(PlayerClassManager playerClassManager) {
        this.playerClassManager = playerClassManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!playerClassManager.hasChosenClass(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.GREEN + "§6§lWelcome! Please choose your class using /class <warrior|mage|archer>");
        }
    }
}
