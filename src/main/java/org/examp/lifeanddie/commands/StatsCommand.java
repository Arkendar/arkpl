package org.examp.lifeanddie.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.examp.lifeanddie.battle.BattleStatistics;

public class StatsCommand implements CommandExecutor {

    private final BattleStatistics battleStatistics;

    public StatsCommand(BattleStatistics battleStatistics) {
        this.battleStatistics = battleStatistics;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player targetPlayer;

        if (args.length == 0) {
            // Если аргументов нет, показываем статистику отправителя команды
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Эта команда может быть использована только игроками.");
                return true;
            }
            targetPlayer = (Player) sender;
        } else {
            // Если есть аргумент, ищем игрока по имени
            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Игрок не найден.");
                return true;
            }
        }

        BattleStatistics.PlayerStats stats = battleStatistics.getPlayerStats(targetPlayer);

        sender.sendMessage(ChatColor.GOLD + "Статистика игрока " + targetPlayer.getName() + ":");
        sender.sendMessage(ChatColor.YELLOW + "Рейтинг: " + ChatColor.WHITE + stats.getRating());
        sender.sendMessage(ChatColor.YELLOW + "Победы: " + ChatColor.WHITE + stats.getWins());
        sender.sendMessage(ChatColor.YELLOW + "Поражения: " + ChatColor.WHITE + stats.getLosses());
        sender.sendMessage(ChatColor.YELLOW + "Ничьи: " + ChatColor.WHITE + stats.getDraws());
        sender.sendMessage(ChatColor.YELLOW + "Убийства: " + ChatColor.WHITE + stats.getKills());
        sender.sendMessage(ChatColor.YELLOW + "Смерти: " + ChatColor.WHITE + stats.getDeaths());
        sender.sendMessage(ChatColor.YELLOW + "K/D: " + ChatColor.WHITE + String.format("%.2f", stats.getKDRatio()));
        sender.sendMessage(ChatColor.YELLOW + "Процент побед: " + ChatColor.WHITE + String.format("%.2f%%", stats.getWinRate() * 100));

        return true;
    }
}