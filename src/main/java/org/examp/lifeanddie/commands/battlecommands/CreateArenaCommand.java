package org.examp.lifeanddie.commands.battlecommands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.examp.lifeanddie.battle.ArenaManager;

public class CreateArenaCommand implements CommandExecutor {
    private ArenaManager arenaManager;

    public CreateArenaCommand(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 5) {
            player.sendMessage("Usage: /createarena <name> <x1> <y1> <z1> <x2> <y2> <z2>");
            return true;
        }

        String name = args[0];
        try {
            Location spawnPoint1 = new Location(player.getWorld(), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
            Location spawnPoint2 = new Location(player.getWorld(), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
            arenaManager.addArena(name, spawnPoint1, spawnPoint2);
            player.sendMessage("Arena " + name + " created and added to available arenas!");

        } catch (NumberFormatException e) {
            player.sendMessage("Invalid coordinates.");
        }

        return true;
    }
}
