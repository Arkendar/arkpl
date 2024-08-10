package org.examp.lifeanddie.listeners.fraction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.examp.lifeanddie.fraction.Fraction;
import org.examp.lifeanddie.fraction.FractionManager;

public class FractionSelectionListener implements Listener {
    private final FractionManager fractionManager;

    public FractionSelectionListener(FractionManager fractionManager) {
        this.fractionManager = fractionManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Выбор фракции")) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        String fractionName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        Fraction selectedFraction = fractionManager.getAllFractions().get(fractionName);

        if (selectedFraction != null) {
            fractionManager.setPlayerFraction(player, selectedFraction);
            player.sendMessage(ChatColor.GREEN + "Вы присоединились к фракции " + fractionName + "!");
        } else {
            player.sendMessage(ChatColor.RED + "Произошла ошибка при выборе фракции.");
        }

        player.closeInventory();
    }
}