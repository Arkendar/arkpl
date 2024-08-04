package org.examp.lifeanddie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.examp.lifeanddie.gui.TagGUI;

public class TagCommand implements CommandExecutor {
    private TagGUI tagGUI;

    public TagCommand(TagGUI tagGUI) {
        this.tagGUI = tagGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда может быть использована только игроками.");
            return true;
        }

        Player player = (Player) sender;
        tagGUI.openTagGUI(player);
        return true;
    }
}