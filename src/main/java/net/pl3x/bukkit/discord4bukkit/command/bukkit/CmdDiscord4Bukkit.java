package net.pl3x.bukkit.discord4bukkit.command.bukkit;

import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import net.pl3x.bukkit.discord4bukkit.configuration.Config;
import net.pl3x.bukkit.discord4bukkit.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;

public class CmdDiscord4Bukkit implements TabExecutor {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("command.discord4bukkit")) {
            return Collections.singletonList("reload");
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

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            Config.reload();
            Lang.reload();

            response += " reloaded.";
        }

        Lang.send(sender, response);
        return true;
    }
}
