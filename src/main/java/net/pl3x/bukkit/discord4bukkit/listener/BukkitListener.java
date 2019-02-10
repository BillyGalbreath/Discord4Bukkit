package net.pl3x.bukkit.discord4bukkit.listener;

import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import net.pl3x.bukkit.discord4bukkit.configuration.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {
    private final D4BPlugin plugin;

    public BukkitListener(D4BPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getBot().sendMessageToDiscord("*" + event.getJoinMessage() + "*");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getBot().sendMessageToDiscord("*" + event.getQuitMessage() + "*");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.getBot().sendMessageToDiscord("*" + event.getDeathMessage() + "*");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        plugin.getBot().sendMessageToDiscord(Lang.DISCORD_CHAT_FORMAT
                .replace("{player}", event.getPlayer().getName())
                .replace("{displayname}", event.getPlayer().getDisplayName())
                .replace("{message}", event.getMessage()));
    }
}
