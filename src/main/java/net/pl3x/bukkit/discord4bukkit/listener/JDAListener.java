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

public class JDAListener extends ListenerAdapter {
    private final D4BPlugin plugin;

    public JDAListener(D4BPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReady(ReadyEvent event) {
        Logger.debug("Discord: Connected");
        TextChannel channel = event.getJDA().getTextChannelById(Config.CHANNEL);
        if (channel != null) {
            plugin.getBot().setChannel(channel);
            plugin.getBot().sendMessageToDiscord(Lang.SERVER_ONLINE);
        } else {
            Logger.error("Could not register channel!");
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
        TextChannel channel = jda != null ? jda.getTextChannelById(Config.CHANNEL) : null;
        plugin.getBot().setChannel(channel);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor() == event.getJDA().getSelfUser()) {
            return; // dont echo
        }
        if (event.isWebhookMessage()) {
            return; // do not listen to webhooks
        }
        if (event.getMessage().getChannel().getId().equals(Config.CHANNEL)) {
            String content = event.getMessage().getContentRaw();
            if (content.startsWith("!") && content.length() > 1) {
                plugin.getBot().handleCommand(event.getAuthor().getName(), content);
            } else {
                plugin.getBot().sendMessageToMinecraft(Lang.MINECRAFT_CHAT_FORMAT
                        .replace("{displayname}", event.getMember().getEffectiveName())
                        .replace("{message}", event.getMessage().getContentDisplay()));
            }
        }
    }
}
