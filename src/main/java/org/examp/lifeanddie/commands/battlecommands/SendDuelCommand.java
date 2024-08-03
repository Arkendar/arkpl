package org.examp.lifeanddie.commands.battlecommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.examp.lifeanddie.battle.DuelManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SendDuelCommand implements CommandExecutor {
    private final DuelManager duelManager;
    private final Map<UUID, UUID> pendingDuels = new HashMap<>();

    public SendDuelCommand(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда может быть использована только игроками.");
            return true;
        }

        Player challenger = (Player) sender;

        if (args.length != 1) {
            challenger.sendMessage("Использование: /duel <имя_игрока>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            challenger.sendMessage("Игрок не найден или не в сети.");
            return true;
        }

        if (target.equals(challenger)) {
            challenger.sendMessage("Вы не можете вызвать на дуэль самого себя.");
            return true;
        }

        pendingDuels.put(target.getUniqueId(), challenger.getUniqueId());

        challenger.sendMessage("Вы отправили приглашение на дуэль игроку " + target.getName());

        TextComponent message = new TextComponent("У вас новое приглашение на дуэль от " + challenger.getName() + "! ");
        message.setColor(ChatColor.GOLD);

        TextComponent accept = new TextComponent("[Принять]");
        accept.setColor(ChatColor.GREEN);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duelaccept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Нажмите, чтобы принять дуэль").create()));

        TextComponent decline = new TextComponent("[Отклонить]");
        decline.setColor(ChatColor.RED);
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dueldecline"));
        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Нажмите, чтобы отклонить дуэль").create()));

        message.addExtra(accept);
        message.addExtra(" ");
        message.addExtra(decline);

        target.spigot().sendMessage(message);

        return true;
    }

    // Метод для обработки отклонения дуэли
    public void declineDuel(Player decliner) {
        UUID challengerUUID = pendingDuels.remove(decliner.getUniqueId());
        if (challengerUUID != null) {
            Player challenger = Bukkit.getPlayer(challengerUUID);
            if (challenger != null && challenger.isOnline()) {
                challenger.sendMessage(decliner.getName() + " отклонил ваше приглашение на дуэль.");
            }
            decliner.sendMessage("Вы отклонили приглашение на дуэль.");
        } else {
            decliner.sendMessage("У вас нет активных приглашений на дуэль.");
        }
    }

    public Player getPendingChallenger(Player acceptor) {
        UUID challengerUUID = pendingDuels.get(acceptor.getUniqueId());
        return challengerUUID != null ? Bukkit.getPlayer(challengerUUID) : null;
    }

    public void removePendingDuel(Player player) {
        pendingDuels.remove(player.getUniqueId());
    }
}
