package org.examp.lifeanddie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.examp.lifeanddie.SkillInventoryManager;

public class ChooseSkillCommand implements CommandExecutor {

    private final SkillInventoryManager skillInventoryManager;

    public ChooseSkillCommand(SkillInventoryManager skillInventoryManager) {
        this.skillInventoryManager = skillInventoryManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            skillInventoryManager.openSkillInventory(player);
            return true;
        }
        return false;
    }
}
