package net.pl3x.bukkit.discord4bukkit.listener;

import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SuperVanishListener implements Listener {
    private final D4BPlugin plugin;

    public SuperVanishListener(D4BPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanish(PlayerHideEvent event) {
        plugin.getBot().sendMessageToDiscord(":heavy_minus_sign: **{player} left the game**"
                .replace("{player}", event.getPlayer().getName()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUnvanish(PlayerShowEvent event) {
        plugin.getBot().sendMessageToDiscord(":heavy_plus_sign: **{player} joined the game**"
                .replace("{player}", event.getPlayer().getName()));
    }
}
