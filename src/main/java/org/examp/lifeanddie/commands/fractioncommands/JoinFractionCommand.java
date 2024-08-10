package org.examp.lifeanddie.commands.fractioncommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.examp.lifeanddie.gui.FractionSelectionGUI;

public class JoinFractionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда может быть использована только игроком.");
            return true;
        }

        Player player = (Player) sender;
        FractionSelectionGUI.openGUI(player);

        return true;
    }
}