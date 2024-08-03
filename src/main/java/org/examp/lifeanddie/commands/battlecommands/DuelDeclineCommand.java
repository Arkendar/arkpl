package org.examp.lifeanddie.commands.battlecommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelDeclineCommand implements CommandExecutor {

    private final SendDuelCommand sendDuelCommand;

    public DuelDeclineCommand(SendDuelCommand sendDuelCommand) {
        this.sendDuelCommand = sendDuelCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            sendDuelCommand.declineDuel((Player) sender);
        } else {
            sender.sendMessage("Эта команда может быть использована только игроками.");
        }
        return true;
    }
}