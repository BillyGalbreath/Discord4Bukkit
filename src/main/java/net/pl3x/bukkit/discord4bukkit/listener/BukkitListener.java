package net.pl3x.bukkit.discord4bukkit.listener;

import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitListener implements Listener {
    private final D4BPlugin plugin;

    public BukkitListener(D4BPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getBot().sendMessageToDiscord(":heavy_plus_sign: **" + event.getJoinMessage() + "**");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getBot().sendMessageToDiscord(":heavy_minus_sign: **" + event.getQuitMessage() + "**");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.getBot().sendMessageToDiscord(":skull: **" + event.getDeathMessage() + "**");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isAsynchronous()) {
            new BukkitRunnable() {
                public void run() {
                    plugin.getBot().sendMessageToDiscord(event.getPlayer(), event.getMessage());
                }
            }.runTaskAsynchronously(plugin);
        } else {
            plugin.getBot().sendMessageToDiscord(event.getPlayer(), event.getMessage());
        }
    }
}
