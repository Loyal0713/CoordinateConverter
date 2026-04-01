package com.loyal0713.coordinateconverter;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CoordConvCommand implements CommandExecutor, TabCompleter {

    private static final String PREFIX = ChatColor.GOLD + "[CoordConv] " + ChatColor.RESET;
    private static final String USAGE = PREFIX + ChatColor.RED + "Usage: /coordconv [<x> <z> [overworld|nether]]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return handleAutoConvert(sender);
        }
        if (args.length >= 2) {
            return handleManualConvert(sender, args);
        }
        sender.sendMessage(USAGE);
        return true;
    }

    /**
     * No-arg mode: uses the player's current location and dimension.
     * Overworld → show nether equivalent; Nether → show overworld equivalent; End → show as-is.
     */
    private boolean handleAutoConvert(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Console must provide coordinates. Usage: /coordconv <x> <z> [overworld|nether]");
            return true;
        }
        Player player = (Player) sender;
        int x = (int) player.getLocation().getX();
        int y = (int) player.getLocation().getY();
        int z = (int) player.getLocation().getZ();
        String facing = formatFacing(player);

        switch (player.getWorld().getEnvironment()) {
            case THE_END:
                sender.sendMessage(PREFIX + ChatColor.LIGHT_PURPLE + "End: "
                        + ChatColor.WHITE + x + ", " + y + ", " + z
                        + " " + ChatColor.GRAY + "(Facing: " + facing + ")");
                break;
            case NORMAL:
                sender.sendMessage(PREFIX + ChatColor.RED + "Nether: "
                        + ChatColor.WHITE + (x / 8) + ", " + y + ", " + (z / 8)
                        + " " + ChatColor.GRAY + "(Facing: " + facing + ")");
                break;
            case NETHER:
            default:
                sender.sendMessage(PREFIX + ChatColor.GREEN + "Overworld: "
                        + ChatColor.WHITE + (x * 8) + ", " + y + ", " + (z * 8)
                        + " " + ChatColor.GRAY + "(Facing: " + facing + ")");
                break;
        }
        return true;
    }

    /**
     * Manual coord mode: /coordconv <x> <z> [overworld|nether]
     * The optional dim arg specifies the TARGET dimension:
     *   overworld → multiply by 8 (treating input as nether coords)
     *   nether    → divide by 8   (treating input as overworld coords)
     * With no dim arg, auto-detects from the player's current dimension.
     */
    private boolean handleManualConvert(CommandSender sender, String[] args) {
        int x, z;
        try {
            x = Integer.parseInt(args[0]);
            z = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Coordinates must be integers.");
            return true;
        }

        String facing = (sender instanceof Player) ? formatFacing((Player) sender) : null;
        String dimArg = args.length >= 3 ? args[2].toLowerCase() : null;

        if (dimArg == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(PREFIX + ChatColor.RED + "Console must specify a dimension. Usage: /coordconv <x> <z> [overworld|nether]");
                return true;
            }
            World.Environment env = ((Player) sender).getWorld().getEnvironment();
            if (env == World.Environment.NORMAL) {
                sendResult(sender, "Nether", ChatColor.RED, x / 8, z / 8, facing);
            } else if (env == World.Environment.NETHER) {
                sendResult(sender, "Overworld", ChatColor.GREEN, x * 8, z * 8, facing);
            } else {
                sender.sendMessage(PREFIX + ChatColor.RED + "Cannot auto-detect from the End. Specify 'overworld' or 'nether'.");
            }
        } else if (dimArg.equals("overworld")) {
            sendResult(sender, "Overworld", ChatColor.GREEN, x * 8, z * 8, facing);
        } else if (dimArg.equals("nether")) {
            sendResult(sender, "Nether", ChatColor.RED, x / 8, z / 8, facing);
        } else {
            sender.sendMessage(PREFIX + ChatColor.RED + "Invalid dimension '" + dimArg + "'. Use 'overworld' or 'nether'.");
        }
        return true;
    }

    private void sendResult(CommandSender sender, String label, ChatColor labelColor, int x, int z, String facing) {
        String msg = PREFIX + labelColor + label + ": " + ChatColor.WHITE + x + ", " + z;
        if (facing != null) {
            msg += " " + ChatColor.GRAY + "(Facing: " + facing + ")";
        }
        sender.sendMessage(msg);
    }

    private String formatFacing(Player player) {
        String raw = player.getFacing().name(); // e.g. "NORTH"
        return raw.charAt(0) + raw.substring(1).toLowerCase(); // "North"
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 3) {
            return Arrays.asList("overworld", "nether");
        }
        return Collections.emptyList();
    }
}
