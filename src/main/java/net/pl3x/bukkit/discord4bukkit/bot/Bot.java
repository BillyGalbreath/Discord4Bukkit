package net.pl3x.bukkit.discord4bukkit.bot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import net.pl3x.bukkit.discord4bukkit.Logger;
import net.pl3x.bukkit.discord4bukkit.configuration.Config;
import net.pl3x.bukkit.discord4bukkit.configuration.Lang;
import net.pl3x.bukkit.discord4bukkit.listener.JDAListener;
import org.bukkit.ChatColor;

import javax.security.auth.login.LoginException;

public class Bot {
    private final D4BPlugin plugin;

    private JDA client = null;
    private TextChannel channel;

    public Bot(D4BPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        disconnect();
        try {
            client = new JDABuilder(AccountType.BOT)
                    .setAudioEnabled(false)
                    .setAutoReconnect(true)
                    .setBulkDeleteSplittingEnabled(false)
                    .setToken(Config.BOT_TOKEN)
                    .addEventListener(new JDAListener(plugin))
                    .build();
        } catch (LoginException e) {
            client = null;
        }
    }

    public void disconnect() {
        if (client != null) {
            plugin.getBot().sendMessageToDiscord(Lang.SERVER_OFFLINE, true);
            client.shutdownNow();
            client = null;
        }
    }

    public void setChannel(TextChannel channel) {
        if (channel == null) {
            Logger.debug("Disconnecting from discord channel");
        } else {
            Logger.debug("Registering channel: " + channel.getName() + " (" + channel.getId() + ")");
        }
        this.channel = channel;
    }

    public void sendMessageToDiscord(String message) {
        sendMessageToDiscord(message, false);
    }

    public void sendMessageToDiscord(String message, boolean blocking) {
        if (client == null) {
            Logger.error("Message delivery failed. Bot is not connected");
            return;
        }

        if (channel == null) {
            Logger.debug("Message delivery failed. Channel not registered");
            return;
        }

        if (message == null) {
            Logger.debug("Message delivery failed. No message to send");
            return;
        }

        message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));

        if (message.isEmpty()) {
            Logger.debug("Message delivery failed. Message is empty");
            return;
        }

        if (blocking) {
            try {
                channel.sendMessage(message).complete(false);
            } catch (Exception ignore) {
            }
        } else {
            channel.sendMessage(message).queue();
        }
    }

    public void sendMessageToMinecraft(String message) {
        Lang.broadcast(message);
    }
}
