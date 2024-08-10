package org.examp.lifeanddie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.examp.lifeanddie.gui.ClassSelectionGUI;

public class ChooseClassCommand implements CommandExecutor {
    private final ClassSelectionGUI classSelectionGUI;

    public ChooseClassCommand(ClassSelectionGUI classSelectionGUI) {
        this.classSelectionGUI = classSelectionGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            classSelectionGUI.openGUI(player);
            return true;
        }
        return false;
    }
}