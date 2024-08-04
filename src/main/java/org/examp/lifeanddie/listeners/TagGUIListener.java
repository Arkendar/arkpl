package org.examp.lifeanddie.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.examp.lifeanddie.prefix.PrefixManager;

import java.util.List;
import java.util.Map;

public class TagGUIListener implements Listener {
    private PrefixManager prefixManager;

    public TagGUIListener(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Выберите тег")) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        String itemName = clickedItem.getItemMeta().getDisplayName();

        if (itemName.startsWith(ChatColor.YELLOW + "Тег: ")) {
            List<String> lore = clickedItem.getItemMeta().getLore();
            if (lore != null && lore.size() > 1) {
                String tagKey = ChatColor.stripColor(lore.get(1)).substring(5);

                if (prefixManager.getAvailableTags(player).containsKey(tagKey)) {
                    prefixManager.setPlayerTag(player, tagKey);
                    player.sendMessage(ChatColor.GREEN + "Вы выбрали тег: " + prefixManager.getTag(tagKey));
                    player.closeInventory();
                } else {
                    player.sendMessage(ChatColor.RED + "Ошибка: Тег не найден в доступных тегах.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Ошибка: Неверный формат тега.");
            }
        } else if (itemName.equals(ChatColor.RED + "Убрать тег")) {
            prefixManager.removePlayerTag(player);
            player.sendMessage(ChatColor.GREEN + "Ваш тег был удален.");
            player.closeInventory();
        } else {
            player.sendMessage(ChatColor.RED + "Ошибка: Неизвестное действие.");
        }
    }
}