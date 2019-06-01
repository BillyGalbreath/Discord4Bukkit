package net.pl3x.bukkit.discord4bukkit.configuration;

import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
    public static boolean DEBUG_MODE;
    public static String LANGUAGE_FILE;
    public static String BOT_TOKEN;
    public static String CHAT_CHANNEL;
    public static String CONSOLE_CHANNEL;

    public static void reload() {
        Plugin plugin = D4BPlugin.getInstance();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        DEBUG_MODE = config.getBoolean("debug-mode", false);
        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");
        BOT_TOKEN = config.getString("bot-token", "");
        CHAT_CHANNEL = config.getString("channel.chat", "000000000000000000");
        CONSOLE_CHANNEL = config.getString("channel.console", "000000000000000000");
    }
}
