package org.examp.lifeanddie.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.examp.lifeanddie.player.PlayerClassManager;
import org.examp.lifeanddie.player.PlayerClass;

public class ClassInventoryClickListener implements Listener {

    private final PlayerClassManager playerClassManager;

    public ClassInventoryClickListener(PlayerClassManager playerClassManager) {
        this.playerClassManager = playerClassManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedInventory == null || clickedItem == null) {
            return;
        }

        if (event.getView().getTitle().equals(ChatColor.RED + "Выберите класс")) {
            event.setCancelled(true);

            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String className = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

                PlayerClass playerClass = null;
                String message = "";

                switch (className) {
                    case "Истребитель демонов":
                        playerClass = PlayerClass.DEMON_SLAYER;
                        message = "Вы выбрали класс: Истребитель демонов";
                        break;
                    case "Призрачный охотник":
                        playerClass = PlayerClass.PHANTOM_HUNTER;
                        message = "Вы выбрали класс: Призрачный охотник";
                        break;
                    case "Архимаг":
                        playerClass = PlayerClass.ARCHMAGE;
                        message = "Вы выбрали класс: Архимаг";
                        break;
                    case "Молния":
                        playerClass = PlayerClass.LIGHTNING;
                        message = "Вы выбрали класс: Молния";
                        break;
                    case "Наследник Древних":
                        playerClass = PlayerClass.HEIR_ANCIENTS;
                        message = "Вы выбрали класс: Наследник Древних";
                        break;
                    case "Милосердие":
                        playerClass = PlayerClass.MERCY;
                        message = "Вы выбрали класс: Милосердие";
                        break;
                    case "Ангел Смерти":
                        playerClass = PlayerClass.ANGEL_OF_DEATH;
                        message = "Вы выбрали класс: Ангел Смерти";
                        break;
                    case "Хранитель":
                        playerClass = playerClass.GUARDIAN;
                        message = "Вы выбрали класс: Хранитель";
                        break;
                    //другие классы по необходимости
                    default:
                        player.sendMessage(ChatColor.RED + "Неизвестный класс!");
                        break;
                }

                if (playerClass != null) {
                    playerClassManager.setPlayerClass(player, playerClass);
                    player.sendMessage(ChatColor.GREEN + message);
                }

                player.closeInventory();
            }
        }
    }
}