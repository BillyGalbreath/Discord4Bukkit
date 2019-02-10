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
import net.pl3x.bukkit.discord4bukkit.configuration.Config;
import net.pl3x.bukkit.discord4bukkit.configuration.Lang;

public class JDAListener extends ListenerAdapter {
    private final D4BPlugin plugin;

    public JDAListener(D4BPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReady(ReadyEvent event) {
        plugin.getLogger().debug("Discord: Connected");

        TextChannel channel = event.getJDA().getTextChannelById(Config.CHANNEL);
        if (channel != null) {
            plugin.getBot().setChannel(channel);
        } else {
            plugin.getLogger().error("Could not register channel!");
        }
    }

    public void onResume(ResumedEvent event) {
        plugin.getLogger().debug("Discord: Resumed connection");
        setChannel(event.getJDA());
    }

    public void onReconnect(ReconnectedEvent event) {
        plugin.getLogger().debug("Discord: Re-connected");
        setChannel(event.getJDA());
    }

    public void onDisconnect(DisconnectEvent event) {
        plugin.getLogger().debug("Discord: Disconnected");
        setChannel(null);
    }

    public void onShutdown(ShutdownEvent event) {
        plugin.getLogger().debug("Discord: Shutting down");
        setChannel(null);
    }

    private void setChannel(JDA jda) {
        TextChannel channel = jda != null ? jda.getTextChannelById(Config.CHANNEL) : null;
        plugin.getBot().setChannel(channel);
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor() == event.getJDA().getSelfUser()) {
            return; // dont echo
        }
        if (event.isWebhookMessage()) {
            return; // do not listen to webhooks
        }
        if (event.getMessage().getChannel().getId().equals(Config.CHANNEL)) {
            plugin.getBot().sendMessageToMinecraft(Lang.MINECRAFT_CHAT_FORMAT
                    .replace("{displayname}", event.getAuthor().getName())
                    .replace("{message}", event.getMessage().getContentDisplay()));
        }
    }
}
