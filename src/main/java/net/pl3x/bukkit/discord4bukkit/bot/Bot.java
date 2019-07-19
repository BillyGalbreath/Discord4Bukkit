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
import net.pl3x.bukkit.discord4bukkit.task.ConsoleMessageQueueWorker;
import net.pl3x.bukkit.discord4bukkit.util.WebhookUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import javax.security.auth.login.LoginException;
import java.util.Collection;
import java.util.stream.Collectors;

public class Bot {
    private final D4BPlugin plugin;

    private JDA client = null;
    private TextChannel chatChannel;
    private TextChannel consoleChannel;

    public Bot(D4BPlugin plugin) {
        this.plugin = plugin;
    }

    public JDA getClient() {
        return client;
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
        ConsoleMessageQueueWorker.QUEUE.clear();
        if (client != null) {
            plugin.getBot().sendMessageToDiscord(Lang.SERVER_OFFLINE, true);
            chatChannel = null;
            consoleChannel = null;
            JDA jda = client;
            client = null;
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignore) {
                }
                jda.shutdownNow();
            }).start();
        }
    }

    public void setChatChannel(TextChannel chatChannel) {
        if (chatChannel == null) {
            Logger.debug("Disconnecting from discord chat channel");
        } else {
            Logger.debug("Registering chat channel: " + chatChannel.getName() + " (" + chatChannel.getId() + ")");
        }
        this.chatChannel = chatChannel;
    }

    public void setConsoleChannel(TextChannel consoleChannel) {
        if (consoleChannel == null) {
            Logger.debug("Disconnecting from discord console channel");
        } else {
            Logger.debug("Registering console channel: " + consoleChannel.getName() + " (" + consoleChannel.getId() + ")");
        }
        this.consoleChannel = consoleChannel;
    }

    public TextChannel getConsoleChannel() {
        return consoleChannel;
    }

    public void sendMessageToConsole(String message) {
        sendMessageToDiscord(consoleChannel, message, false);
    }

    public void sendMessageToDiscord(String message) {
        sendMessageToDiscord(message, false);
    }

    public void sendMessageToDiscord(String message, boolean blocking) {
        sendMessageToDiscord(chatChannel, message, blocking);
    }

    private void sendMessageToDiscord(TextChannel channel, String message, boolean blocking) {
        if (client == null) {
            return;
        }

        if (channel == null) {
            return;
        }

        if (message == null) {
            return;
        }

        message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));

        if (message.isEmpty()) {
            return;
        }

        try {
            if (blocking) {
                try {
                    channel.sendMessage(message).complete(false);
                } catch (Exception ignore) {
                }
            } else {
                channel.sendMessage(message).queue();
            }
        } catch (Exception e) {
            System.out.println("caught some exception");
        }
    }

    public void sendMessageToDiscord(Player player, String message) {
        WebhookUtil.sendMessageToDiscord(this, player, message);
    }

    public void sendMessageToMinecraft(String message) {
        Lang.broadcast(message);
    }

    public void handleCommand(String sender, String command, String[] args) {
        // TODO this is just a quick and stupid impl to get going
        if (command.equals("list") || command.equals("playerlist")) {
            Collection<? extends Player> online = Bukkit.getOnlinePlayers().stream()
                    .filter(player -> !isVanished(player)).collect(Collectors.toList());
            sendMessageToDiscord(String.format("**Players Online:** (%s out of %s) \n%s",
                    online.size(),
                    Bukkit.getMaxPlayers(),
                    online.stream()
                            .map(HumanEntity::getName)
                            .collect(Collectors.joining(", "))));
            return;
        }
        if (command.equals("tps")) {
            double[] tps = Bukkit.getTPS();
            sendMessageToDiscord("TPS from last 1m, 5m, 15m: "
                    + String.format("%.2f", tps[0]) + " "
                    + String.format("%.2f", tps[1]) + " "
                    + String.format("%.2f", tps[2]));
        }
    }

    private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}
