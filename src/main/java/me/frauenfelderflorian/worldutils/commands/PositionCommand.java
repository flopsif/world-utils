package me.frauenfelderflorian.worldutils.commands;

import me.frauenfelderflorian.worldutils.Settings;
import me.frauenfelderflorian.worldutils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CommandExecutor and TabCompleter for command position
 */
public record PositionCommand(WorldUtils plugin) implements CommandExecutor, TabCompleter {
    private static final List<String> SOLO_COMMANDS = new ArrayList<>(List.of("list", "clear"));
    private static final List<String> NAME_COMMANDS = new ArrayList<>(List.of("tp", "del"));

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
        switch (args.length) {
            case 1 -> {
                //command or position name entered
                switch (args[0]) {
                    case "list" -> {
                        //send all position info
                        for (String pos : WorldUtils.positions.getKeys(false))
                            sender.sendMessage(positionMessage(pos, (Location) WorldUtils.positions.get(pos)));
                        return true;
                    }
                    case "clear" -> {
                        //remove all positions
                        plugin.getLogger().info("Cleared positions");
                        //Bukkit.getLogger().info("Cleared positions"); //does this work the same?
                        Bukkit.broadcastMessage("Cleared positions");
                        for (String pos : WorldUtils.positions.getKeys(false)) WorldUtils.positions.remove(pos);
                        return true;
                    }
                    default -> {
                        //position name entered
                        if (WorldUtils.positions.contains(args[0]))
                            //existing position, send info
                            if (WorldUtils.positions.contains(args[0] + ".author"))
                                sender.sendMessage(positionMessage(
                                        args[0], (String) WorldUtils.positions.get(args[0] + ".author"),
                                        (Location) WorldUtils.positions.get(args[0])));
                            else sender.sendMessage(positionMessage(args[0], (Location) WorldUtils.positions.get(args[0])));
                        else if (sender instanceof Player) {
                            //new position name, save position
                            WorldUtils.positions.set(args[0], ((Player) sender).getLocation());
                            if ((Boolean) WorldUtils.config.get(Settings.POSITION.getKey("saveAuthor")))
                                WorldUtils.positions.set(args[0] + ".author", sender.getName());
                            Bukkit.broadcastMessage("Added position "
                                    + positionMessage(args[0], (Location) WorldUtils.positions.get(args[0])));
                        } else WorldUtils.notConsole(sender);
                        return true;
                    }
                }
            }
            case 2 -> {
                //command and position entered
                switch (args[0]) {
                    case "tp" -> {
                        //teleport player to position if OP
                        if (sender instanceof Player && sender.isOp())
                            ((Player) sender).teleport((Location) WorldUtils.positions.get(args[1]));
                        else if (sender instanceof Player) WorldUtils.notAllowed(sender);
                        else WorldUtils.notConsole(sender);
                        return true;
                    }
                    case "del" -> {
                        //delete position
                        Bukkit.broadcastMessage("Deleted position "
                                + positionMessage(args[1], (Location) WorldUtils.positions.get(args[1])));
                        WorldUtils.positions.remove(args[1]);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get a formatted message with position information
     *
     * @param name     name  of the position
     * @param author   who saved the position
     * @param location location of the position
     * @return String with formatted position
     */
    public static String positionMessage(String name, String author, Location location) {
        return name + " from " + author + " (" + Objects.requireNonNull(location.getWorld()).getName() + "): "
                + location.getBlockX() + "  " + location.getBlockY() + "  " + location.getBlockZ();
    }

    /**
     * Get a formatted message with position information
     *
     * @param name     name of the position
     * @param location location of the position
     * @return String with formatted position
     */
    public static String positionMessage(String name, Location location) {
        return name + " (" + Objects.requireNonNull(location.getWorld()).getName() + "): "
                + location.getBlockX() + "  " + location.getBlockY() + "  " + location.getBlockZ();
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
        switch (args.length) {
            case 1 -> {
                //command or position name being entered
                StringUtil.copyPartialMatches(args[0], SOLO_COMMANDS, completions);
                StringUtil.copyPartialMatches(args[0], NAME_COMMANDS, completions);
                StringUtil.copyPartialMatches(args[0], WorldUtils.positions.getKeys(false), completions);
            }
            case 2 -> {
                //position name being entered
                for (String cmd : NAME_COMMANDS)
                    if (args[0].equals(cmd))
                        StringUtil.copyPartialMatches(args[1], WorldUtils.positions.getKeys(false), completions);
            }
        }
        return completions;
    }
}
