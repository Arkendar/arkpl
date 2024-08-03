package org.examp.lifeanddie.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.examp.lifeanddie.PlayerClass;
import org.examp.lifeanddie.PlayerClassManager;

public class ChooseClassCommand implements CommandExecutor {

    private final PlayerClassManager playerClassManager;

    public ChooseClassCommand(PlayerClassManager playerClassManager) {
        this.playerClassManager = playerClassManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /class <warrior|mage|archer>");
                return false;
            }

            String className = args[0].toLowerCase();
            PlayerClass playerClass = null;

            switch (className) {
                case "warrior":
                    playerClass = PlayerClass.WARRIOR;
                    break;
                case "mage":
                    playerClass = PlayerClass.MAGE;
                    break;
                case "archer":
                    playerClass = PlayerClass.ARCHER;
                    break;
                default:
                    player.sendMessage(ChatColor.RED + "Invalid class. Choose either warrior, mage, or archer.");
                    return false;
            }

            playerClassManager.setPlayerClass(player, playerClass);
            player.sendMessage(ChatColor.GREEN + "You have chosen the class: " + className);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
        return false;
    }
}
