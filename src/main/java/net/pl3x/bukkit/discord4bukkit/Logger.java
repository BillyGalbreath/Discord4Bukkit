package net.pl3x.bukkit.discord4bukkit;

import net.pl3x.bukkit.discord4bukkit.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {
    public static void debug(String str) {
        if (Config.DEBUG_MODE) {
            log("&3[&aDEBUG&3]&d " + str);
        }
    }

    public static void info(String str) {
        log("&e" + str);
    }

    public static void warn(String str) {
        log("&6" + str);
    }

    public static void error(String str) {
        log("&c" + str);
    }

    private static void log(String str) {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        "&e[&3" + D4BPlugin.getInstance().getName() + "&e]&r " + str + "&r"));
    }
}
