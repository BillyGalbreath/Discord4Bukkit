package net.pl3x.bukkit.discord4bukkit.configuration;

import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Lang {
    public static String COMMAND_NO_PERMISSION;
    public static String MINECRAFT_CHAT_FORMAT;
    public static String SERVER_ONLINE;
    public static String SERVER_OFFLINE;
    public static String ADVANCEMENT_ICON_TASK;
    public static String ADVANCEMENT_ICON_GOAL;
    public static String ADVANCEMENT_ICON_CHALLENGE;
    public static String ADVANCEMENT_FORMAT;


    public static void reload() {
        Plugin plugin = D4BPlugin.getInstance();
        String langFile = Config.LANGUAGE_FILE;
        File configFile = new File(plugin.getDataFolder(), langFile);
        if (!configFile.exists()) {
            plugin.saveResource(Config.LANGUAGE_FILE, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        COMMAND_NO_PERMISSION = config.getString("command-no-permission", "&cYou do not have permission for that command!");
        MINECRAFT_CHAT_FORMAT = config.getString("minecraft-chat-format", "<&7[&bD&7]&r {displayname}> {message}");
        SERVER_ONLINE = config.getString("server-online", ":<a:online:579218931495993354> **Server is online!**");
        SERVER_OFFLINE = config.getString("server-offline", "<a:offline:579218899493715971> **Server is offline!**");
        ADVANCEMENT_ICON_TASK = config.getString("advancement.icon.task", ":medal:");
        ADVANCEMENT_ICON_GOAL = config.getString("advancement.icon.goal", ":trophy:");
        ADVANCEMENT_ICON_CHALLENGE = config.getString("advancement.icon.challenge", ":military_medal:");
        ADVANCEMENT_FORMAT = config.getString("advancement.format", "{icon} {player} has completed the {type}, **[**{title}**]**!```{title}:\n  {description}```");
    }

    /**
     * Sends a message to a recipient
     *
     * @param recipient Recipient of message
     * @param message   Message to send
     */
    public static void send(CommandSender recipient, String message) {
        if (recipient != null) {
            for (String part : colorize(message).split("\n")) {
                if (part != null && !part.isEmpty()) {
                    recipient.sendMessage(part);
                }
            }
        }
    }

    /**
     * Broadcast a message to server
     *
     * @param message Message to broadcast
     */
    public static void broadcast(String message) {
        for (String part : colorize(message).split("\n")) {
            if (part != null && !part.isEmpty()) {
                Bukkit.broadcastMessage(part);
            }
        }
    }

    /**
     * Colorize a String
     *
     * @param str String to colorize
     * @return Colorized String
     */
    public static String colorize(String str) {
        if (str == null) {
            return "";
        }
        str = ChatColor.translateAlternateColorCodes('&', str);
        if (ChatColor.stripColor(str).isEmpty()) {
            return "";
        }
        return str;
    }
}
