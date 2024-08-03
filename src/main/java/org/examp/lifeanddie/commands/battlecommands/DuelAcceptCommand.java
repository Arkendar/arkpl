package org.examp.lifeanddie.commands.battlecommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.examp.lifeanddie.battle.DuelManager;

public class DuelAcceptCommand implements CommandExecutor {
    private final DuelManager duelManager;
    private final SendDuelCommand sendDuelCommand;

    public DuelAcceptCommand(DuelManager duelManager, SendDuelCommand sendDuelCommand) {
        this.duelManager = duelManager;
        this.sendDuelCommand = sendDuelCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player acceptor = (Player) sender;
            Player challenger = sendDuelCommand.getPendingChallenger(acceptor);
            if (challenger != null) {
                boolean success = duelManager.createDuel(challenger, acceptor);
                if (success) {
                    acceptor.sendMessage("Вы приняли вызов на дуэль от " + challenger.getName() + "!");
                    challenger.sendMessage(acceptor.getName() + " принял ваш вызов на дуэль!");
                } else {
                    acceptor.sendMessage("Не удалось начать дуэль. Пожалуйста, попробуйте позже.");
                    challenger.sendMessage("Не удалось начать дуэль с " + acceptor.getName() + ". Пожалуйста, попробуйте позже.");
                }
                sendDuelCommand.removePendingDuel(acceptor);
            } else {
                acceptor.sendMessage("У вас нет активных приглашений на дуэль.");
            }
            return true;
        }
        return false;
    }
}