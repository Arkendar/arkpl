package org.examp.lifeanddie;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.examp.lifeanddie.player.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownDisplayManager {
    private final LifeAndDie plugin;
    private final PlayerData playerData;
    private final Map<UUID, Map<String, Integer>> cooldownTasks = new HashMap<>();
    private final Map<UUID, Map<String, ItemStack>> originalItems = new HashMap<>();

    public CooldownDisplayManager(LifeAndDie plugin, PlayerData playerData) {
        this.plugin = plugin;
        this.playerData = playerData;
    }

    public void startCooldownDisplay(Player player, String ability, int slot) {
        UUID playerId = player.getUniqueId();
        long cooldownTime = playerData.getCooldownTime(ability) / 1000; // Преобразуем в секунды

        // Сохраняем оригинальный предмет
        ItemStack originalItem = player.getInventory().getItem(slot);
        originalItems.computeIfAbsent(playerId, k -> new HashMap<>()).put(ability, originalItem);

        BukkitRunnable runnable = new BukkitRunnable() {
            int timeLeft = (int) cooldownTime;

            @Override
            public void run() {
                if (timeLeft <= 0 || !player.isOnline()) {
                    // Возвращаем оригинальный предмет
                    if (player.isOnline()) {
                        player.getInventory().setItem(slot, originalItems.get(playerId).get(ability));
                    }
                    originalItems.get(playerId).remove(ability);
                    cancel();
                    cooldownTasks.get(playerId).remove(ability);
                } else {
                    ItemStack cooldownItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7); // Серое стекло
                    ItemMeta meta = cooldownItem.getItemMeta();
                    meta.setDisplayName("§c§l" + ability + " - " + timeLeft + "s");
                    cooldownItem.setItemMeta(meta);
                    player.getInventory().setItem(slot, cooldownItem);
                    timeLeft--;
                }
            }
        };

        runnable.runTaskTimer(plugin, 0L, 20L);
        cooldownTasks.computeIfAbsent(playerId, k -> new HashMap<>()).put(ability, runnable.getTaskId());
    }

    public boolean isCooldownItem(ItemStack item) {
        return item != null && item.getType() == Material.STAINED_GLASS_PANE && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().startsWith("§c");
    }

    public void resetCooldowns(Player player) {
        if (player == null) {
            Bukkit.getLogger().warning("Attempted to reset cooldowns for a null player");
            return;
        }
        UUID playerId = player.getUniqueId();
        if (cooldownTasks.containsKey(playerId)) {
            cooldownTasks.get(playerId).values().forEach(taskId -> {
                plugin.getServer().getScheduler().cancelTask(taskId);
            });
            cooldownTasks.remove(playerId);
        }
        if (originalItems.containsKey(playerId)) {
            originalItems.get(playerId).forEach((ability, item) -> {
                int slot = player.getInventory().first(Material.STAINED_GLASS_PANE);
                if (slot != -1) {
                    player.getInventory().setItem(slot, item);
                } else {
                    // Если слот не найден, найдем первый пустой слот
                    slot = player.getInventory().firstEmpty();
                    if (slot != -1) {
                        player.getInventory().setItem(slot, item);
                    }
                }
            });
            originalItems.remove(playerId);
        }
        // Отменяем все выполняющиеся BukkitRunnable для этого игрока
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }

}