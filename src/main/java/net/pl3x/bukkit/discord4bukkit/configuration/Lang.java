package net.pl3x.bukkit.discord4bukkit.configuration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Lang {
    public static String COMMAND_NO_PERMISSION;
    public static String DISCORD_CHAT_FORMAT;
    public static String MINECRAFT_CHAT_FORMAT;

    public static void reload(Plugin plugin) {
        String langFile = Config.LANGUAGE_FILE;
        File configFile = new File(plugin.getDataFolder(), langFile);
        if (!configFile.exists()) {
            plugin.saveResource(Config.LANGUAGE_FILE, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        COMMAND_NO_PERMISSION = config.getString("command-no-permission", "&cYou do not have permission for that command!");
        DISCORD_CHAT_FORMAT = config.getString("discord-chat-format", "{displayname}: {message}");
        MINECRAFT_CHAT_FORMAT = config.getString("minecraft-chat-format", "<[D] {displayname}> {message}");
    }

    public static void send(CommandSender recipient, String message) {
        if (message == null) {
            return; // do not send blank messages
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (ChatColor.stripColor(message).isEmpty()) {
            return; // do not send blank messages
        }

        for (String part : message.split("\n")) {
            recipient.sendMessage(part);
        }
    }

    public static void broadcast(String message) {
        if (message == null) {
            return; // do not send blank messages
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (ChatColor.stripColor(message).isEmpty()) {
            return; // do not send blank messages
        }
        for (String part : message.split("\n")) {
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(part));
            Bukkit.getConsoleSender().sendMessage(part);
        }
    }
}
