package org.examp.lifeanddie;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SkillInventoryManager implements Listener {

    private final PlayerClassManager playerClassManager;

    public SkillInventoryManager(PlayerClassManager playerClassManager) {
        this.playerClassManager = playerClassManager;
    }

    public void openSkillInventory(Player player) {
        Inventory skillInventory = Bukkit.createInventory(null, 27, "Choose Your Skills");

        skillInventory.setItem(9, playerClassManager.createEarthJaws());
        skillInventory.setItem(10, playerClassManager.createDashSkill());
        skillInventory.setItem(11, playerClassManager.createFortressSkill());
        skillInventory.setItem(12, playerClassManager.createFireStrikeSkill());
        skillInventory.setItem(13, playerClassManager.createFlightSkill());
        skillInventory.setItem(14, playerClassManager.createDeathSkill());
        skillInventory.setItem(15, playerClassManager.createEruptionSkill());
        skillInventory.setItem(16, playerClassManager.createPhaseArrow());
        skillInventory.setItem(17, playerClassManager.createAstralSphere());
        skillInventory.setItem(18, playerClassManager.createLightningStorm());
        skillInventory.setItem(19, playerClassManager.createIceWave());
        skillInventory.setItem(20, playerClassManager.createChaosBearer());
        skillInventory.setItem(21, playerClassManager.createBurial());
        skillInventory.setItem(22, playerClassManager.createLightHeaven());
        skillInventory.setItem(23, playerClassManager.createIce());
        skillInventory.setItem(24, playerClassManager.createDashTeleport());
        skillInventory.setItem(25, playerClassManager.createSkyfall());
        skillInventory.setItem(26, playerClassManager.createWrathStorm());

        player.openInventory(skillInventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Choose Your Skills")) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                ItemStack item = event.getCurrentItem();
                if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    String skillName = item.getItemMeta().getDisplayName();

                    // Проверяем, есть ли уже предмет с таким же именем в инвентаре игрока
                    boolean hasSkill = false;
                    for (ItemStack invItem : player.getInventory().getContents()) {
                        if (invItem != null && invItem.hasItemMeta() && invItem.getItemMeta().hasDisplayName()) {
                            if (invItem.getItemMeta().getDisplayName().equals(skillName)) {
                                hasSkill = true;
                                break;
                            }
                        }
                    }

                    if (hasSkill) {
                        player.sendMessage("You already have this skill in your inventory!");
                    } else {
                        player.sendMessage("You have chosen the skill: " + skillName);
                        player.getInventory().addItem(item.clone());
                    }
                }
            }
        }
    }
}