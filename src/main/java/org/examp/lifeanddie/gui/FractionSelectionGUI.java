package org.examp.lifeanddie.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class FractionSelectionGUI {
    private static final String GUI_TITLE = ChatColor.DARK_PURPLE + "Выбор фракции";

    public static void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, GUI_TITLE);

        gui.setItem(1, createFractionItem(Material.SHIELD, "Стражи крепости",
                "§7Увеличенное здоровье (4 сердца)",
                "§7Устойчивость к отбрасыванию (30%)",
                "§7Сопротивление урону (10%)"));

        gui.setItem(3, createFractionItem(Material.GHAST_TEAR, "Призраки",
                "§7Шанс уклонения от атак (15%)",
                "§7Призрачный Шаг при получении урона (увеличивает скорость передвижения)",
                "§7Уменьшенный радиус обнаружения (невидимость, если игрок на расстоянии 20 блоков)"));

        gui.setItem(5, createFractionItem(Material.ROTTEN_FLESH, "Нежить",
                "§7Шанс получить эффект Поглощения (10%)",
                "§7Последний Вздох при смертельном уроне (15%)",
                "§7Обозначение врагов с низким здоровьем"));

        gui.setItem(7, createFractionItem(Material.ENDER_PEARL, "Братство Теней",
                "§7Накладывает отравление при получении урона (20%)",
                "§7Предчувствие Опасности (увеличивает скорость передвижения вблизи противника)",
                "§7Шанс телепортации при атаке (10%)", "§7Увеличенная скорость передвижения (15%)", "§7Уменьшенное здоровье (всего 8 сердец)"));

        player.openInventory(gui);
    }

    private static ItemStack createFractionItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}