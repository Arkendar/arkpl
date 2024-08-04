package org.examp.lifeanddie.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.examp.lifeanddie.prefix.PrefixManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TagGUI {
    private PrefixManager prefixManager;

    public TagGUI(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
    }

    public void openTagGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "Выберите тег");

        Map<String, String> availableTags = prefixManager.getAvailableTags(player);

        int slot = 0;
        for (Map.Entry<String, String> entry : availableTags.entrySet()) {
            ItemStack item = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + "Тег: " + entry.getValue());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Нажмите, чтобы выбрать этот тег");
            lore.add(ChatColor.DARK_GRAY + "Key: " + entry.getKey()); // Добавляем ключ в лор
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
            slot++;
        }

        // Добавим кнопку для удаления тега
        ItemStack removeTag = new ItemStack(Material.BARRIER);
        ItemMeta removeMeta = removeTag.getItemMeta();
        removeMeta.setDisplayName(ChatColor.RED + "Убрать тег");
        removeTag.setItemMeta(removeMeta);
        inv.setItem(26, removeTag);

        player.openInventory(inv);
    }
}