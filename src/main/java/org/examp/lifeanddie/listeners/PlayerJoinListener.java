package org.examp.lifeanddie.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.examp.lifeanddie.PlayerClassManager;
import org.examp.lifeanddie.prefix.PrefixManager;

public class PlayerJoinListener implements Listener {

    private final PlayerClassManager playerClassManager;
    private final PrefixManager prefixManager;

    public PlayerJoinListener(PlayerClassManager playerClassManager, PrefixManager prefixManager) {
        this.playerClassManager = playerClassManager;
        this.prefixManager = prefixManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            prefixManager.setPlayerTag(player, "default");
        } else {
            String tagKey = prefixManager.getPlayerTagKey(player.getUniqueId());
            if (tagKey != null && prefixManager.hasAccessToTag(player, tagKey)) {
                prefixManager.setPlayerTag(player, tagKey);
            } else {
                prefixManager.setPlayerTag(player, "default");
            }
        }
        if (!playerClassManager.hasChosenClass(player)) {
            player.sendMessage(ChatColor.GREEN + "§6§lПривет! Выбери класс с помощью /class <warrior|mage|archer>");
        }

        prefixManager.updatePlayerName(player);
    }
}