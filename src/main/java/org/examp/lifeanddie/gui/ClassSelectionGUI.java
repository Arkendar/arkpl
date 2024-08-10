package org.examp.lifeanddie.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.examp.lifeanddie.player.PlayerClass;

public class ClassSelectionGUI {

    public void openGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.RED + "Выберите класс");


        inventory.setItem(0, createClassItem(Material.FERMENTED_SPIDER_EYE, "Истребитель демонов", PlayerClass.DEMON_SLAYER));
        inventory.setItem(1, createClassItem(Material.CHORUS_FRUIT_POPPED, "Призрачный охотник", PlayerClass.PHANTOM_HUNTER));
        inventory.setItem(2, createClassItem(Material.FIREBALL, "Архимаг", PlayerClass.ARCHMAGE));
        inventory.setItem(3, createClassItem(Material.SPECTRAL_ARROW, "Молния", PlayerClass.LIGHTNING));
        inventory.setItem(4, createClassItem(Material.TOTEM, "Наследник Древних", PlayerClass.HEIR_ANCIENTS));
        inventory.setItem(5, createClassItem(Material.GOLDEN_APPLE, "Милосердие", PlayerClass.MERCY));
        inventory.setItem(6, createClassItem(Material.NETHER_STAR, "Ангел Смерти", PlayerClass.ANGEL_OF_DEATH));
        inventory.setItem(7, createClassItem(Material.DIAMOND_CHESTPLATE, "Хранитель", PlayerClass.GUARDIAN));


        player.openInventory(inventory);
    }

    private ItemStack createClassItem(Material material, String name, PlayerClass playerClass) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + name);
        item.setItemMeta(meta);
        return item;
    }
}