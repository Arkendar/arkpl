package org.examp.lifeanddie.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.examp.lifeanddie.CooldownDisplayManager;

public class CooldownItemListener implements Listener {
    private final CooldownDisplayManager cooldownDisplayManager;

    public CooldownItemListener(CooldownDisplayManager cooldownDisplayManager) {
        this.cooldownDisplayManager = cooldownDisplayManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (cooldownDisplayManager.isCooldownItem(clickedItem)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        // Отменяем событие выбрасывания для всех предметов
        event.setCancelled(true);
    }
}