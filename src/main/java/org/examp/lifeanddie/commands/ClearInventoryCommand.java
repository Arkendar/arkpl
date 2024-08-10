package org.examp.lifeanddie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearInventoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда может быть использована только игроком!");
            return true;
        }

        Player player = (Player) sender;
        player.getInventory().clear();
        player.sendMessage("Ваш инвентарь был очищен!");

        return true;
    }
}