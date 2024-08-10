package org.examp.lifeanddie.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.examp.lifeanddie.fraction.Fraction;
import org.examp.lifeanddie.fraction.FractionManager;
import org.examp.lifeanddie.prefix.PrefixManager;
import org.examp.lifeanddie.LifeAndDie; // Убедитесь, что импортировали ваш основной класс плагина

public class PlayerJoinListener implements Listener {

    private final PrefixManager prefixManager;
    private final LifeAndDie plugin; // Добавьте ссылку на ваш основной класс плагина
    private final FractionManager fractionManager;



    public PlayerJoinListener(LifeAndDie plugin, FractionManager fractionManager, PrefixManager prefixManager) {
        this.plugin = plugin;
        this.prefixManager = prefixManager;
        this.fractionManager = fractionManager;

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Существующая логика
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
            player.sendMessage(ChatColor.RED + "    Добро пожаловать!");
            player.sendMessage(" ❱ Выбери свой класс • §a/class");
            player.sendMessage(" ❱ Выбери фракцию • §a/joinfraction");
            player.sendMessage(" ❱ Вызови противника на дуэль • §a/duel");
            player.sendMessage(" ❱ Очисти свой инвентарь • §a/reset");
            player.sendMessage(" ❱ Посмотри свою или чужую статистику • §a/stats");
            player.sendMessage(" ❱ Получи титул и поставь его • §a/tag");
            player.sendMessage(" ❱ Посмотри лучших игроков и обыграй их • §a/top");
            player.sendMessage(ChatColor.RED + "    Удачи в сражениях!");

        prefixManager.updatePlayerName(player);

        // Добавление эффекта насыщения
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
                }
            }
        }.runTask(plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                teleportToWorldSpawn(player);
            }
        }.runTaskLater(plugin, 10);

        fractionManager.applyFractionEffects(player);
    }
    private void teleportToWorldSpawn(Player player) {
        World world = plugin.getServer().getWorld("world");
        if (world != null) {
            Location spawnLocation = world.getSpawnLocation();
            player.teleport(spawnLocation);
        } else {
            plugin.getLogger().warning("Мир 'world' не найден!");
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                teleportToWorldSpawn(player);
            }
        }.runTaskLater(plugin, 1); // 1 тик задержки, чтобы убедиться, что возрождение завершено
    }
}
