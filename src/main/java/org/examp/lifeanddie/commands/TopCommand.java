package org.examp.lifeanddie.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.examp.lifeanddie.battle.BattleStatistics;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TopCommand implements CommandExecutor {

    private final BattleStatistics battleStatistics;

    public TopCommand(BattleStatistics battleStatistics) {
        this.battleStatistics = battleStatistics;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int limit = 10; // По умолчанию показываем топ-10
        if (args.length > 0) {
            try {
                limit = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Неверный формат числа. Используйте целое число.");
                return true;
            }
        }

        List<Map.Entry<UUID, BattleStatistics.PlayerStats>> topPlayers = battleStatistics.getTopPlayers(limit);

        sender.sendMessage(ChatColor.GOLD + "Топ " + String.valueOf(limit) + " игроков по рейтингу:");
        for (int i = 0; i < topPlayers.size(); i++) {
            Map.Entry<UUID, BattleStatistics.PlayerStats> entry = topPlayers.get(i);
            String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            int rating = entry.getValue().getRating();
            sender.sendMessage(ChatColor.YELLOW + String.valueOf(i + 1) + ". " +
                    ChatColor.WHITE + playerName + " - " +
                    ChatColor.GREEN + String.valueOf(rating));
        }

        return true;
    }
}