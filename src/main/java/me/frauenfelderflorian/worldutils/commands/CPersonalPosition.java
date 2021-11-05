package me.frauenfelderflorian.worldutils.commands;

import me.frauenfelderflorian.worldutils.WorldUtils;
import me.frauenfelderflorian.worldutils.config.Positions;
import me.frauenfelderflorian.worldutils.config.Prefs;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * CommandExecutor and TabCompleter for command personalposition
 */
public record CPersonalPosition(WorldUtils plugin, Positions positions) implements TabExecutor {
    public static final String CMD = "personalposition";

    /**
     * Done when command sent
     *
     * @param sender  sender of the command
     * @param command sent command
     * @param alias   used alias
     * @param args    used arguments
     * @return true if correct command syntax used and no errors, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            switch (args.length) {
                case 1 -> {
                    //command or position name entered
                    switch (args[0]) {
                        case "list" -> {
                            //send all position info
                            for (String pos : positions.getPositions((Player) sender))
                                sender.sendMessage(WorldUtils.Messages.positionMessage(
                                        pos, positions.getPersonalLocation((Player) sender, pos)));
                            return true;
                        }
                        case "clear" -> {
                            //remove all positions
                            sender.sendMessage("§e§oCleared personal positions");
                            positions.remove(((Player) sender).getUniqueId().toString());
                            return true;
                        }
                        default -> {
                            //position name entered
                            if (positions.containsPersonal((Player) sender, args[0]))
                                //existing position, send info
                                sender.sendMessage(WorldUtils.Messages.positionMessage(args[0],
                                        positions.getPersonalLocation((Player) sender, args[0])));
                            else {
                                //new position name, save position
                                positions.setPersonal((Player) sender, args[0]);
                                sender.sendMessage("§aAdded§r personal position " + WorldUtils.Messages.positionMessage(
                                        args[0], positions.getPersonalLocation((Player) sender, args[0])));
                            }
                            return true;
                        }
                    }
                }
                case 2 -> {
                    //command and position entered
                    switch (args[0]) {
                        case "tp" -> {
                            //teleport player to position if OP
                            if (sender.isOp())
                                ((Player) sender).teleport(positions.getPersonalLocation((Player) sender, args[1]));
                            else WorldUtils.Messages.notAllowed(sender);
                            return true;
                        }
                        case "del" -> {
                            //delete position
                            sender.sendMessage("§cDeleted§r personal position " + WorldUtils.Messages.positionMessage(
                                    args[1], positions.getPersonalLocation((Player) sender, args[1])));
                            positions.remove((Player) sender, args[1]);
                            return true;
                        }
                        default -> {
                            return otherPlayersPosition(sender, args);
                        }
                    }
                }
            }
        } else {
            if (args.length == 2) return otherPlayersPosition(sender, args);
            WorldUtils.Messages.notConsole(sender);
            return true;
        }
        return false;
    }

    /**
     * Done while entering command
     *
     * @param sender  sender of the command
     * @param command sent command
     * @param alias   used alias
     * @param args    used arguments
     * @return List of Strings for tab completion
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (sender instanceof Player)
            switch (args.length) {
                case 1 -> {
                    //command or position name being entered
                    StringUtil.copyPartialMatches(args[0], List.of("list", "clear", "del"), completions);
                    if (sender.isOp()) StringUtil.copyPartialMatches(args[0], List.of("tp"), completions);
                    StringUtil.copyPartialMatches(args[0], positions.getPositions((Player) sender), completions);
                }
                case 2 -> {
                    //position name being entered
                    if (args[0].equals("del") || sender.isOp() && args[0].equals("tp"))
                        StringUtil.copyPartialMatches(args[1], positions.getPositions((Player) sender), completions);
                }
            }
        return completions;
    }

    /**
     * Get another player's private position if possible
     *
     * @param sender sender of the commands
     * @param args   used arguments
     * @return true if correct command syntax used and no errors, false otherwise
     */
    private boolean otherPlayersPosition(CommandSender sender, String[] args) {
        if (plugin.prefs.getBoolean(Prefs.Option.PERSONALPOSITION_ACCESS_GLOBAL)) {
            Player other = Bukkit.getPlayer(args[0]);
            if (other != null && other.isOnline()) {
                //get personalposition from player
                try {
                    sender.sendMessage("Personal position from player " + args[0] + ": "
                            + WorldUtils.Messages.positionMessage(args[1], positions.getPersonalLocation(other, args[1])));
                } catch (NullPointerException e) {
                    WorldUtils.Messages.positionNotFound(sender);
                }
            } else {
                WorldUtils.Messages.playerNotFound(sender);
            }
            return true;
        }
        return false;
    }
}
