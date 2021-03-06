package net.pl3x.bukkit.discord4bukkit.command.bukkit;

import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import net.pl3x.bukkit.discord4bukkit.configuration.Config;
import net.pl3x.bukkit.discord4bukkit.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CmdDiscord4Bukkit implements TabExecutor {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("command.discord4bukkit")) {
            if (args.length == 1) {
                return Stream.of("reload", "start", "stop", "broadcast")
                        .filter(arg -> arg.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("command.discord4bukkit")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        D4BPlugin plugin = D4BPlugin.getInstance();
        String response = "&d" + plugin.getName() + " v" + plugin.getDescription().getVersion();

        if (args.length == 0) {
            Lang.send(sender, response);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            Config.reload();
            Lang.reload();

            response += " reloaded.";
            Lang.send(sender, response);
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            plugin.getBot().connect();
            Lang.send(sender, "Bot started");
            return true;
        }

        if (args[0].equalsIgnoreCase("stop")) {
            plugin.getBot().disconnect();
            Lang.send(sender, "Bot stopped");
            return true;
        }

        if (args[0].equalsIgnoreCase("broadcast")) {
            if (args.length < 2) {
                return false;
            }
            plugin.getBot().sendMessageToDiscord(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
            return true;
        }

        return false;
    }
}
