package net.pl3x.bukkit.discord4bukkit.util;

import net.pl3x.bukkit.discord4bukkit.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

public class Logger extends PluginLogger {
    private final String prefix;

    public Logger(Plugin plugin) {
        super(plugin);
        this.prefix = plugin.getName();
    }

    public void debug(String str) {
        if (Config.DEBUG_MODE) {
            log("&3[&aDEBUG&3]&d " + str);
        }
    }

    public void info(String str) {
        log("&e" + str);
    }

    public void warn(String str) {
        log("&6" + str);
    }

    public void error(String str) {
        log("&c" + str);
    }

    private void log(String str) {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        "&e[&3" + prefix + "&e]&r " + str + "&r"));
    }
}
