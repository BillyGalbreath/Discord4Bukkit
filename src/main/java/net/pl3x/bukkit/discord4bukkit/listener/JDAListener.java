package net.pl3x.bukkit.discord4bukkit.listener;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import net.pl3x.bukkit.discord4bukkit.Logger;
import net.pl3x.bukkit.discord4bukkit.configuration.Config;
import net.pl3x.bukkit.discord4bukkit.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class JDAListener extends ListenerAdapter {
    private final D4BPlugin plugin;

    public JDAListener(D4BPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReady(ReadyEvent event) {
        Logger.debug("Discord: Connected");
        TextChannel channel = event.getJDA().getTextChannelById(Config.CONSOLE_CHANNEL);
        if (channel != null) {
            plugin.getBot().setConsoleChannel(channel);
        } else {
            Logger.error("Could not register console channel!");
        }

        channel = event.getJDA().getTextChannelById(Config.CHAT_CHANNEL);
        if (channel != null) {
            plugin.getBot().setChatChannel(channel);
            plugin.getBot().sendMessageToDiscord(Lang.SERVER_ONLINE);
        } else {
            Logger.error("Could not register chat channel!");
        }
    }

    @Override
    public void onResume(ResumedEvent event) {
        Logger.debug("Discord: Resumed connection");
        setChannel(event.getJDA());
    }

    @Override
    public void onReconnect(ReconnectedEvent event) {
        Logger.debug("Discord: Re-connected");
        setChannel(event.getJDA());
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        Logger.debug("Discord: Disconnected");
        setChannel(null);
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        Logger.debug("Discord: Shutting down");
        setChannel(null);
    }

    private void setChannel(JDA jda) {
        TextChannel channel = jda != null ? jda.getTextChannelById(Config.CHAT_CHANNEL) : null;
        plugin.getBot().setChatChannel(channel);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor() == event.getJDA().getSelfUser()) {
            return; // dont echo
        }
        if (event.isWebhookMessage()) {
            return; // do not listen to webhooks
        }
        if (event.getMessage().getChannel().getId().equals(Config.CHAT_CHANNEL)) {
            String content = event.getMessage().getContentRaw();
            if (content.startsWith("!") && content.length() > 1) {
                String[] split = content.split(" ");
                String command = split[0].substring(1).toLowerCase();
                String[] args = Arrays.copyOfRange(split, 1, split.length);
                plugin.getBot().handleCommand(event.getAuthor().getName(), command, args);
            } else {
                plugin.getBot().sendMessageToMinecraft(Lang.MINECRAFT_CHAT_FORMAT
                        .replace("{displayname}", event.getMember().getEffectiveName())
                        .replace("{message}", event.getMessage().getContentDisplay()));
            }
        } else if (event.getMessage().getChannel().getId().equals(Config.CONSOLE_CHANNEL)) {
            if (event.getAuthor() == null || event.getAuthor().getId() == null || plugin.getBot().getClient().getSelfUser().getId() == null || event.getAuthor().getId().equals(plugin.getBot().getClient().getSelfUser().getId())) {
                return;
            }
            new BukkitRunnable() {
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event.getMessage().getContentRaw());
                }
            }.runTask(plugin);
        }
    }
}
